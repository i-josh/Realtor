package com.ihiabe.josh.realtor.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ihiabe.josh.realtor.R
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        val auth = FirebaseAuth.getInstance()

        reset_email_button.setOnClickListener {
            val email = reset_email.text.toString()
            if (reset_email.text!!.isEmpty()){
                reset_email.error = "enter verified email"
            }else{
                auth.sendPasswordResetEmail(email).addOnCompleteListener{task->
                    if (task.isSuccessful){
                        Toast.makeText(this@ForgotPassword,"password reset link sent",
                            Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@ForgotPassword,
                            SignInActivity::class.java))
                        finish()
                    }else
                        Toast.makeText(this@ForgotPassword,
                            "couldn't send password reset",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
