package com.muod.bloodbank.utils

import android.content.ContentValues.TAG
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import android.content.ContentValues.TAG
import android.content.Context
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.muod.bloodbank.MySingleton


class FirebaseFunctions(var context:Context) {

    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey = "key=" + Constants.SERVER_KEY
    private val contentType = "application/json"
    val TAG = "NOTIFICATION TAG"

    fun sendPush(topic:String,title:String,message:String){

        var TOPIC = "/topics/requests" //topic must match with what the receiver subscribed to
//        NOTIFICATION_TITLE = edtTitle.getText().toString()
//        NOTIFICATION_MESSAGE = edtMessage.getText().toString()

        val notification = JSONObject()
        val notifcationBody = JSONObject()
        try {
            notifcationBody.put("title", title)
            notifcationBody.put("message", message)

            notification.put("to", TOPIC)
            notification.put("data", notifcationBody)
        } catch (e: JSONException) {
            Log.e(TAG, "onCreate: " + e.message)
        }

        sendNotification(notification)
    }


    private fun sendNotification(notification: JSONObject) {
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
                Response.Listener<JSONObject> {
                    response ->  {
                    Log.d("FCMTOPIC",response.toString())
                }
                },
               Response.ErrorListener{
                   error -> {
                   Log.d("FCMTOPIC",error.localizedMessage)
               }
                }) {
            override fun getHeaders(): MutableMap<String, String>  {
                    val params = HashMap<String,String>()
                    params.put("Authorization", serverKey)
                    params.put("Content-Type", contentType)
                    return params
                }
        }
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }

}