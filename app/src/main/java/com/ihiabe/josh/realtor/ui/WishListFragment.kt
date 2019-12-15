package com.ihiabe.josh.realtor.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ihiabe.josh.realtor.R
import com.ihiabe.josh.realtor.model.Favourite
import com.squareup.picasso.Picasso
import java.text.DecimalFormat

class WishListFragment: Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var favouriteRef: DatabaseReference
    private val decimalFormat = DecimalFormat("#,##0.00")
    private lateinit var wishListRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wish_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wishListRecyclerView = view.findViewById(R.id.wish_list_recycler_view)
        wishListRecyclerView.hasFixedSize()
        wishListRecyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)

        val user = FirebaseAuth.getInstance().currentUser
        database = FirebaseDatabase.getInstance()

        if(user != null){
            favouriteRef = database.reference.child("Favourites").child(user.uid)
            initFireBaseUiDatabase()
        }else
            Toast.makeText(activity!!.applicationContext,"not signed in",Toast.LENGTH_SHORT)
                .show()

    }

    private fun initFireBaseUiDatabase(){
        val options = FirebaseRecyclerOptions.Builder<Favourite>().setQuery(
            favouriteRef,
            Favourite::class.java
        ).setLifecycleOwner(this).build()

        val adapter = object : FirebaseRecyclerAdapter<Favourite, FavouriteViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
                val itemView = LayoutInflater.from(activity!!.applicationContext).inflate(
                    R.layout.favourite_item,parent,false
                )
                return FavouriteViewHolder(itemView)
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(
                holder: FavouriteViewHolder,
                position: Int,
                model: Favourite
            ) {
                Picasso.with(activity!!.applicationContext).load(model.image)
                    .fit().centerCrop().into(holder.wishImage)
                holder.wishLocation.text = model.location
                holder.wishDescription.text = model.description
                holder.wishPrice.text = "₦${decimalFormat.format(model.price)}"
                holder.wishUserName.text = model.userName
                holder.wishCall.setOnClickListener {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:${model.phoneNumber}")
                    startActivity(intent)
                }
                holder.wishDelete.setOnClickListener {
                    favouriteRef.child(model.postId).removeValue().addOnCompleteListener {
                        if (it.isSuccessful)
                            Toast.makeText(activity!!.applicationContext,"removed from wish list",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        wishListRecyclerView.adapter = adapter
        adapter.startListening()
    }

    class FavouriteViewHolder(item: View): RecyclerView.ViewHolder(item){
        internal var wishImage = item.findViewById<ImageView>(R.id.wish_image)
        internal var wishLocation = item.findViewById<TextView>(R.id.wish_location)
        internal var wishDescription = item.findViewById<TextView>(R.id.wish_description)
        internal var wishPrice = item.findViewById<TextView>(R.id.wish_price)
        internal var wishUserName = item.findViewById<TextView>(R.id.wish_user_name)
        internal var wishCall = item.findViewById<Button>(R.id.wish_phone)
        internal var wishDelete = item.findViewById<Button>(R.id.wish_delete)
    }
}