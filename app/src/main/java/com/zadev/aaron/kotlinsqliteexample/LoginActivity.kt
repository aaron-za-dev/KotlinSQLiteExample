package com.zadev.aaron.kotlinsqliteexample

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var btnLogin : Button? = null
    private var txtRegister : TextView? = null
    private var myHelper : MyDatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initUi()

        myHelper = MyDatabaseHelper.getInstance(this)

    }

    private fun initUi () {

        btnLogin = findViewById(R.id.btnLogin)
        txtRegister = findViewById(R.id.txtRegister)

        btnLogin?.setOnClickListener(this)
        txtRegister?.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when(v?.id){

            R.id.txtRegister -> launchRegister(v.context)
            R.id.btnLogin -> launchMain(v.context)

        }

    }

    private fun launchMain (c : Context){

        Toast.makeText(c, "Lanzando Menu Principal", Toast.LENGTH_SHORT).show()


    }

    private fun launchRegister(c: Context){

        startActivity(Intent(c, RegisterActivity::class.java))

    }


}
