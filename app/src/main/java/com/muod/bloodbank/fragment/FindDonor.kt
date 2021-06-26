package com.muod.bloodbank.fragment

import android.opengl.Visibility
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast

import com.muod.bloodbank.R
import com.muod.bloodbank.adapter.DonorsAdapter
import kotlinx.android.synthetic.main.fragment_find_donor.view.*
import com.google.firebase.firestore.FirebaseFirestore;
import com.muod.bloodbank.callback.UserInterface
import com.muod.bloodbank.model.UserModel
import kotlinx.android.synthetic.main.fragment_find_donor.*
import java.util.ArrayList


class FindDonor : Fragment() {


    val TAG = "FindDonor"
    var groupSelected = "All"
    var db :FirebaseFirestore?=null
    var donorArray = ArrayList<UserModel>()
    var adapter:DonorsAdapter?=null
    var userInterface: UserInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userInterface = activity as UserInterface

        adapter = DonorsAdapter(donorArray!!,context!!,userInterface!!)

        db = FirebaseFirestore.getInstance();

        this.updateDonorList()

    }

    fun updateDonorList(){

        if(groupSelected.equals("All")){
            db!!.collection("Users")
                    .get()
                    .addOnSuccessListener { result ->

                        if(result.documents.isEmpty()){
                            Snackbar.make(donor_recyclerview,"No Donors Found", Snackbar.LENGTH_SHORT).show()
                        }
                        try {
                            progress.visibility = View.GONE
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                        donorArray.clear()
                       result.documents.forEach {
                           document->
                           var donor = document.toObject(UserModel::class.java)
                           donorArray.add(donor!!)
                       }
                        adapter!!.notifyDataSetChanged()


                        for (document in result) {
                            Log.d(TAG, document.id + " => " + document.data)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                        Snackbar.make(donor_recyclerview,"Failed to load Donor list", Snackbar.LENGTH_SHORT)
                                .setAction("RETRY"){
                                    updateDonorList()
                                }
                                .show()
                        try {
                            progress.visibility = View.GONE
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
        }
        else{
            db!!.collection("Users")
                    .whereEqualTo("bloodGroup",this.groupSelected)
                    .get()
                    .addOnSuccessListener { result ->


                        if(result.documents.isEmpty()){
                            Snackbar.make(donor_recyclerview,"No Donors Found", Snackbar.LENGTH_SHORT).show()
                        }
                        try {
                            progress.visibility = View.GONE
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                        donorArray.clear()
                        result.documents.forEach {
                            document->
                            var donor = document.toObject(UserModel::class.java)
                            donorArray.add(donor!!)
                        }
                        adapter!!.notifyDataSetChanged()
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                        try {
                            progress.visibility = View.GONE
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                        Snackbar.make(donor_recyclerview,"Failed to load Donor list", Snackbar.LENGTH_SHORT)
                                .setAction("RETRY"){
                                    updateDonorList()
                                }
                                .show()

                    }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        var v = inflater.inflate(R.layout.fragment_find_donor, container, false)


        v.groupSpinner.onItemSelectedListener = object :AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                Toast.makeText(activity,resources.getStringArray(R.array.blood_groups)[p2],Toast.LENGTH_SHORT).show()
                try {
                    progress.visibility = View.VISIBLE
                }catch (e:Exception){
                    e.printStackTrace()
                }

                groupSelected = resources.getStringArray(R.array.blood_groups)[p2]
                updateDonorList()
            }

        }

        v.donor_recyclerview.layoutManager = LinearLayoutManager(context!!)
        v.donor_recyclerview.adapter = this.adapter

        return v
    }

companion object {
    var findDonor:FindDonor? =null

    fun newInstance(userInterface: UserInterface):FindDonor{
        if(findDonor ==null){
            findDonor = FindDonor()
            findDonor!!.userInterface = userInterface
        }
        return findDonor!!
    }
}

}
