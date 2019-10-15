package com.ihiabe.josh.realtor.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.ihiabe.josh.realtor.R
import kotlinx.android.synthetic.main.activity_verify_phone_number.*

class VerifyPhoneNumber : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone_number)

        val auth = FirebaseAuth.getInstance()

        val phoneNumber = intent.getStringExtra(SignUpActivity.PHONE_NUMBER)
        verify_phone_number.text = phoneNumber

        verify_phone_fab.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}
