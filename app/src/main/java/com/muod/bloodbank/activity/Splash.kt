package com.muod.bloodbank.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import com.muod.bloodbank.R
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_splash.*


class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        var preferences = getSharedPreferences("userdata", Context.MODE_PRIVATE)

        var isSignedIn = preferences
                .getBoolean("isRegistered", false)

        var skipLogin = preferences
                .getBoolean("skipLogin", false)

        Log.d("signedIn", isSignedIn.toString())

        Handler().postDelayed(Runnable {
            if (isSignedIn) {
                startActivity(Intent(this@Splash, HomeActivity::class.java))
                finish()
            } else {

                if(skipLogin){
                    startActivity(Intent(this@Splash, HomeActivity::class.java))
                    finish()
                }

                else{
                    var intent = Intent(this@Splash, SignIn::class.java)
//                val options = ActivityOptionsCompat.makeSceneTransitionAnimation (this@Splash,imageView2, ViewCompat.getTransitionName(imageView2))
                    startActivity(intent)
                    finish()
                }
            }

        }, 2000)


    }
}
