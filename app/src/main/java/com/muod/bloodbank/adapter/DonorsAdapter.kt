package com.muod.bloodbank.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muod.bloodbank.R
import com.muod.bloodbank.callback.UserInterface
import com.muod.bloodbank.model.UserModel
import kotlinx.android.synthetic.main.donor_item_layout.view.*

class DonorsAdapter(var donors: MutableList<UserModel>, var context: Context, var userInterface: UserInterface): RecyclerView.Adapter<DonorViewHolder>() {

    override fun onBindViewHolder(holder: DonorViewHolder?, position: Int) {
        holder!!.itemView.nameTextView.text = donors[position].userName
        holder!!.itemView.phoneTextView.text = donors[position].phone.toString()
        holder!!.itemView.placeTextView.text = donors[position].place
        holder!!.itemView.bloodGroupTextView.text = donors[position].bloodGroup
        holder!!.itemView.setOnClickListener {
            userInterface!!.viewUser(donors.get(position))

        }
//        holder.itemView.nameTextView.text = donors[position]!!.userName
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DonorViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.donor_item_layout,parent,false)
        return DonorViewHolder(v)
    }

    override fun getItemCount(): Int {
        Log.d("DonorList Size ",donors.size.toString())
        return donors.size
    }


}

class DonorViewHolder(v:View): RecyclerView.ViewHolder(v)
