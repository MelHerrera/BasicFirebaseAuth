package com.app.basicfirebaseauth

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class NetworkWatcher(private val mContext:Context) : ConnectivityManager.NetworkCallback() {
    fun isConnected():Boolean {
        val conMgr = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            val capabilities = conMgr.getNetworkCapabilities(conMgr.activeNetwork)

            if(capabilities!=null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
              return accesInternet()
            else
                false
        } else {
            val activeNetwork: NetworkInfo? = conMgr.activeNetworkInfo
            if(activeNetwork?.isConnectedOrConnecting==true)
            accesInternet()
            else
                false
        }
    }

    private fun accesInternet():Boolean
    {
           return try {
                val url = URL("https://www.google.com")
                val  request = url.openConnection() as HttpURLConnection
                request.connectTimeout = 3000
                request.connect()
                request.responseCode == HttpURLConnection.HTTP_OK
            }catch (e:Exception) {
                e.printStackTrace()
               false
            }
    }
}