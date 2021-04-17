package com.app.basicfirebaseauth

import android.content.Context
import androidx.security.crypto.MasterKey

class LLaveMaestra() {

    companion object
    {
        fun build(myContext:Context):MasterKey {
            return MasterKey.Builder(myContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }
    }
}