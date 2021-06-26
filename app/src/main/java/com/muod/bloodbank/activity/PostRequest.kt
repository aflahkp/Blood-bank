package com.muod.bloodbank.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.muod.bloodbank.R
import com.muod.bloodbank.model.RequestModel
import com.muod.bloodbank.utils.FirebaseFunctions
import kotlinx.android.synthetic.main.activity_post_request.*
import kotlinx.android.synthetic.main.activity_view_request.*
import kotlinx.android.synthetic.main.request_item_layout.*
import java.text.SimpleDateFormat
import java.util.*


class PostRequest : AppCompatActivity() {

    var TAG = "Register"

    var phone = ""
    var uid = ""
    var userName =""
    lateinit var preferences:SharedPreferences
    var today =""
    var requirementDate = ""
    lateinit var datePicker:DatePickerDialog

    lateinit var db:FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_request)

        preferences =  getSharedPreferences("userdata", Context.MODE_PRIVATE)


        phone = preferences.getString("phone","")
        uid = preferences.getString("uid","")
        userName = preferences.getString("userName","")

//        Toast.makeText(this,"phone "+uid+" user "+userName,Toast.LENGTH_SHORT).show()


        Log.d("Phone ",phone)
        Log.d("UserName ",userName)

        db = FirebaseFirestore.getInstance()

        placeTextInput.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.toString().isNullOrEmpty()){
                    placeTextInputLayout.isErrorEnabled =true
                    placeTextInputLayout.error = "Location should not be empty"
                }
                else{
                    placeTextInputLayout.error =null
                    placeTextInputLayout.isErrorEnabled = false
                }
            }

        })

        btnRegister!!.setOnClickListener {
            it ->
            this.validateAndRegister()
        }

        btnLinkToLoginScreen!!.setOnClickListener{
            finish()
        }

        var cal = Calendar.getInstance()
        today = SimpleDateFormat("d MMM YYYY").format(cal.time)

        requirementDate = SimpleDateFormat("d MMM YYYY").format(cal.time)

        datePicker = DatePickerDialog(PostRequest@this,object :DatePickerDialog.OnDateSetListener{
            override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
                var c = Calendar.getInstance()
                c.set(p1,p2,p3)
                var date = c.time

                requirementDate = SimpleDateFormat("d MMM YYYY").format(date)
                dateTextInput.setText(SimpleDateFormat("d MMM YYYY").format(date))
            }
        },cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))

        imageDate.setOnClickListener{
            datePicker.show()
        }

    }




    fun validateAndRegister(){
        if(placeTextInput.text!!.isNullOrEmpty()){
            placeTextInputLayout.error = "Location should not be empty"
            placeTextInputLayout.isErrorEnabled = true
        }
        else{
            placeTextInputLayout.error = null
            placeTextInputLayout.isErrorEnabled = false
            this.postRequest()
        }
    }


    fun postRequest(){
        try {


            val secondPhone= phoneTextInput.text.trim().toString()
            val place= placeTextInput.text.trim().toString()
            val bloodGroup= resources.getStringArray(R.array.blood_groups_only)[groupSpinner.selectedItemPosition]
            val message = messageTextInput.text.toString()

            var registerModel = RequestModel(
                    userName,
                    phone,
                    uid,
                    secondPhone,
                    place,
                    bloodGroup,
                    today,
                    requirementDate,
                    message

            )
            db.collection("Requests")
                    .add(registerModel)
                    .addOnSuccessListener {
                        FirebaseFunctions(this@PostRequest).sendPush("requests","New Blood Request from "+place,bloodGroup +" blood is requested by "+ userName)
                        finish()
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(Register@this)
                                .setTitle("Failed to postRequest")
                                .setMessage("Unable to post request")
                                .setPositiveButton("Try Again",object:DialogInterface.OnClickListener{
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        postRequest()
                                    }

                                })
                                .setNegativeButton("Cancel",null)
                                .create()
                                .show()
                    }

            //Task successful
        } catch (e: Throwable) {
            Log.d(TAG,e.localizedMessage)
            //Manage error
        }
    }
}
