package com.ihiabe.josh.realtor

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.ihiabe.josh.realtor.ui.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val isFirstRun = getSharedPreferences("Preference", Context.MODE_PRIVATE)
                .getBoolean("isFirstRun", true)

            if (isFirstRun){
                startActivity(Intent(this, LaunchActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }


            getSharedPreferences("Preference", Context.MODE_PRIVATE).edit()
                .putBoolean("isFirstRun",false).apply()
        },3000)
    }
}
