package com.zadev.aaron.kotlinsqliteexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.zadev.aaron.kotlinsqliteexample.models.Account
import com.zadev.aaron.kotlinsqliteexample.models.Holder
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private var btnRegister : Button? = null
    private var txtName : TextInputEditText? = null
    private var txtEmail: TextInputEditText? = null
    private var txtPass: TextInputEditText? = null

    private var myHelper : MyDatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initUi()

        myHelper = MyDatabaseHelper.getInstance(this)

    }

    override fun onClick(v: View?) {

        if(!TextUtils.equals(txtName?.text, "")
            && Patterns.EMAIL_ADDRESS.matcher(txtEmail?.text).matches() && !TextUtils.equals(txtPass?.text, "")){

            val holder = Holder(0, txtName?.text.toString(), txtEmail?.text.toString(), txtPass?.text.toString())
            val account = Account(0, 0f)

            if (myHelper!!.checkHolderIfExists(holder)){

                txtEmail?.error = "Introduzca otro correo"
                Snackbar.make(v!!, "El Correo Electronico ya se encuentra registrado", Snackbar.LENGTH_SHORT).show()

            }
            else{

                myHelper?.addUser(holder, account)

                Toast.makeText(v?.context, "Usuario registrado ahora puede iniciar sesion", Toast.LENGTH_SHORT).show()

                finish()

            }

        }
        else{

            txtName?.error = "Campo Requerido"
            txtPass?.error = "Campo Requerido"
            txtEmail?.error = "Correo Invalido"
        }

    }

    fun initUi () {

        txtName = findViewById(R.id.txtName)
        txtEmail = findViewById(R.id.txtEmailReg)
        txtPass = findViewById(R.id.txtPassReg)
        btnRegister = findViewById(R.id.btnRegister)
        btnRegister?.setOnClickListener(this)

    }


}
