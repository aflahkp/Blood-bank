package com.muod.bloodbank.model

data class UserModel(
        val userName:String = "",
        val phone:String = "",
        val uid:String = "",
        val secondPhone:String = "",
        val gender:String = "",
        val age:Int = 0,
        val place:String = "",
        val bloodGroup:String = ""
        )