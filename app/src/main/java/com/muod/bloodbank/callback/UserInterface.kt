package com.muod.bloodbank.callback

import com.muod.bloodbank.model.RequestModel
import com.muod.bloodbank.model.UserModel

interface UserInterface {
    fun viewUser(user:UserModel)
    fun viewRequest(requestModel: RequestModel)
}