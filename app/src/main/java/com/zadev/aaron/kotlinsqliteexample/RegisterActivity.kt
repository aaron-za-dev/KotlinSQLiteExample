package com.zadev.aaron.kotlinsqliteexample

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.zadev.aaron.kotlinsqliteexample.models.Account
import com.zadev.aaron.kotlinsqliteexample.models.Holder
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private var myHelper : MyDatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister.setOnClickListener(this)

        myHelper = MyDatabaseHelper.getInstance(this)

    }

    override fun onClick(v: View?) {

        if(!TextUtils.equals(txtNameReg.text, "")
            && Patterns.EMAIL_ADDRESS.matcher(txtEmailReg.text).matches() && !TextUtils.equals(txtPassReg.text, "")){

            val holder = Holder(0, txtNameReg.text.toString(), txtEmailReg.text.toString(), txtPassReg.text.toString())
            val account = Account(0, 0f)

            if (myHelper!!.checkHolderIfExists(holder)){

                txtEmailReg.error = "Introduzca otro correo"
                Snackbar.make(v!!, "El Correo Electronico ya se encuentra registrado", Snackbar.LENGTH_SHORT).show()

            }
            else{

                myHelper?.addUser(holder, account)

                Toast.makeText(v?.context, "Usuario registrado ahora puede iniciar sesion", Toast.LENGTH_SHORT).show()

                finish()

            }

        }
        else{

            txtNameReg.error = "Campo Requerido"
            txtPassReg.error = "Campo Requerido"
            txtEmailReg.error = "Correo Invalido"
        }

    }


}
