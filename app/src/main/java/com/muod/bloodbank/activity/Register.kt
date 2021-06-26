package com.muod.bloodbank.activity

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
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.muod.bloodbank.R
import com.muod.bloodbank.model.UserModel
import kotlinx.android.synthetic.main.activity_register.*


class Register : AppCompatActivity() {

    var TAG = "Register"

    var phone = ""
    var uid = ""
    lateinit var preferences:SharedPreferences

    lateinit var db:FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        preferences =  getSharedPreferences("userdata", Context.MODE_PRIVATE)

        phone = intent.getStringExtra("phone")
        uid = intent.getStringExtra("uid")

        db = FirebaseFirestore.getInstance()

        nameTextInput.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.toString().isNullOrEmpty()){
                    rTextName.isErrorEnabled =true
                    rTextName.error = "Name should not be empty"
                }
                else{
                    rTextName.error =null
                    rTextName.isErrorEnabled = false
                }
            }

        })

        btnRegister!!.setOnClickListener {
            it ->
            this.validateAndRegister()
        }

        btnLinkToLoginScreen!!.setOnClickListener{
            startActivity(Intent(Register@this,SignIn::class.java))
            finish()
        }

    }

    fun validateAndRegister(){
        if(nameTextInput.text!!.isNullOrEmpty()){
            rTextName.error = "Name should not be empty"
            rTextName.isErrorEnabled = true
        }
        else{
            rTextName.error = null
            rTextName.isErrorEnabled = false
            this.register()
        }
    }


    fun register(){
        try {

            val userName = nameTextInput.text.trim().toString()
            val secondPhone= phoneTextInput.text.trim().toString()
            val gender= resources.getStringArray(R.array.gender)[genderSpinner.selectedItemPosition]
            val age:Int = Integer.parseInt(ageTextInput.text.trim().toString())
            val place= placeTextInput.text.trim().toString()
            val bloodGroup= resources.getStringArray(R.array.blood_groups_only)[groupSpinner.selectedItemPosition]

            var registerModel = UserModel(
                    userName,
                    phone,
                    uid,
                    secondPhone,
                    gender,
                    age,
                    place,
                    bloodGroup
            )
            val userPublicProfileRef = db.collection("Users").document(uid)
            userPublicProfileRef
                    .set(registerModel)
                    .addOnSuccessListener {
                        var editor = preferences.edit()
                        editor.putString("uid",uid)
                        editor.putString("phone",phone)
                        editor.putString("userName",registerModel.userName)
                        editor.putBoolean("isRegistered",true)
                        editor.commit()
                        editor.apply()
                        var intent = Intent(this@Register,HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(Intent(this@Register,HomeActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(Register@this)
                                .setTitle("Failed to register")
                                .setMessage("Unable to complete registration")
                                .setPositiveButton("Try Again",object:DialogInterface.OnClickListener{
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        register()
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
