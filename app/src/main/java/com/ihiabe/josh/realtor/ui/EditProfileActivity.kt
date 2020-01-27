package com.ihiabe.josh.realtor.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ihiabe.josh.realtor.R
import com.ihiabe.josh.realtor.model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.lang.Exception

class EditProfileActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var dpRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val toolbar = findViewById<Toolbar>(R.id.edit_profile_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Edit Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val user = FirebaseAuth.getInstance().currentUser
        database = FirebaseDatabase.getInstance()
        userRef = database.reference.child("Users").child(user!!.uid)
        dpRef = FirebaseStorage.getInstance().reference.child("Images")
            .child("Profile Pictures").child(user.uid)

        getExtras()

        frameLayout.setOnClickListener {
            launchGallery()
        }

        setProfilePic()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_profile_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.edit_profile_save -> saveChanges()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveChanges(){
        val extraUsername = edit_user_name.text.toString()
        val extraPhoneNumber = edit_phone_number.text.toString()
        val extraEmail = edit_email.text.toString()
        val userEdit = User(extraUsername,extraEmail,extraPhoneNumber)
        userRef.setValue(userEdit).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(this,"Changes made",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,ProfileActivity::class.java))
                finish()
            }
        }
    }

    private fun getExtras(){
        val extraUsername = intent.getStringExtra(ProfileActivity.EXTRA_USERNAME)
        val extraPhoneNumber = intent.getStringExtra(ProfileActivity.EXTRA_PHONE_NUMBER)
        val extraEmail = intent.getStringExtra(ProfileActivity.EXTRA_EMAIL)

        edit_user_name.append(extraUsername)
        edit_phone_number.append(extraPhoneNumber)
        edit_email.append(extraEmail)
    }

    private fun launchGallery(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"select image"),PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null){
            val imagePath = data.data
            Picasso.with(this).load(imagePath).fit().centerCrop().into(edit_profile_image)
            try {
                dpRef.putFile(imagePath!!).addOnSuccessListener {
                    Toast.makeText(this@EditProfileActivity, "Upload Successful", Toast.LENGTH_SHORT)
                        .show()
                    setProfilePic()
                }
            }catch (e: Exception){
                e.stackTrace
            }
        }
    }

    private fun setProfilePic(){
        dpRef.downloadUrl.addOnSuccessListener {
            Picasso.with(this).load(it).centerCrop().fit().into(edit_profile_image)
        }
    }

    companion object{
        const val PICK_IMAGE = 123
    }
}