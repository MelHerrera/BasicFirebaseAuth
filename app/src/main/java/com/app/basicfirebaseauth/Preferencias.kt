package com.app.basicfirebaseauth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences

class Preferencias {

    companion object
    {
         const val sharedPrefsFileUser: String ="userPrefernces"
    }

    private fun sharedPrefs(myContext: Context,sharedPrefsFile:String):EncryptedSharedPreferences
    {
        return EncryptedSharedPreferences.create(
            myContext,
            sharedPrefsFile,
            LLaveMaestra.build(myContext),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }

    fun guardarSharedPrefsUser(
        mContext: Context,
        mUser: Set<String>,
        mPass: Set<String>?,
        mTokData: Set<String>,
        mUidData: Set<String?>
    ):Boolean
    {
        try {
            val mySharedPrefs=sharedPrefs(mContext, sharedPrefsFileUser).edit()

            if(mUser.isNotEmpty())
            mySharedPrefs.putString(mUser.elementAt(0),mUser.elementAt(1))

            if(mPass?.isNotEmpty()!!)
            mySharedPrefs.putString(mPass.elementAt(0),mPass.elementAt(1))

            if(mTokData.isNotEmpty())
                mySharedPrefs.putString(mTokData.elementAt(0),mTokData.elementAt(1))

            if(mUidData.isNotEmpty())
                mySharedPrefs.putString(mUidData.elementAt(0),mUidData.elementAt(1))

            mySharedPrefs.apply()
        }
        catch (e:Exception)
        {
            return false
        }
        return true
    }

    fun obtenerSharedPrefsUser(
        myContext: Context,
        myUserKey: String,
        myPassKey: String,
        mTokKey: String,
        mUidKey: String
    ):MutableList<String>
    {
        val mySharedPrefs=sharedPrefs(myContext, sharedPrefsFileUser)
        val myData = mutableListOf<String>()

        if(mySharedPrefs.all.isNotEmpty())
        {
            mySharedPrefs.getString(myUserKey,"")?.let { myData.add(0, it) }
            mySharedPrefs.getString(myPassKey,"")?.let { myData.add(1, it) }
            mySharedPrefs.getString(mTokKey,"")?.let { myData.add(2, it) }
            mySharedPrefs.getString(mUidKey,"")?.let { myData.add(3, it) }
        }
        return myData
    }

    fun limpiarSharedPrefs(myContext: Context,sharedPrefsFile:String):Boolean
    {
        try {
            val mySharedPrefs=sharedPrefs(myContext,sharedPrefsFile).edit()

            mySharedPrefs.clear()
            mySharedPrefs.apply()
        }catch (e:Exception)
        {
            return false
        }
        return true
    }

}