package com.ihiabe.josh.realtor.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.ihiabe.josh.realtor.R
import kotlinx.android.synthetic.main.activity_add_listing.*
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ihiabe.josh.realtor.model.Listing
import com.squareup.picasso.Picasso

class AddListingActivity : AppCompatActivity() {
    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView
    private lateinit var image4: ImageView
    private lateinit var image5: ImageView

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_listing)

        val toolbar = findViewById<Toolbar>(R.id.add_listing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Add Listing"

        databaseReference = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        image1 = findViewById(R.id.listing_image_1)
        image2 = findViewById(R.id.listing_image_2)
        image3 = findViewById(R.id.listing_image_3)
        image4 = findViewById(R.id.listing_image_4)
        image5 = findViewById(R.id.listing_image_5)

        setImages()

        submit_listing_button.setOnClickListener {
            if (validate()){
                val location = add_listing_location.text.toString()
                val description = add_listing_description.text.toString()
                val pr = add_listing_price.text.toString()
                val price = pr.toLong()
                val listing = Listing(location,description,price,
                    "https://images.nigeriapropertycentre.com/properties/images/485112/3243344_485112-beautiful-house--detached-duplexes-for-sale-ikota-villa-estate-lekki-lagos-.jpg",
                    "https://images.nigeriapropertycentre.com/properties/images/485112/3243344_485112-beautiful-house--detached-duplexes-for-sale-ikota-villa-estate-lekki-lagos-.jpg",
                    "https://images.nigeriapropertycentre.com/properties/images/485112/3243344_485112-beautiful-house--detached-duplexes-for-sale-ikota-villa-estate-lekki-lagos-.jpg",
                    "https://images.nigeriapropertycentre.com/properties/images/485112/3243344_485112-beautiful-house--detached-duplexes-for-sale-ikota-villa-estate-lekki-lagos-.jpg",
                    "https://images.nigeriapropertycentre.com/properties/images/485112/3243344_485112-beautiful-house--detached-duplexes-for-sale-ikota-villa-estate-lekki-lagos-.jpg")
                databaseReference.child("Listing").child(auth.currentUser!!.uid).
                    setValue(listing).addOnCompleteListener { task ->
                    if (task.isSuccessful)
                        Toast.makeText(this,"listing added",Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this,"unfortunately couldn't add listing",
                            Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun setImages(){
        image1.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"),
                PICK_IMAGE1)
        }
        image2.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"),
                PICK_IMAGE2)
        }
        image3.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"),
                PICK_IMAGE3)
        }
        image4.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"),
                PICK_IMAGE4)
        }
        image5.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"),
                PICK_IMAGE5)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE1 && resultCode == Activity.RESULT_OK && data != null){
            val imagePath: Uri = data.data!!
            Picasso.get().load(imagePath).noPlaceholder().centerCrop().fit().into(image1)
        }
        if (requestCode == PICK_IMAGE2 && resultCode == Activity.RESULT_OK && data != null){
            val imagePath: Uri = data.data!!
            Picasso.get().load(imagePath).noPlaceholder().centerCrop().fit().into(image2)
        }
        if (requestCode == PICK_IMAGE3 && resultCode == Activity.RESULT_OK && data != null){
            val imagePath: Uri = data.data!!
            Picasso.get().load(imagePath).noPlaceholder().centerCrop().fit().into(image3)
        }
        if (requestCode == PICK_IMAGE4 && resultCode == Activity.RESULT_OK && data != null){
            val imagePath: Uri = data.data!!
            Picasso.get().load(imagePath).noPlaceholder().centerCrop().fit().into(image4)
        }
        if (requestCode == PICK_IMAGE5 && resultCode == Activity.RESULT_OK && data != null){
            val imagePath: Uri = data.data!!
            Picasso.get().load(imagePath).noPlaceholder().centerCrop().fit().into(image5)
        }
    }
    companion object{
        private const val PICK_IMAGE1 = 1
        private const val PICK_IMAGE2 = 2
        private const val PICK_IMAGE3 = 3
        private const val PICK_IMAGE4 = 4
        private const val PICK_IMAGE5 = 5
    }
}
