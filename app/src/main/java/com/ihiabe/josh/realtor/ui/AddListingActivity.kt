package com.ihiabe.josh.realtor.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.ihiabe.josh.realtor.R
import kotlinx.android.synthetic.main.activity_add_listing.*
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ihiabe.josh.realtor.adapter.ImageGridAdapter
import com.ihiabe.josh.realtor.model.Listing
import com.ihiabe.josh.realtor.model.User
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.PicassoAdapter
import com.sangcomz.fishbun.define.Define

class AddListingActivity : AppCompatActivity() {
    private lateinit var listingReference: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var imagesReference: StorageReference
    private lateinit var dpRef: StorageReference
    private lateinit var pushId: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_listing)

        val toolbar = findViewById<Toolbar>(R.id.add_listing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Add Listing"

        val spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.listing_category,
            android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listing_category.adapter = spinnerAdapter

        storageReference = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()
        userRef = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(auth.currentUser!!.uid)

        listingReference = FirebaseDatabase.getInstance().reference
            .child("Listing").push()
        pushId = listingReference.key!!

        imagesReference = storageReference
            .child("Images")
            .child("Listing Images")
            .child(pushId)

        dpRef = storageReference
            .child("Images")
            .child("Profile Pictures")
            .child(auth.currentUser!!.uid)

        add_images_button.setOnClickListener {
            addImageFromGallery()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_listing_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.submit_listing -> submit()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addImageFromGallery() {
        FishBun.with(this).setImageAdapter(PicassoAdapter())
            .setActionBarColor(Color.WHITE, Color.WHITE)
            .setActionBarTitleColor(Color.parseColor("#448aff"))
            .setHomeAsUpIndicatorDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_arrow_back
                )
            )
            .setDoneButtonDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_done
                )
            )
            .startAlbum()
    }

    private fun submit(){
        if (validate()) {
            userRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    val user = p0.getValue(User::class.java)
                    val userName = user!!.fullName
                    val userPhoneNumber = user.phoneNumber
                    val userId = auth.currentUser!!.uid
                    val location = add_listing_location.text.toString()
                    val description = listing_category.selectedItem.toString()
                    val price = add_listing_price.text.toString().toLong()
                    val images = mutableListOf<String>()
                    images.run {
                        imagesReference.child("image 1").downloadUrl.addOnSuccessListener {
                            dpRef.downloadUrl.addOnSuccessListener {dp->
                                val profilePic = dp.toString()
                                val listing = Listing(pushId,userId,userName,userPhoneNumber,profilePic,location,description,price,images)
                                listingReference.setValue(listing).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(applicationContext, "listing added"
                                            , Toast.LENGTH_SHORT)
                                            .show()
                                        startActivity(Intent(applicationContext, MainActivity::class.java))
                                        finish()
                                    }
                                }
                            }
                            add(it.toString())
                        }
                    }
                    dpRef.downloadUrl.addOnSuccessListener {
                        val profilePic = it.toString()
                        add(images, userId, userName, userPhoneNumber, profilePic, location, description, price)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }
    }

    private fun add(
        images: MutableList<String>,
        userId: String,
        userName: String,
        userPhoneNumber: String,
        userImage: String,
        location: String,
        description: String,
        price: Long
    ) {
        images.run {
            imagesReference.child("image 2").downloadUrl.addOnSuccessListener {
                add(it.toString())
                val listing = Listing(pushId,userId, userName, userPhoneNumber, userImage, location, description, price, images)
                listingReference.setValue(listing)
            }
        }
        images.run {
            imagesReference.child("image 3").downloadUrl.addOnSuccessListener {
                add(it.toString())
                val listing = Listing(pushId,userId, userName, userPhoneNumber, userImage, location, description, price, images)
                listingReference.setValue(listing)
            }
        }
        images.run {
            imagesReference.child("image 4").downloadUrl.addOnSuccessListener {
                add(it.toString())
                val listing = Listing(pushId,userId, userName, userPhoneNumber, userImage, location, description, price, images)
                listingReference.setValue(listing)
            }
        }
        images.run {
            imagesReference.child("image 5").downloadUrl.addOnSuccessListener {
                add(it.toString())
                val listing = Listing(pushId,userId, userName, userPhoneNumber, userImage, location, description, price, images)
                listingReference.setValue(listing)
            }
        }
        images.run {
            imagesReference.child("image 6").downloadUrl.addOnSuccessListener {
                add(it.toString())
                val listing = Listing(pushId,userId, userName, userPhoneNumber, userImage, location, description, price, images)
                listingReference.setValue(listing)
            }
        }
        images.run {
            imagesReference.child("image 7").downloadUrl.addOnSuccessListener {
                add(it.toString())
                val listing = Listing(pushId,userId, userName, userPhoneNumber, userImage, location, description, price, images)
                listingReference.setValue(listing)
            }
        }
        images.run {
            imagesReference.child("image 8").downloadUrl.addOnSuccessListener {
                add(it.toString())
                val listing = Listing(pushId,userId, userName, userPhoneNumber, userImage, location, description, price, images)
                listingReference.setValue(listing)
            }
        }
        images.run {
            imagesReference.child("image 9").downloadUrl.addOnSuccessListener {
                add(it.toString())
                val listing = Listing(pushId,userId, userName, userPhoneNumber, userImage, location, description, price, images)
                listingReference.setValue(listing)
            }
        }
        images.run {
            imagesReference.child("image 10").downloadUrl.addOnSuccessListener {
                add(it.toString())
                val listing = Listing(pushId,userId, userName, userPhoneNumber, userImage, location, description, price, images)
                listingReference.setValue(listing)
            }
        }
    }

    private fun validate(): Boolean {
        if (add_listing_location.text!!.isEmpty()) {
            add_listing_location.error = "location required"
            return false
        }
        if (add_listing_price.text!!.isEmpty()) {
            add_listing_price.error = "price required"
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Define.ALBUM_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                val imagePaths: List<Uri> = data!!.getParcelableArrayListExtra(Define.INTENT_PATH)
                val adapter = ImageGridAdapter(this, imagePaths)
                listing_image_grid.numColumns = 3
                listing_image_grid.adapter = adapter

                var count = 0
                for (image in imagePaths) {
                    count++
                    progressBar3.visibility =  View.VISIBLE
                    imagesReference.child("image $count")
                        .putFile(image)
                        .addOnSuccessListener {
                            Toast.makeText(this,"image uploaded",Toast.LENGTH_SHORT)
                                .show()
                            progressBar3.visibility =  View.INVISIBLE
                        }
                }
            }
        }
    }
}
