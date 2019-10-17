package com.ihiabe.josh.realtor.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_verify_email.*
import com.ihiabe.josh.realtor.R
import com.ihiabe.josh.realtor.model.User


class VerifyEmail : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)
        auth = FirebaseAuth.getInstance()
        val databaseReference = FirebaseDatabase.getInstance().reference

        val phoneNumber = intent.getStringExtra(SignUpActivity.PHONE_NUMBER)
        val fullName = intent.getStringExtra(SignUpActivity.FULL_NAME)
        val email = intent.getStringExtra(SignUpActivity.EMAIL)
        val birthYear = intent.getStringExtra(SignUpActivity.BIRTH_YEAR)
        val password = intent.getStringExtra(SignUpActivity.PASSWORD)
        val newUser = User(fullName, email, phoneNumber, birthYear)

        verify_email.text = email

        verify_phone_fab.setOnClickListener {
            progressBar2.visibility = View.VISIBLE
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {task->
                if (task.isSuccessful){
                    sendVerificationEmail()
                    databaseReference.child("Users").child(auth.uid!!).setValue(newUser)
                    startActivity(Intent(this@VerifyEmail,SignInActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun sendVerificationEmail(){
        val user = auth.currentUser

        user?.sendEmailVerification()?.addOnCompleteListener { task->
            if (task.isSuccessful){
                Toast.makeText(this,"Verification email sent"
                    , Toast.LENGTH_SHORT).show()
                auth.signOut()
            }else
                Toast.makeText(this,"couldn't send verification email"
                    , Toast.LENGTH_SHORT)
                    .show()
        }
    }
}
