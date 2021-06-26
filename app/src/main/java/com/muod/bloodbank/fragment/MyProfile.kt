package com.muod.bloodbank.fragment

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore

import com.muod.bloodbank.R
import com.muod.bloodbank.model.UserModel
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.layout_edit_profile.*
import kotlinx.android.synthetic.main.layout_view_profile.*


class MyProfile : Fragment() {

    var TAG = "MyProfile"
    val VIEW_PROFILE = 201
    val EDIT_PROFILE = 202
    val SIGNUP_NOW = 203


    var phone = ""
    var uid = ""
    var notLoggedIn = false
    lateinit var preferences: SharedPreferences

    lateinit var db: FirebaseFirestore
    var userModel = UserModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       preferences =  activity!!.getSharedPreferences("userdata", Context.MODE_PRIVATE)

        notLoggedIn =preferences.getBoolean("skipSLogin",false)

                phone = preferences.getString("phone", "")
        uid = preferences.getString("uid", "")

        db = FirebaseFirestore.getInstance()

        db!!.collection("Users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    this.userModel = document!!.toObject(UserModel::class.java)!!
                    setProfileView()
                }

        nameTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0!!.toString().isNullOrEmpty()) {
                    rTextName.isErrorEnabled = true
                    rTextName.error = "Name should not be empty"
                } else {
                    rTextName.error = null
                    rTextName.isErrorEnabled = false
                }
            }

        })

        btnUpdate!!.setOnClickListener { it ->
            this.validateAndRegister()
        }

        btnCancel!!.setOnClickListener { it ->
            this.cancelEdit()
        }

        btnEdit!!.setOnClickListener { it ->
            this.switchView(EDIT_PROFILE)
        }
    }


    fun validateAndRegister() {
        if (nameTextInput.text!!.isNullOrEmpty()) {
            rTextName.error = "Name should not be empty"
            rTextName.isErrorEnabled = true
        } else {
            rTextName.error = null
            rTextName.isErrorEnabled = false
            this.update()
        }
    }


    fun update() {
        try {

            val userName = nameTextInput.text.trim().toString()
            val secondPhone = phoneTextInput.text.trim().toString()
            val gender = resources.getStringArray(R.array.gender)[genderSpinner.selectedItemPosition]
            val age: Int = Integer.parseInt(ageTextInput.text.trim().toString())
            val place = placeTextInput.text.trim().toString()
            val bloodGroup = resources.getStringArray(R.array.blood_groups_only)[groupSpinner.selectedItemPosition]

            var updateUserModel = UserModel(
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
                    .set(updateUserModel)
                    .addOnSuccessListener {
                        this.userModel = updateUserModel
                        var editor = preferences.edit()
                        editor.putString("uid", uid)
                        editor.putString("phone", phone)
                        editor.putBoolean("isRegistered", true)
                        editor.putString("userName",updateUserModel.userName)
                        editor.commit()
                        editor.apply()
                        switchView(VIEW_PROFILE)
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(activity!!)
                                .setTitle("Failed to register")
                                .setMessage("Unable to complete registration")
                                .setPositiveButton("Try Again",object: DialogInterface.OnClickListener{
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        update()
                                    }

                                })
                                .setNegativeButton("Cancel",null)
                                .create()
                                .show()
                    }

//
//            switchView(VIEW_PROFILE)
            //Task successful
        } catch (e: Throwable) {
            Log.d(TAG, e.localizedMessage)
            //Manage error
        }
    }


    fun cancelEdit() {
        resetEditView()
        switchView(VIEW_PROFILE)
    }

    fun setProfileView() {

        try {
            nameTextView.setText(userModel.userName)
            phoneTextView.setText(userModel.phone)
            secondPhoneTextView.setText(userModel.secondPhone)
            placeTextView.setText(userModel.place)
            genderTextView.setText(userModel.gender)
            ageTextView.setText(userModel.age.toString())
            bloodGroupTextView.setText(userModel.bloodGroup)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resetEditView() {

        try {
            nameTextInput.setText(userModel.userName)
            ageTextInput.setText(userModel.age.toString())
            placeTextInput.setText(userModel.place)
            phoneTextInput.setText(userModel.secondPhone)
            genderSpinner.setSelection(resources.getStringArray(R.array.blood_groups_only).indexOf(userModel.gender))
            groupSpinner.setSelection(resources.getStringArray(R.array.gender).indexOf(userModel.bloodGroup))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun switchView(viewType: Int) {

        try {
            when (viewType) {
                VIEW_PROFILE -> {
                    setProfileView()
                    editProfile.visibility = View.GONE
                    viewProfile.visibility = View.VISIBLE
                }

                EDIT_PROFILE -> {
                    resetEditView()
                    viewProfile.visibility = View.GONE
                    editProfile.visibility = View.VISIBLE
                }

                SIGNUP_NOW -> {
                    resetEditView()
                    viewProfile.visibility = View.GONE
                    editProfile.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {

        var myProfile: MyProfile? = null

        fun newInstance(): MyProfile {
            if (myProfile == null) {
                myProfile = MyProfile()
            }
            return myProfile!!
        }
    }
}
