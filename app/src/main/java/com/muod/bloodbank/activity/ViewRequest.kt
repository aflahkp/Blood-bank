package com.muod.bloodbank.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.muod.bloodbank.R
import com.muod.bloodbank.model.UserModel
import kotlinx.android.synthetic.main.activity_view_request.*
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.muod.bloodbank.model.RequestModel


class ViewRequest : AppCompatActivity() {

    lateinit var requestModel: RequestModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_request)

        requestModel = RequestModel(
                intent.getStringExtra("userName"),
                intent.getStringExtra("phone"),
                intent.getStringExtra("uid"),
                intent.getStringExtra("secondPhone"),
                intent.getStringExtra("place"),
                intent.getStringExtra("bloodGroup"),
                intent.getStringExtra("postedOn"),
                intent.getStringExtra("requirementDate"),
                intent.getStringExtra("message")
        )

        try {
            postmanTextView.setText(requestModel.userName)
            phoneTextView.setText(requestModel.phone)
            secondPhoneTextView.setText(requestModel.secondPhone)
            locationTextView.setText(requestModel.place)
            postDateTextView.setText(requestModel.postedOn)
            requiredDateTextView.setText(requestModel.requirementDate)
            messageTextView.setText(requestModel.message)
            bloodGroupTextView.setText(requestModel.bloodGroup)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        btnCallOne.setOnClickListener{
            call(requestModel.phone)
        }

        btnCallTwo.setOnClickListener{
            call(requestModel.secondPhone)
        }
        btnWhatsapp.setOnClickListener{
            whatsapp(requestModel.phone)
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
