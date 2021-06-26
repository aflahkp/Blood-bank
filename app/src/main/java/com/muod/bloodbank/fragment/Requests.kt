package com.muod.bloodbank.fragment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.muod.bloodbank.R
import kotlinx.android.synthetic.main.fragment_requests.view.*
import com.google.firebase.firestore.FirebaseFirestore;
import com.muod.bloodbank.activity.PostRequest
import com.muod.bloodbank.adapter.RequestsAdapter
import com.muod.bloodbank.callback.UserInterface
import com.muod.bloodbank.model.RequestModel
import kotlinx.android.synthetic.main.fragment_requests.*
import java.util.ArrayList


class Requests : Fragment() {


    val TAG = "FindDonor"
    var groupSelected = "All"
    var db :FirebaseFirestore?=null
    var requestsArray = ArrayList<RequestModel>()
    var adapter:RequestsAdapter?=null
    var userInterface: UserInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userInterface = activity as UserInterface

        adapter = RequestsAdapter(requestsArray!!,context!!,userInterface!!)

        db = FirebaseFirestore.getInstance();


        this.loadRequests()

    }

    fun loadRequests(){

            db!!.collection("Requests")
                    .get()
                    .addOnSuccessListener { result ->

                        try {
                            progress.visibility = View.GONE
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                        if(result.documents.isEmpty()){
                            Snackbar.make(requests_recyclerview,"No Requests",Snackbar.LENGTH_SHORT).show()
                        }
                        requestsArray.clear()
                       result.documents.forEach {
                           document->
                           var donor = document.toObject(RequestModel::class.java)
                           requestsArray.add(donor!!)
                       }
                        adapter!!.notifyDataSetChanged()


                        for (document in result) {
                            Log.d(TAG, document.id + " => " + document.data)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                        Snackbar.make(requests_recyclerview,"Failed to load requests",Snackbar.LENGTH_SHORT)
                                .setAction("RETRY"){
                                    loadRequests()
                                }
                                .show()
                        try {
                            progress.visibility = View.GONE
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
    }

    override fun onResume() {
        super.onResume()
        this.loadRequests()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        var v = inflater.inflate(R.layout.fragment_requests, container, false)

        v.requests_recyclerview.layoutManager = LinearLayoutManager(context!!)
        v.requests_recyclerview.adapter = this.adapter

        v.fab_post.setOnClickListener{
            var intent = Intent(activity,PostRequest::class.java)
            activity!!.startActivity(intent)
        }

        return v
    }

companion object {
    var requests:Requests? =null

    fun newInstance(userInterface: UserInterface):Requests{
        if(requests==null){
            requests= Requests()
            requests!!.userInterface = userInterface
        }
        return requests!!
    }
}

}
