package com.app.basicfirebaseauth

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.basicfirebaseauth.Const.Companion.AUTH
import com.app.basicfirebaseauth.Const.Companion.DATABASE
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class Dash : AppCompatActivity() {
    private var mUser:User=User()
    private lateinit var txtWelc:TextView
    lateinit  var mUserAdapter:UserAdapter
    private var mListUsers: ArrayList<User> = arrayListOf()
    lateinit var mLayoutManager:RecyclerView.LayoutManager
    lateinit var mRecyclerView: RecyclerView
    private val FLAGUSER="Usuarios"
    private lateinit var txtUser:TextInputLayout
    private lateinit var User:TextInputEditText
    private lateinit var txtPass:TextInputLayout
    private lateinit var Pass:TextInputEditText
    private lateinit var txtNames:TextInputLayout
    private lateinit var Names:TextInputEditText
    private lateinit var txtLastNames:TextInputLayout
    private lateinit var LastNames:TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash)

        txtWelc=findViewById(R.id.txt_welcome)
        txtUser=findViewById(R.id.txtusuario)
        User=findViewById(R.id.edtusuario)
        txtNames= findViewById(R.id.txt_Nombres)
        Names=findViewById(R.id.edtNombres)
        txtLastNames=findViewById(R.id.txt_Apellidos)
        LastNames=findViewById(R.id.edtApellidos)
        txtPass= findViewById(R.id.txtcontra)
        Pass=findViewById(R.id.edtcontra)

        mRecyclerView=findViewById(R.id.Recy)
        mLayoutManager=LinearLayoutManager(this)

         listUsers()

        mUser=intent.extras?.get(Const.KEYNAME_USER) as User

        txtWelc.text="${mUser.name} ${mUser.lastName}"

        findViewById<Button>(R.id.btn_Add).setOnClickListener {
            addUser()
        }
    }

    private fun addUser() {
        val mUser = gatherUser()

        if(mUser!=null){
            DATABASE.child(FLAGUSER).child(mUser.uId).setValue(mUser)
                    .addOnCompleteListener {res->
                        if(res.isSuccessful){
                            limpiarCampos()
                        }
                    }.addOnFailureListener { error ->
                        showError(error.message.toString())
                    }
        }

        /*if (mUser != null) {
            AUTH.createUserWithEmailAndPassword(mUser.user, mUser.pass)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            it.result?.user?.getIdToken(true)
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            mUser.uId = it.result?.user?.uid.toString()
                                            mUser.token = task.result?.token.toString()

                                            DATABASE.child(FLAGUSER).child(mUser.uId).setValue(mUser)
                                                    .addOnCompleteListener {res->
                                                        if(res.isSuccessful){
                                                            limpiarCampos()
                                                        }
                                                    }
                                                  .addOnFailureListener { error ->
                                                        showError(error.message.toString())
                                                    }
                                        }
                                    }
                        }
                    }.addOnFailureListener {
                        showError(it.message.toString())
                    }
        }*/
    }

    private fun limpiarCampos() {
        Names.text?.clear()
        LastNames.text?.clear()
        User.text?.clear()
        Pass.text?.clear()
    }

    private fun gatherUser():User? {
        return if(validar())
            User("",User.text.toString(),Pass.text.toString(),"",Names.text.toString(),LastNames.text.toString())
        else
            null
    }

    private fun showError(message: String) {
        CustomAlert.mAlertInstance.Builder(this,"Error",message.toString())
                .setPositiveButton("Aceptar") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
    }

    private fun validar(): Boolean {
        if(Names.text?.isEmpty()!!) {
           txtNames.error="Campo Requerido"
            return false
        }
        else
            txtNames.error=null


        if(LastNames.text?.isEmpty()!!) {
            txtLastNames.error="Campo Requerido"
            return false
        }
        else
            txtLastNames.error=null

        if(User.text?.isEmpty()!!) {
            txtUser.error="Campo Requerido"
            return false
        }
        else
            txtUser.error=null

        if(Pass.text?.isEmpty()!!) {
           txtPass.error="Campo Requerido"
            return false
        }
        else
           txtPass.error=null

        return true
    }

    private fun listUsers() {
            DATABASE.child(FLAGUSER).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    //nothing to dooo
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    mListUsers.clear()
                    for (item in snapshot.children) {
                        val mUser: User? =item.getValue(mUser::class.java)
                        if (mUser != null) {
                            mListUsers.add(mUser)
                        }
                    }
                   mUserAdapter= UserAdapter(mListUsers)
                    mRecyclerView.adapter=mUserAdapter
                    mRecyclerView.layoutManager=mLayoutManager
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_cerrar ->{
                FirebaseAuth.getInstance().signOut()

                val mySharedPrefs= Preferencias()
                if(mySharedPrefs.limpiarSharedPrefs(this,Preferencias.sharedPrefsFileUser))
                    startActivity(Intent(this, MainActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}