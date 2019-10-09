package com.ihiabe.josh.realtor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sign_in_button.setOnClickListener {
            startActivity(Intent(this,RentActivity::class.java))
        }

        forgot_password_text_view.setOnClickListener {
            startActivity(Intent(this,ForgotPassword::class.java))
        }
    }
}
