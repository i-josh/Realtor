package com.ihiabe.josh.realtor.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ihiabe.josh.realtor.R
import com.ihiabe.josh.realtor.adapter.SlideAdapter
import com.ihiabe.josh.realtor.model.User
import com.ihiabe.josh.realtor.model.UserListing
import com.viewpagerindicator.CirclePageIndicator
import kotlinx.android.synthetic.main.activity_profile.*
import java.text.DecimalFormat

class ProfileActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var userListingRef: Query
    private lateinit var userRef: DatabaseReference
    private lateinit var profileRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<Toolbar>(R.id.profile_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.title = "Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        profileRecyclerView = findViewById(R.id.profile_recycler_view)

        profileRecyclerView.layoutManager = LinearLayoutManager(this)
        profileRecyclerView.hasFixedSize()

        val user = FirebaseAuth.getInstance().currentUser
        database = FirebaseDatabase.getInstance()
        userListingRef = database.reference.child("Listing").orderByChild("userId")
            .equalTo(user!!.uid)
        userRef = database.reference.child("Users").child(user.uid)

        initProfileData()
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

    class UserListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var imageSlide = itemView.findViewById<ViewPager>(R.id.user_pager)
        internal var listinglocation = itemView.findViewById<TextView>(R.id.user_listing_location)
        internal var listingDescription = itemView.findViewById<TextView>(R.id.user_listing_description)
        internal var listingPrice = itemView.findViewById<TextView>(R.id.user_listing_price)
        internal var deleteListing = itemView.findViewById<Button>(R.id.user_delete_listing)
        internal var indicator = itemView.findViewById<CirclePageIndicator>(R.id.user_indicator)
    }
}
