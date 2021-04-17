package com.app.basicfirebaseauth

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.basicfirebaseauth.Const.Companion.AUTH
import com.app.basicfirebaseauth.Const.Companion.DATABASE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var buttonSign:Button
    private lateinit var editUser:EditText
    private lateinit var editPass:EditText
    private val FLAGUSER="Usuarios"
    private var mUser:User= User()
    private lateinit var userListener:ChildEventListener
    private lateinit var userDbReferece:DatabaseReference
    private var signLocal:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFirebase()

        buttonSign=findViewById(R.id.btnSign)
        editUser=findViewById(R.id.user)
        editPass=findViewById(R.id.pass)

        buttonSign.setOnClickListener {
            if(validar())
                signIn(editUser.text.trim().toString(),editPass.text.trim().toString())
        }
    }
    private fun signIn(editUser: String, editPass: String) {
        AUTH.signInWithEmailAndPassword(editUser,editPass)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    it.result?.user?.getIdToken(true)
                        ?.addOnSuccessListener {task->

                            lifecycleScope.launch {
                                guardarPreferencias(editUser,editPass,task.token.toString(),it.result?.user?.uid)
                            }

                            lifecycleScope.launch() {
                                initializeUser(it.result?.user?.uid.toString())
                            }
                        }
                }
            }.addOnFailureListener {
                    CustomAlert.mAlertInstance.Builder(this,"Error",it.message.toString())
                            .setPositiveButton("Aceptar") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
            }
    }

    private fun signInLocal(mData:MutableList<String>)
    {
        signLocal=true

        userDbReferece=FirebaseDatabase.getInstance().getReference(FLAGUSER)

        userListener=userDbReferece.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                //Do nothing
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //Do nothing
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //Do nothing
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.exists())
                {
                    mUser= snapshot.getValue(mUser::class.java)!!

                    //se deberia verificar la validez y fecha de caducidad del token tambien// mas adelante
                    if (mUser.user==mData[0] && mUser.pass==mData[1] && mUser.uId==mData[3])
                        launchDash(mUser)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //Do nothing
            }
        })
    }

    private fun guardarPreferencias(
        mUser: String,
        mPass: String,
        mTok: String,
        uid: String?
    ) {
        val prefs = Preferencias()
        val userData=setOf(Const.KEYNAME_USER,mUser)
        val passData=setOf(Const.KEYNAME_PASS,mPass)
        val mTokData=setOf(Const.KEYNAME_TOK,mTok)
        val mUidData=setOf(Const.KEYNAME_UID,uid)

        if(!prefs.guardarSharedPrefsUser(this, userData,passData,mTokData,mUidData))
        {
            Toast.makeText(this,"Error al guardar Preferencias",Toast.LENGTH_SHORT).show()
        }
    }

    private fun initFirebase() {
        DATABASE=FirebaseDatabase.getInstance().reference
        AUTH= FirebaseAuth.getInstance()
    }

    override fun onStart() {
        var mData: MutableList<String>

        lifecycleScope.launch {
            withContext(Dispatchers.IO) { mData = obtenerPreferencias() }

            GlobalScope.launch {
                if(mData.isNotEmpty() || !mData.isNullOrEmpty()){
                    if(NetworkWatcher(this@MainActivity).isConnected())
                        signIn(mData[0],mData[1])
                    else
                        signInLocal(mData)
                }
            }
        }
        super.onStart()
    }

    private fun initializeUser(uId: String) {
            DATABASE.child(FLAGUSER).child(uId).get()
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            mUser= it.result?.getValue(mUser::class.java)!!
                            launchDash(mUser)
                        }
                    }
                    .addOnFailureListener {
                        CustomAlert.mAlertInstance.Builder(this,"Error",it.message.toString())
                                .setPositiveButton("Aceptar") { dialog, which ->
                                    dialog.dismiss()
                                }.show()
                    }
    }

    private fun launchDash( mUser: User) {
        val mIntent=Intent(this,Dash::class.java)
        mIntent.putExtra(Const.KEYNAME_USER,mUser)
        startActivity(mIntent)
        finish()
    }

    private fun obtenerPreferencias():MutableList<String>
    {
        val mySharedPrefs= Preferencias()
        return mySharedPrefs.obtenerSharedPrefsUser(this,Const.KEYNAME_USER,Const.KEYNAME_PASS,Const.KEYNAME_TOK,Const.KEYNAME_UID)
    }

    private fun validar(): Boolean {

        if(editUser.text.isEmpty())
        {
            editUser.error="Campo Requerido"
            return false
        }
        else
            editUser.error=null

        if(editPass.text.isEmpty())
        {
            editPass.error="Campo Requerido"
            return false
        }
        else
            editPass.error=null

        return true
    }

    override fun onDestroy() {
        if(signLocal)
            userDbReferece.removeEventListener(userListener)

        super.onDestroy()
    }
}