package com.app.basicfirebaseauth

import java.io.Serializable

data class User(var uId:String, var user:String, var pass:String, var token:String, var name:String, var lastName:String):Serializable {
    constructor():this("","","","","","")
}