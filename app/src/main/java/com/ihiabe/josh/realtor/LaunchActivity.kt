package com.ihiabe.josh.realtor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ihiabe.josh.realtor.auth.SignUpActivity
import com.ihiabe.josh.realtor.ui.RentActivity
import kotlinx.android.synthetic.main.activity_launch.*

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        launch_rent_button.setOnClickListener {
            startActivity(Intent(this, RentActivity::class.java)) }

        launch_rent_out_button.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
