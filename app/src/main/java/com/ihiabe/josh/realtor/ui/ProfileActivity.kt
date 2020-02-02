package com.ihiabe.josh.realtor.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ihiabe.josh.realtor.R
import com.ihiabe.josh.realtor.adapter.SlideAdapter
import com.ihiabe.josh.realtor.model.User
import com.ihiabe.josh.realtor.model.UserListing
import com.squareup.picasso.Picasso
import com.viewpagerindicator.CirclePageIndicator
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.text.DecimalFormat

class ProfileActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var userListingRef: Query
    private lateinit var userRef: DatabaseReference
    private lateinit var listingRef: DatabaseReference
    private lateinit var imagesRef: StorageReference
    private lateinit var dpRef: StorageReference
    private lateinit var profileRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<Toolbar>(R.id.profile_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.title = "Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        profileRecyclerView = findViewById(R.id.profile_recycler_view)

        profileRecyclerView.layoutManager = LinearLayoutManager(this)
        profileRecyclerView.hasFixedSize()

        val user = FirebaseAuth.getInstance().currentUser
        database = FirebaseDatabase.getInstance()
        userListingRef = database.reference.child("Listing").orderByChild("userId")
            .equalTo(user!!.uid)
        userRef = database.reference.child("Users").child(user.uid)
        listingRef = database.reference.child("Listing")
        imagesRef = FirebaseStorage.getInstance().reference.child("Images/Listing Images")
        dpRef = FirebaseStorage.getInstance().reference.child("Images")
            .child("Profile Pictures").child(user.uid)

        edit_profile_fab.setOnClickListener {
            startEditProfile()
        }

        initProfileData()
        setProfilePic()
        initFireBaseUiDatabase()
    }

    private fun initFireBaseUiDatabase(){
        val options = FirebaseRecyclerOptions.Builder<UserListing>().setQuery(
            userListingRef,UserListing::class.java
        ).setLifecycleOwner(this).build()

        val adapter = object : FirebaseRecyclerAdapter<UserListing, UserListingViewHolder>(options){
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): UserListingViewHolder {
                val itemView = LayoutInflater.from(this@ProfileActivity).inflate(
                    R.layout.user_listing_item,
                    parent, false
                )
                return UserListingViewHolder(itemView)
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(
                holder: UserListingViewHolder,
                position: Int,
                model: UserListing
            ) {

                val decimalFormat = DecimalFormat("#,##0.00")
                val slideAdapter = SlideAdapter(this@ProfileActivity,model.images)
                holder.imageSlide.adapter = slideAdapter
                holder.indicator.setViewPager(holder.imageSlide)
                holder.listinglocation.text = model.location
                holder.listingDescription.text = model.description
                holder.listingPrice.text = "₦${decimalFormat.format(model.price)}"
                holder.deleteListing.setOnClickListener {
                    val builder = AlertDialog.Builder(this@ProfileActivity)
                    builder.setTitle("Delete Listing")
                    builder.setMessage("Are you sure you want to delete?")
                    builder.setCancelable(false)
                    builder.setPositiveButton("Yes"
                    ) { dialog, which ->
                        imagesRef.child(model.pushId).delete()
                        listingRef.child(model.pushId).removeValue().addOnCompleteListener {
                            if (it.isSuccessful){
                                Toast.makeText(this@ProfileActivity,"Listing removed",Toast.LENGTH_SHORT)
                                    .show()
                                dialog.dismiss()
                            }
                        }
                    }
                    builder.setNegativeButton("No"){
                        dialog, which ->
                        dialog.cancel()
                    }

                    val alertDialog = builder.create()
                    alertDialog.show()
                }
            }

            override fun getItem(position: Int): UserListing {
                return super.getItem(itemCount - 1 - position)
            }
        }
        profileRecyclerView.adapter = adapter
        adapter.startListening()
    }

    private fun initProfileData(){
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                profile_username.text = user!!.fullName
                profile_phone_number.text = user.phoneNumber
                profile_email.text = user.email
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@ProfileActivity,p0.message,Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setProfilePic(){
        dpRef.downloadUrl.addOnSuccessListener {
            Picasso.with(this).load(it).centerCrop().fit().into(profile_image)
        }
    }

    private fun startEditProfile(){
        val profileUsername = profile_username.text.toString()
        val profilePhoneNumber = profile_phone_number.text.toString()
        val profileEmail = profile_email.text.toString()
        val intent = Intent(this,EditProfileActivity::class.java)
        intent.putExtra(EXTRA_USERNAME,profileUsername)
        intent.putExtra(EXTRA_PHONE_NUMBER,profilePhoneNumber)
        intent.putExtra(EXTRA_EMAIL,profileEmail)
        startActivity(intent)
    }

    class UserListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var imageSlide = itemView.findViewById<ViewPager>(R.id.user_pager)
        internal var listinglocation = itemView.findViewById<TextView>(R.id.user_listing_location)
        internal var listingDescription = itemView.findViewById<TextView>(R.id.user_listing_description)
        internal var listingPrice = itemView.findViewById<TextView>(R.id.user_listing_price)
        internal var deleteListing = itemView.findViewById<Button>(R.id.user_delete_listing)
        internal var indicator = itemView.findViewById<CirclePageIndicator>(R.id.user_indicator)
    }

    companion object{
        const val EXTRA_USERNAME = "com.ihiabe.josh.realtor.ui.EXTRA_USERNAME"
        const val EXTRA_PHONE_NUMBER = "com.ihiabe.josh.realtor.ui.EXTRA_PHONE_NUMBER"
        const val EXTRA_EMAIL = "com.ihiabe.josh.realtor.ui.EXTRA_EMAIL"
    }
}
