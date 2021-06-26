package com.muod.bloodbank.model

public class DonorModel(data: MutableMap<String, Any>) {
    var userName:String = data.get("userName").toString()
    var email:String = data.get("email").toString()
    var bloodGroup:String = data.get("bloodGroup").toString()
    var age:Int= (data.get("age") as Long).toInt()
    var phone:Int =   if(data.get("phone") == null)  0 else (data.get("phone") as Long).toInt()
    var secondPhone:Int = if(data.get("secondPhone") == null)  0 else (data.get("secondPhone") as Long).toInt()
    var place:String = data.get("location").toString()
}