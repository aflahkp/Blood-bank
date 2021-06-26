package com.muod.bloodbank.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muod.bloodbank.R
import com.muod.bloodbank.callback.UserInterface
import com.muod.bloodbank.model.RequestModel
import com.muod.bloodbank.model.UserModel
import kotlinx.android.synthetic.main.request_item_layout.view.*

class RequestsAdapter(var requests: MutableList<RequestModel>, var context: Context, var userInterface: UserInterface): RecyclerView.Adapter<RequestViewHolder>() {

    override fun onBindViewHolder(holder: RequestViewHolder?, position: Int) {
        holder!!.itemView.nameTextView.text = requests[position].userName
        holder!!.itemView.dateTextView.text = requests[position].requirementDate
        holder!!.itemView.placeTextView.text = requests[position].place
        holder!!.itemView.bloodGroupTextView.text = requests[position].bloodGroup
        holder!!.itemView.setOnClickListener {
            userInterface!!.viewRequest(requests.get(position))

        }
//        holder.itemView.nameTextView.text = requests[position]!!.userName
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RequestViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.request_item_layout,parent,false)
        return RequestViewHolder(v)
    }

    override fun getItemCount(): Int {
        Log.d("RequestList Size ",requests.size.toString())
        return requests.size
    }


}

class RequestViewHolder(v:View): RecyclerView.ViewHolder(v)
