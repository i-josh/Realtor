package com.ihiabe.josh.realtor.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ihiabe.josh.realtor.ui.RentActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import com.ihiabe.josh.realtor.R


class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()

        sign_in_button.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email = sign_in_email.text.toString()
            val password = sign_in_password.text.toString()
            if (validateSignInForm())
                signIn(email,password)
        }

        forgot_password_text_view.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
            finish()
        }
        redirect_sign_up_text.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

    }

    private fun checkEmailVerification() {
        val user = auth.currentUser
        val emailFlag = user!!.isEmailVerified

        if (emailFlag) {
            startActivity(Intent(this, RentActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Verify Your Email", Toast.LENGTH_SHORT).show()
            auth.signOut()
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun signIn(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                checkEmailVerification()
            } else {
                sign_in_password.error = "email or password not correct"
                progressBar.visibility = View.INVISIBLE
            }
        }
    }

    private fun validateSignInForm(): Boolean{
        if (sign_in_email.text!!.isEmpty()){
            sign_in_email.error = "email required"
            progressBar.visibility = View.INVISIBLE
            return false
        }
        if (sign_in_password.text!!.isEmpty()){
            sign_in_password.error = "email required"
            progressBar.visibility = View.INVISIBLE
            return false
        }
        return true
    }
}
