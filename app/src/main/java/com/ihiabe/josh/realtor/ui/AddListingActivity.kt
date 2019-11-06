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
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ihiabe.josh.realtor.adapter.ImageGridAdapter
import com.ihiabe.josh.realtor.adapter.SlideAdapter
import com.ihiabe.josh.realtor.model.Listing
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.PicassoAdapter
import com.sangcomz.fishbun.define.Define
import com.squareup.picasso.Picasso

class AddListingActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var imageReference: StorageReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_listing)

        val toolbar = findViewById<Toolbar>(R.id.add_listing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Add Listing"

        databaseReference = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()
        imageReference = storageReference.child("Images").
            child("Listing Images").child(auth.currentUser!!.uid)

        submit_listing_button.setOnClickListener {
            if (validate()){
                val userId = auth.currentUser!!.uid
                val location = add_listing_location.text.toString()
                val description = add_listing_description.text.toString()
                val price = add_listing_price.text.toString().toLong()
                val images: List<String> = mutableListOf("https://images.adsttc.com/media/images/5c7e/dd83/284d/d1ce/f000/0220/newsletter/190217_SENCILLO18.jpg?1551818102",
                    "https://images.adsttc.com/media/images/5c7e/dd83/284d/d1ce/f000/0220/newsletter/190217_SENCILLO18.jpg?1551818102",
                    "https://images.adsttc.com/media/images/5c7e/dd83/284d/d1ce/f000/0220/newsletter/190217_SENCILLO18.jpg?1551818102")
                val listing = Listing(userId,location,description,price,images)
                databaseReference.child("Listing").push()
                    .setValue(listing).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this,"listing added",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,RentActivity::class.java))
                        finish()
                    }
                    else
                        Toast.makeText(this,"unfortunately couldn't add listing",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }

        add_images_button.setOnClickListener {
            FishBun.with(this).setImageAdapter(PicassoAdapter())
                .setActionBarColor(Color.WHITE,Color.WHITE)
                .setActionBarTitleColor(Color.parseColor("#448aff"))
                .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(this,
                    R.drawable.ic_arrow_back))
                .setDoneButtonDrawable(ContextCompat.getDrawable(this,
                    R.drawable.ic_done))
                .startAlbum()
        }
    }

    private fun validate(): Boolean {
        if (add_listing_location.text!!.isEmpty()){
            add_listing_location.error = "location required"
            return false
        }
        if (add_listing_description.text!!.isEmpty()){
            add_listing_description.error = "description required"
            return false
        }
        if (add_listing_price.text!!.isEmpty()){
            add_listing_price.error = "price required"
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            Define.ALBUM_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK){
                val imagePaths: List<Uri> = data!!.getParcelableArrayListExtra(Define.INTENT_PATH)

                val adapter = ImageGridAdapter(this,imagePaths)
                listing_image_grid.numColumns = 2
                listing_image_grid.adapter = adapter
            }
        }
    }
}
