package com.muod.bloodbank.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.muod.bloodbank.R
import com.muod.bloodbank.model.UserModel
import kotlinx.android.synthetic.main.activity_view_profile.*
import android.content.Intent
import android.net.Uri
import android.widget.Toast




class ViewUser : AppCompatActivity() {

    lateinit var userModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)

        userModel = UserModel(
                intent.getStringExtra("userName"),
                intent.getStringExtra("phone"),
                intent.getStringExtra("uid"),
                intent.getStringExtra("secondPhone"),
                intent.getStringExtra("gender"),
                intent.getIntExtra("age", 0),
                intent.getStringExtra("place"),
                intent.getStringExtra("bloodGroup")
        )

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


        btnCallOne.setOnClickListener{
            call(userModel.phone)
        }

        btnCallTwo.setOnClickListener{
            call(userModel.secondPhone)
        }
        btnWhatsapp.setOnClickListener{
            whatsapp(userModel.phone)
        }

    }

    fun call(phone:String){
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:"+phone)
        startActivity(intent)
    }

    fun whatsapp(phone: String){
        try {
            val whatsAppRoot = "http://api.whatsapp.com/"
            val number = "send?phone=91"+phone //here the mobile number with its international prefix
            val text = "&text=HERE YOUR TEXT"
            val uri = whatsAppRoot + number //+ text

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(uri)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(applicationContext,
                    "WhatsApp cannot be opened", Toast.LENGTH_SHORT).show()
        }

    }
}
