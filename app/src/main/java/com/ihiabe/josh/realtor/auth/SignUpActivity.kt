package com.ihiabe.josh.realtor.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import com.ihiabe.josh.realtor.R
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        sign_up_button.setOnClickListener {
            if (validateSignUpForm())
                startVerifyPhone()
        }
        sign_in_text.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }
    }
    private fun validateSignUpForm(): Boolean {
        val editTexts = arrayOf<TextInputEditText>(sign_up_full_name,sign_up_email,
            sign_up_password,sign_up_re_password,sign_up_phone_number,sign_up_birth_year)

        for (et in editTexts){
            if(et.text!!.isEmpty()) {
                et.error = "required field"
                return false
            }
        }

        if (editTexts[2].text.toString() != editTexts[3].text.toString()){
            editTexts[3].error = "password does not match"
            return false
        }

        if (editTexts[2].text.toString().length < 6){
            editTexts[2].error = "enter at least 6 characters"
            return false
        }

        if (editTexts[4].text.toString().length < 11){
            editTexts[4].error = "enter a valid phone number"
            return false
        }

        val year = Calendar.getInstance().get(Calendar.YEAR)
        val isOfAge = year - editTexts[5].text.toString().toInt() > 18

        if (!isOfAge){
            editTexts[5].error = "not up to 18, can't sign up"
            return false
        }
        return true
    }

    private fun startVerifyPhone(){
        val fullName = sign_up_full_name.text.toString()
        val email = sign_up_email.text.toString()
        val password = sign_up_password.text.toString()
        val phoneNumber = sign_up_phone_number.text.toString()
        val birthYear = sign_up_birth_year.text.toString()

        val intent = Intent(this,VerifyEmail::class.java)
        intent.putExtra(FULL_NAME,fullName)
        intent.putExtra(EMAIL,email)
        intent.putExtra(PASSWORD,password)
        intent.putExtra(PHONE_NUMBER,phoneNumber)
        intent.putExtra(BIRTH_YEAR,birthYear)

        startActivity(intent)
        finish()
    }

    companion object{
        const val FULL_NAME = "com.ihiabe.josh.realtor.auth.FULL_NAME"
        const val EMAIL = "com.ihiabe.josh.realtor.auth.EMAIL"
        const val PASSWORD = "com.ihiabe.josh.realtor.auth.PASSWORD"
        const val PHONE_NUMBER = "com.ihiabe.josh.realtor.auth.PHONE_NUMBER"
        const val BIRTH_YEAR = "com.ihiabe.josh.realtor.auth.BIRTH_YEAR"
    }
}
