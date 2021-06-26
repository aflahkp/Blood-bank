package com.muod.bloodbank.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.muod.bloodbank.R
import com.muod.bloodbank.callback.UserInterface
import com.muod.bloodbank.fragment.AboutUs
import com.muod.bloodbank.fragment.FindDonor
import com.muod.bloodbank.fragment.MyProfile
import com.muod.bloodbank.fragment.Requests
import com.muod.bloodbank.model.RequestModel
import com.muod.bloodbank.model.UserModel
import kotlinx.android.synthetic.main.activity_home.*
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging



class HomeActivity : AppCompatActivity(),UserInterface {


    lateinit var preferences: SharedPreferences

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        navigateTo(item.itemId)

        when (item.itemId) {
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        return@OnNavigationItemSelectedListener true
    }

    override fun viewUser(user:UserModel){
        var viewUserIntent = Intent(HomeActivity@this,ViewUser::class.java)
        viewUserIntent.putExtra("userName",user.userName)
        viewUserIntent.putExtra("phone",user.phone)
        viewUserIntent.putExtra("secondPhone",user.secondPhone)
        viewUserIntent.putExtra("gender",user.gender)
        viewUserIntent.putExtra("age",user.age)
        viewUserIntent.putExtra("uid",user.uid)
        viewUserIntent.putExtra("place",user.place)
        viewUserIntent.putExtra("bloodGroup",user.bloodGroup)
        startActivity(viewUserIntent)
    }



    override fun viewRequest(requestModel: RequestModel) {
        var viewRequestIntent = Intent(HomeActivity@this,ViewRequest::class.java)
        viewRequestIntent.putExtra("userName",requestModel.userName)
        viewRequestIntent.putExtra("phone",requestModel.phone)
        viewRequestIntent.putExtra("secondPhone",requestModel.secondPhone)
        viewRequestIntent.putExtra("uid",requestModel.uid)
        viewRequestIntent.putExtra("place",requestModel.place)
        viewRequestIntent.putExtra("bloodGroup",requestModel.bloodGroup)
        viewRequestIntent.putExtra("postedOn",requestModel.postedOn)
        viewRequestIntent.putExtra("requirementDate",requestModel.requirementDate)
        viewRequestIntent.putExtra("message",requestModel.message)
        startActivity(viewRequestIntent)

    }

    fun navigateTo(id:Int){

        when (id) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main,MyProfile.newInstance()).commit()
            }
            R.id.navigation_dashboard -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main,FindDonor.newInstance(this@HomeActivity as UserInterface)).commit()
            }
            R.id.navigation_notifications -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_main,Requests.newInstance(this@HomeActivity)).commit()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        preferences = applicationContext.getSharedPreferences("MyPrefs",0)


        navigation.selectedItemId = R.id.navigation_dashboard

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        var isSubscribed = preferences.getBoolean("isSubscribed",false)

        if(!isSubscribed){
            subscribeToTopic()
        }

    }

    fun subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("requests")
                .addOnCompleteListener { task ->
                    var msg = "Subscribed"
                    if (!task.isSuccessful) {
                        msg = "Failed to Subscribe"
                    }
                    else{
                        var editor = preferences.edit()
                        editor.putBoolean("isSubscribed",true)
                        editor.commit()
                        editor.apply()
                    }
                    Log.d("firebasetopic", msg)
//                    Toast.makeText(this@HomeActivity, msg, Toast.LENGTH_SHORT).show()
                }
    }
}
