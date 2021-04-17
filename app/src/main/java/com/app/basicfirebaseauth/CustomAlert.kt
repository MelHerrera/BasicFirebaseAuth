package com.app.basicfirebaseauth

import android.app.AlertDialog
import android.content.Context

class CustomAlert{
    private lateinit var mBuilder:AlertDialog

   companion object{
       val mAlertInstance:CustomAlert=CustomAlert()
   }

    fun Builder(mContext: Context,title:String, message:String):AlertDialog.Builder{
        return AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
    }
}