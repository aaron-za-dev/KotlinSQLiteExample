package com.zadev.aaron.kotlinsqliteexample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.zadev.aaron.kotlinsqliteexample.models.Holder
import com.zadev.aaron.kotlinsqliteexample.models.HolderHaveAccount

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var btnLogin : Button? = null
    private var txtRegister : TextView? = null
    private var myHelper : MyDatabaseHelper? = null
    private var context : Context? = null
    private var txtEmail : TextInputEditText? = null
    private var txtPass : TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        context = applicationContext

        initUi()

        myHelper = MyDatabaseHelper.getInstance(context!!)

    }

    private fun initUi () {

        btnLogin = findViewById(R.id.btnLogin)
        txtRegister = findViewById(R.id.txtRegister)
        txtEmail = findViewById(R.id.txtEmail)
        txtPass = findViewById(R.id.txtPass)

        btnLogin?.setOnClickListener(this)
        txtRegister?.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when(v?.id){

            R.id.txtRegister -> launchRegister(v.context)
            R.id.btnLogin -> doLogin(txtEmail?.text.toString(), txtPass?.text.toString())

        }

    }

    private fun doLogin (email : String, pass: String) {

        if(Patterns.EMAIL_ADDRESS.matcher(email).matches() && !TextUtils.equals(pass, "")){

            val holder =  myHelper?.doLogin(Holder(0, "", email, pass))

            if (holder?.IDHolder != 0){

                launchMain(context!!, holder!!)

            }else{

                txtEmail?.error = "No Valido"
                txtEmail?.setText("")
                txtPass?.setText("")
                txtPass?.error = "No Valido"
                Toast.makeText(context!!, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun launchMain (c : Context, h : HolderHaveAccount){

        val i = Intent(c, MainActivity::class.java)
        i.putExtra("IDHolder", h.IDAccount)
        i.putExtra("Name", h.Name)
        i.putExtra("IDAccount", h.IDAccount)

        startActivity(i)
        finish()
    }

    private fun launchRegister(c: Context){

        startActivity(Intent(c, RegisterActivity::class.java))

    }


}
