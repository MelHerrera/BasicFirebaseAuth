package com.app.basicfirebaseauth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class Const {
    companion object {
        var KEYNAME_USER="USUARIO"
        var KEYNAME_PASS="PASS"
        var KEYNAME_TOK="TOK"
        var KEYNAME_UID="UID"
        lateinit var DATABASE:DatabaseReference
        lateinit var AUTH: FirebaseAuth
    }
}