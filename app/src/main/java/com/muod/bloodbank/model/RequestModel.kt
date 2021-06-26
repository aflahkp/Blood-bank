package com.muod.bloodbank.model

data class RequestModel(
        val userName: String = "",
        val phone: String = "",
        val uid: String = "",
        val secondPhone: String = "",
        val place: String = "",
        val bloodGroup: String = "",
        val postedOn: String = "",
        val requirementDate: String = "",
        val message:String=""
)