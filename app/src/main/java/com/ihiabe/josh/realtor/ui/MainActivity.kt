package com.ihiabe.josh.realtor.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ihiabe.josh.realtor.R
import com.ihiabe.josh.realtor.auth.SignInActivity
import com.ihiabe.josh.realtor.model.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_layout.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        navController = Navigation.findNavController(this,
            R.id.nav_host_fragment
        )
        bottom_nav.setupWithNavController(navController)

        val toggle = ActionBarDrawerToggle(this,drawer_layout,toolbar,
            R.string.nav_drawer_open,
            R.string.nav_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        setDrawerUserName()
    }

    private fun setDrawerUserName() {
        if (user != null) {
            val userRef = FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(user!!.uid)

            userRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(User::class.java)
                    val userName = user!!.fullName
                    drawer_header_user_name.text = userName
                }
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(this@MainActivity,p0.message,Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.drawer_home -> startActivity(Intent(this,MainActivity::class.java))
            R.id.drawer_feedback -> sendFeedback()
            R.id.drawer_about -> startActivity(Intent(this,AboutActivity::class.java))
            R.id.drawer_sign_in -> signInUser()
            R.id.drawer_sign_out -> signOutUser()
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOutUser() {
        if (user != null){
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this,"user signed out",Toast.LENGTH_SHORT).show()
            drawer_header_user_name.text = ""
        } else
            Toast.makeText(this,"no user found",Toast.LENGTH_SHORT).show()
    }

    private fun signInUser(){
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null)
            startActivity(Intent(this,SignInActivity::class.java))
        else
            Toast.makeText(this,"already signed in",Toast.LENGTH_SHORT).show()
    }

    private fun sendFeedback(){
        val feedback = Intent(Intent.ACTION_SEND)
        feedback.type = "message/rfc822"
        val s = arrayOf("jihiabe@gmail.com")
        feedback.putExtra(Intent.EXTRA_EMAIL, s)
        val chooser = Intent.createChooser(feedback, "Launch Email")
        startActivity(chooser)
    }
}
