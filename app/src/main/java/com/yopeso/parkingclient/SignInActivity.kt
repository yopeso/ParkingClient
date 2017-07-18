package com.yopeso.parkingclient

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.yopeso.parkingclient.model.User

class SignInActivity : BaseActivity(), OnClickListener, OnCompleteListener<AuthResult> {
    private val TAG = "SignInActivity"

    private val RC_SIGN_IN: Int = 1

    private lateinit var mDatabase: DatabaseReference

    private lateinit var mAuth: FirebaseAuth

    private lateinit var mEmailField: EditText
    private lateinit var mPasswordField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Views
        mEmailField = findViewById(R.id.field_email) as EditText
        mPasswordField = findViewById(R.id.field_password) as EditText
        val signInButton = findViewById(R.id.button_sign_in)
        val googleSignInButton = findViewById(R.id.button_google_sign_in)

        // Click listeners
        signInButton.setOnClickListener(this)
        googleSignInButton.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()

        mDatabase = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        mAuth.currentUser?.let { onAuthSuccess(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            handleGoogleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data))
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_sign_in -> signIn()
            R.id.button_google_sign_in -> googleSignIn()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, SignInActivity::class.java)
            return intent
        }
    }

    private fun googleSignIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleGoogleSignInResult(result: GoogleSignInResult) {
        Log.i(TAG, "handleGoogleSignInResult" + result.signInAccount.toString())
        if (result.isSuccess) {
            val googleSignInAccount: GoogleSignInAccount = result.signInAccount!!
            val authCredential: AuthCredential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
            if (isYopesoEmail(googleSignInAccount.email)) {
                mAuth.signInWithCredential(authCredential).addOnCompleteListener(this)
            } else {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                Toast.makeText(this, "You need to use a yopeso account to login", Toast.LENGTH_LONG).show()
            }
        } else {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient)
            Toast.makeText(this, "You need to use a yopeso account to login", Toast.LENGTH_LONG).show()
        }
    }

    override fun onComplete(p0: Task<AuthResult>) {
        if (p0.isSuccessful) {
            onAuthSuccess(mAuth.currentUser!!)
        } else {
            Log.w(TAG, "signInWithCredential:failure", p0.getException())
            Toast.makeText(this, "Authentication with Firebase failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun signIn() {
        Log.d(TAG, "signIn")
        if (!validateForm()) {
            return
        }

        showProgressDialog()
        val email = mEmailField.getText().toString()
        val password = mPasswordField.getText().toString()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnFailureListener { e -> e.printStackTrace() }
            .addOnCompleteListener(this) { task ->
                Log.d(TAG, "signIn:onComplete:" + task.isSuccessful)
                hideProgressDialog()

                    if (task.isSuccessful) {
                        onAuthSuccess(task.result.user)
                    } else {
                        Toast.makeText(this@SignInActivity, "Sign In Failed",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun onAuthSuccess(@NonNull user: FirebaseUser) {
        val username = user.email?.let { usernameFromEmail(it) }

        // Write new user
        user.email?.let { writeNewUser(user.uid, username!!, it) }

        // Go to MainActivity
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()
    }

    private fun usernameFromEmail(@NonNull email: String): String {
        if (email.contains("@")) {
            return email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            return email
        }
    }

    private fun validateForm(): Boolean {
        var result = true
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.error = "Required"
            result = false
        } else {
            mEmailField.error = null
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.error = "Required"
            result = false
        } else {
            mPasswordField.error = null
        }

        return result
    }

    private fun isYopesoEmail(email: String?): Boolean {
        return email!!.contains("@yopeso.com")
    }

    private fun writeNewUser(userId: String, @Nullable name: String, email: String) {
        val user = User(name, email)

        mDatabase.child("users")?.child(userId)?.setValue(user)
    }
}