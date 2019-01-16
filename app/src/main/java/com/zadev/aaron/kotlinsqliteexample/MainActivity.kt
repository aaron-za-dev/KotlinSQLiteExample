package com.zadev.aaron.kotlinsqliteexample

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.widget.Toast
import com.zadev.aaron.kotlinsqliteexample.models.ItemOperation
import com.zadev.aaron.kotlinsqliteexample.models.Operation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var context : Context? = null

    private var myHelper : MyDatabaseHelper? = null

    private var accountID = 0
    private var totalAmount = 0f
    private var lastOperation = 0

    private var operations : ArrayList<ItemOperation> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUi()

        context = applicationContext

        myHelper = MyDatabaseHelper.getInstance(context!!)

        accountID = intent.extras.getInt("IDAccount")

        txtHeader.setText("Bienvenido de nuevo ${intent.extras.getString("Name")}")

        checkTotalAmount(accountID)

    }


    private fun initUi () {

        //Configuring the recyclerView

        val viewManager = LinearLayoutManager(context)
        val adapter = OperationAdapter()

        rvOne.apply {

            setHasFixedSize(true)

            layoutManager = viewManager

            setAdapter(adapter)
        }


        // Setting CheckedChange listener to RadioGrouo
        rbGroup?.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId){

                R.id.rbOne -> {

                    when (sprOne.selectedItemPosition){

                        0 -> {
                            //Toast.makeText(context, "Posicion 1", Toast.LENGTH_SHORT).show()
                            allOperations(accountID, adapter)
                            rbOne.isChecked = false
                        }
                        1 -> {
                            //Toast.makeText(context, "Posicion 2", Toast.LENGTH_SHORT).show()
                            allPositiveOperations(accountID, adapter)
                            rbOne.isChecked = false
                        }
                        2 -> {
                            //Toast.makeText(context, "Posicion 3", Toast.LENGTH_SHORT).show()
                            allNegativeOperations(accountID, adapter)
                            rbOne.isChecked = false
                        }

                    }

                }
                R.id.rbTwo -> {

                    if(!TextUtils.equals(txtAmount.text, "")){

                        lastOperation = myHelper?.lastOperation()!!
                        val op = Operation(lastOperation, "", txtAmount.text.toString().toFloat(), 1)
                        updateAmount(accountID, op, totalAmount + txtAmount.text.toString().toFloat())

                    }
                    else{

                        rbTwo.isChecked = false
                        Toast.makeText(context, "Debe de introducir un monto ", Toast.LENGTH_SHORT).show()
                        txtAmount.error = "Introduzca el monto"

                    }
                }

                R.id.rbThree -> {

                    if (!TextUtils.equals(txtAmount.text, "")) {

                        if (totalAmount > 0 && totalAmount >= txtAmount.text.toString().toFloat()) {

                            lastOperation = myHelper?.lastOperation()!!
                            val op = Operation(lastOperation, "", txtAmount.text.toString().toFloat(), 2)
                            updateAmount(accountID, op, totalAmount - txtAmount.text.toString().toFloat())

                        } else {

                            rbThree.isChecked = false
                            txtAmount.error = "Saldo Insuficiente"
                            Toast.makeText(context, "Saldo Insuficiente", Toast.LENGTH_SHORT).show()

                        }

                    }
                    else{

                        rbThree.isChecked = false
                        txtAmount.error = "Introduzca un monto"
                        Toast.makeText(context, "Debe de introducir un monto", Toast.LENGTH_SHORT).show()

                    }
                }

            }
        }

    }


    private fun checkTotalAmount (a: Int)  {

        totalAmount = myHelper?.totalAmount(a)!!
        txtTotalAmount.setText("Saldo Disp: $totalAmount")

    }

    private fun updateAmount (idAccount : Int, op: Operation, amount: Float) {

        myHelper?.addOperation(idAccount, amount, op)

        checkTotalAmount(idAccount)

        rbTwo.isChecked = false
        rbThree.isChecked = false

        txtAmount.setText("")

        Toast.makeText(context, "Saldo Actualizado", Toast.LENGTH_SHORT).show()

    }

    private fun allOperations(idAccount: Int, adapter: OperationAdapter) {
        if(operations.size == 0){
           operations = myHelper?.allOperationsByAccount(idAccount)!!
        }
        adapter.setData(operations)
    }

    private fun allPositiveOperations (idAccount: Int, adapter: OperationAdapter){

        if(operations.size == 0){

            operations = myHelper?.allOperationsByAccount(idAccount)!!
        }

        var positiveOps = ArrayList (operations.filter { it.IDTypeOp == 1 })

        adapter.setData(positiveOps)

    }

    private fun allNegativeOperations(idAccount: Int, adapter: OperationAdapter) {

        if(operations.size == 0){

            operations = myHelper?.allOperationsByAccount(idAccount)!!
        }

        var negativeOps = ArrayList(operations.filter { it.IDTypeOp == 2 })

        adapter.setData(negativeOps)

    }
}

