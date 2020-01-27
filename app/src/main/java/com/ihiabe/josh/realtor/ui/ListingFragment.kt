package com.ihiabe.josh.realtor.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ihiabe.josh.realtor.R
import com.ihiabe.josh.realtor.adapter.SlideAdapter
import com.ihiabe.josh.realtor.auth.SignInActivity
import com.ihiabe.josh.realtor.model.Favourite
import com.ihiabe.josh.realtor.model.Listing
import com.squareup.picasso.Picasso
import com.viewpagerindicator.CirclePageIndicator
import de.hdodenhof.circleimageview.CircleImageView
import java.text.DecimalFormat

class ListingFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var listingRef: DatabaseReference
    private lateinit var favouriteRef: DatabaseReference
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

        val preferences =
            PreferenceManager.getDefaultSharedPreferences(activity!!.applicationContext)
        val editor = preferences.edit()
        editor.putBoolean("isFavourite", false)
        editor.apply()

        database = FirebaseDatabase.getInstance()
        listingRef = database.reference.child("Listing")


        listingRecyclerView = view.findViewById(R.id.listing_recycler_view)
        listingRecyclerView.hasFixedSize()
        listingRecyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)

        showProgress = view.findViewById(R.id.progressBarListing)
        addListingButton = view.findViewById(R.id.add_listing_button)

        listingRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !addListingButton.isShown)
                    addListingButton.show()
                else if (dy > 0 && addListingButton.isShown)
                    addListingButton.hide()
            }
        })

        initFireBaseUiDatabase()

        addListingButton.setOnClickListener {
            val signedUser = FirebaseAuth.getInstance().currentUser
            if (signedUser != null)
                startActivity(Intent(activity!!.applicationContext, AddListingActivity::class.java))
            else
                Snackbar.make(
                    view.findViewById(R.id.listingFragment)
                    , "Not signed in"
                    , Snackbar.LENGTH_LONG
                ).setAction(
                    "Sign in"
                ) {
                    startActivity(Intent(activity!!.applicationContext, SignInActivity::class.java))
                }.show()
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
                val slideAdapter = SlideAdapter(activity!!.applicationContext, model.images)
                holder.imageSlide.adapter = slideAdapter
                holder.indicator.setViewPager(holder.imageSlide)
                holder.listinglocation.text = model.location
                holder.listingDescription.text = model.description
                holder.listingPrice.text = "₦${decimalFormat.format(model.price)}"
                holder.listingUsername.text = model.userName
                Picasso.with(activity!!.applicationContext).load(model.userImage).fit().centerCrop()
                    .into(holder.listingUserImage)

                holder.callListing.setOnClickListener {
                    requestPhonePermission()
                    if (ActivityCompat.checkSelfPermission(
                            activity!!.applicationContext,
                            android.Manifest.permission.CALL_PHONE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(
                            activity!!.applicationContext,
                            "please grant permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data = Uri.parse("tel:${model.userPhoneNumber}")
                        startActivity(intent)
                    }
                }

                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    holder.favListing.isEnabled = false
                    holder.favListing.background = ContextCompat.getDrawable(
                        activity!!.applicationContext,
                        R.drawable.ic_bookmark_grey
                    )
                }
                if (user != null) {
                    favouriteRef = database.reference.child("Favourites").child(user.uid).push()
                    val isBookmarked = model.bookmarked[user.uid] == true
                    if (isBookmarked) {
                        holder.favListing.background = ContextCompat.getDrawable(
                            activity!!.applicationContext,
                            R.drawable.ic_bookmark_blue
                        )
                    }
                }

                holder.favListing.setOnClickListener {
                    if (user != null) {
                        val postId = favouriteRef.key!!
                        val isBookmarked = model.bookmarked[user.uid] == true
                        if (isBookmarked) {
                            listingRef.child(model.pushId).child("bookmarked").child(user.uid)
                                .setValue(false)
                            holder.favListing.background = ContextCompat.getDrawable(
                                activity!!.applicationContext,
                                R.drawable.ic_bookmark
                            )
                        } else {
                            listingRef.child(model.pushId).child("bookmarked").child(user.uid)
                                .setValue(true)
                            addBookmark(
                                postId,
                                model.location,
                                model.description,
                                model.price,
                                model.images[0],
                                model.userPhoneNumber,
                                model.userName
                            )
                            holder.favListing.background = ContextCompat.getDrawable(
                                activity!!.applicationContext,
                                R.drawable.ic_bookmark_blue
                            )
                        }
                    }
                }

                showProgress.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
                val itemView = LayoutInflater.from(activity!!.applicationContext).inflate(
                    R.layout.listing_item,
                    parent, false
                )
                return ListingViewHolder(itemView)
            }

            override fun getItem(position: Int): Listing {
                return super.getItem(itemCount - 1 - position)
            }
        }

        listingRecyclerView.adapter = adapter
        adapter.startListening()
    }

    private fun requestPhonePermission() {
        ActivityCompat.requestPermissions(
            (activity as AppCompatActivity),
            arrayOf(android.Manifest.permission.CALL_PHONE), 1
        )
    }

    private fun addBookmark(
        postId: String, location: String, description: String, price: Long, image: String,
        phoneNumber: String, userName: String
    ) {
        val favourite =
            Favourite(postId, location, description, price, image, phoneNumber, userName)
        favouriteRef.setValue(favourite).addOnCompleteListener { task ->
            if (task.isSuccessful)
                Toast.makeText(
                    activity!!.applicationContext, "Added to wish list"
                    , Toast.LENGTH_SHORT
                ).show()
            else
                Toast.makeText(
                    activity!!.applicationContext, "could not bookmark, try again"
                    , Toast.LENGTH_SHORT
                ).show()
        }
    }

    class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var imageSlide = itemView.findViewById<ViewPager>(R.id.pager)
        internal var listinglocation = itemView.findViewById<TextView>(R.id.listing_location)
        internal var listingDescription = itemView.findViewById<TextView>(R.id.listing_description)
        internal var listingPrice = itemView.findViewById<TextView>(R.id.listing_price)
        internal var listingUsername = itemView.findViewById<TextView>(R.id.listing_user_name)
        internal var listingUserImage = itemView.findViewById<CircleImageView>(R.id.listing_user_image)
        internal var callListing = itemView.findViewById<Button>(R.id.call_listing)
        internal var favListing = itemView.findViewById<Button>(R.id.fav_listing)
        internal var indicator = itemView.findViewById<CirclePageIndicator>(R.id.indicator)
    }
}
