package com.ihiabe.josh.realtor.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ihiabe.josh.realtor.R
import com.ihiabe.josh.realtor.adapter.SlideAdapter
import com.ihiabe.josh.realtor.model.Listing
import com.ihiabe.josh.realtor.model.User
import com.viewpagerindicator.CirclePageIndicator
import java.text.DecimalFormat
import java.util.jar.Manifest

class ListingFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var listingRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private lateinit var listingRecyclerView: RecyclerView
    private lateinit var showProgress: ProgressBar
    private lateinit var addListingButton: FloatingActionButton
    private val decimalFormat = DecimalFormat("#,##0.00")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listingRecyclerView = view.findViewById(R.id.listing_recycler_view)
        listingRecyclerView.hasFixedSize()
        listingRecyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)

        showProgress = view.findViewById(R.id.progressBarListing)
        addListingButton = view.findViewById(R.id.add_listing_button)

        listingRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy<0 && !addListingButton.isShown)
                    addListingButton.show()
                else if (dy>0 && addListingButton.isShown)
                    addListingButton.hide()
            }
        })

        val auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        listingRef = database.reference.child("Listing")
        userRef = database.reference.child("Users").child(auth.currentUser!!.uid)

        initFireBaseUiDatabase()

        addListingButton.setOnClickListener {
            startActivity(Intent(activity!!.applicationContext,AddListingActivity::class.java))
        }
    }

    private fun initFireBaseUiDatabase() {
        val options = FirebaseRecyclerOptions.Builder<Listing>().setQuery(
            listingRef,
            Listing::class.java
        ).setLifecycleOwner(this).build()

        val adapter = object : FirebaseRecyclerAdapter<Listing, ListingViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(
                holder: ListingViewHolder,
                position: Int,
                model: Listing
            ) {
                val slideAdapter = SlideAdapter(activity!!.applicationContext,model.images)
                holder.imageSlide.adapter = slideAdapter
                holder.indicator.setViewPager(holder.imageSlide)
                holder.listinglocation.text = model.location
                holder.listingDescription.text = model.description
                holder.listingPrice.text = "₦${decimalFormat.format(model.price)}"

                userRef.addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        val user = p0.getValue(User::class.java)
                        holder.listingUsername.text = user!!.fullName
                        holder.callListing.setOnClickListener {
                            requestPhonePermission()
                            if (ActivityCompat.checkSelfPermission(activity!!.applicationContext,
                                  android.Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(activity!!.applicationContext,"please grant permission",
                                    Toast.LENGTH_SHORT).show()
                            }else{
                                val intent = Intent(Intent.ACTION_CALL)
                                intent.data = Uri.parse("tel:${user.phoneNumber}")
                                startActivity(intent)
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(activity!!.applicationContext,p0.code,Toast.LENGTH_SHORT)
                            .show()
                    }

                })

                showProgress.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
                val itemView = LayoutInflater.from(activity!!.applicationContext).inflate(
                    R.layout.listing_item,
                    parent, false
                )
                return ListingViewHolder(itemView)
            }
        }

        listingRecyclerView.adapter = adapter
        adapter.startListening()
    }

    private fun requestPhonePermission(){
        ActivityCompat.requestPermissions((activity as AppCompatActivity),
            arrayOf(android.Manifest.permission.CALL_PHONE),1)
    }

    class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var imageSlide = itemView.findViewById<ViewPager>(R.id.pager)
        internal var listinglocation = itemView.findViewById<TextView>(R.id.listing_location)
        internal var listingDescription = itemView.findViewById<TextView>(R.id.listing_description)
        internal var listingPrice = itemView.findViewById<TextView>(R.id.listing_price)
        internal var listingUsername = itemView.findViewById<TextView>(R.id.listing_user_name)
        internal var callListing = itemView.findViewById<Button>(R.id.call_listing)
        internal var favListing = itemView.findViewById<Button>(R.id.fav_listing)
        internal var indicator = itemView.findViewById<CirclePageIndicator>(R.id.indicator)
    }

}
