package com.zadev.aaron.kotlinsqliteexample

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.zadev.aaron.kotlinsqliteexample.models.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyDatabaseHelper private constructor (val c: Context?) :SQLiteOpenHelper(c, "MyDatabase.db", null, 1) {

    private var query = ""
    private var cursor : Cursor? = null
    private var myDatabase : SQLiteDatabase? = null

    companion object {

        private var instance: MyDatabaseHelper? = null

        fun getInstance (c : Context) : MyDatabaseHelper?{

            if(instance == null){

                instance = MyDatabaseHelper(c)

            }

            return instance

        }

    }

    override fun onCreate(db: SQLiteDatabase?) {

        query = "CREATE TABLE HOLDER (ID_HOLDER INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, NAME TEXT NOT NULL, EMAIL TEXT NOT NULL, PASS TEXT NOT NULL);"
        db?.execSQL(query)

        query = "CREATE TABLE ACCOUNT (ID_ACCOUNT INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, TOTAL_AMOUNT DECIMAL(5,2) NOT NULL);"
        db?.execSQL(query)

        query = "CREATE TABLE TYPE_OPS (ID_TYPE_OP INTEGER PRIMARY KEY NOT NULL, NAME_OP TEXT NOT NULL);"
        db?.execSQL(query)

        query = "CREATE TABLE OPERATIONS (ID_OPERATION INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, DATE_OP DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                "AMOUNT_OP DECIMAL(5,2) NOT NULL, ID_TYPE_OP INTEGER NOT NULL REFERENCES TYPE_OPS (ID_TYPE_OP));"
        db?.execSQL(query)

        query = "CREATE TABLE HAVE (ID_HOLDER INTEGER NOT NULL UNIQUE REFERENCES HOLDER (ID_HOLDER), ID_ACCOUNT INTEGER NOT NULL REFERENCES ACCOUNT " +
                "(ID_ACCOUNT));"
        db?.execSQL(query)

        query = "CREATE TABLE MAKE (ID_ACCOUNT INTEGER NOT NULL REFERENCES ACCOUNT (ID_ACCOUNT), ID_OPERATION INTEGER NOT NULL REFERENCES OPERATIONS (ID_OPERATION));"
        db?.execSQL(query)

        query = "INSERT INTO TYPE_OPS (ID_TYPE_OP, NAME_OP) VALUES (1, 'Deposito'), (2, 'Retiro');"
        db?.execSQL(query)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        db?.execSQL("DROP TABLE IF EXISTS HOLDER")
        db?.execSQL("DROP TABLE IF EXISTS ACCOUNT")
        db?.execSQL("DROP TABLE IF EXISTS TYPE_OPS")
        db?.execSQL("DROP TABLE IF EXISTS OPERATIONS")
        db?.execSQL("DROP TABLE IF EXISTS HAVE")
        db?.execSQL("DROP TABLE IF EXISTS MAKE")
        onCreate(db)

    }

    fun addUser (h: Holder, a : Account) {

        myDatabase = writableDatabase

        var contentH = ContentValues()
        contentH.put("NAME", h.Name)
        contentH.put("EMAIL", h.Email)
        contentH.put("PASS", h.Pass)

        myDatabase?.insert("HOLDER", null, contentH)

        var contentA = ContentValues()
        contentA.put("TOTAL_AMOUNT", a.TotalAmount)

        myDatabase?.insert("ACCOUNT", null, contentA)

        var contentHave = ContentValues()
        contentHave.put("ID_HOLDER", lastHolder())
        contentHave.put("ID_ACCOUNT", lastAccount())

        myDatabase?.insert("HAVE", null, contentHave)

    }

    fun lastAccount () : Int {

        query = "SELECT ID_ACCOUNT FROM ACCOUNT ORDER BY ID_ACCOUNT DESC LIMIT 1;"

        myDatabase = readableDatabase

        var accountID = 0

        cursor = myDatabase?.rawQuery(query, null)

        if(cursor!!.moveToFirst()){

            do{

               accountID = cursor!!.getInt(0)

            }while (cursor!!.moveToNext())

        }

        return  accountID

    }

    fun lastHolder () : Int {

        query = "SELECT ID_HOLDER FROM HOLDER ORDER BY ID_HOLDER DESC LIMIT 1;"

        myDatabase = readableDatabase

        var holderID = 0

        cursor = myDatabase?.rawQuery(query, null)

        if(cursor!!.moveToFirst()){

            do{

                holderID = cursor!!.getInt(0)

            }while (cursor!!.moveToNext())

        }

        return  holderID

    }

    fun lastOperation () : Int {

        query = "SELECT ID_OPERATION FROM OPERATIONS ORDER BY ID_OPERATION DESC LIMIT 1;"

        myDatabase = readableDatabase

        var operationID = 1

        cursor = myDatabase?.rawQuery(query, null)

        if(cursor!!.moveToFirst()){

            do{

                operationID = cursor!!.getInt(0)+1

            }while (cursor!!.moveToNext())

        }

        return  operationID

    }


    fun checkHolderIfExists (h: Holder): Boolean {

        myDatabase = readableDatabase

        query = "SELECT EMAIL FROM HOLDER WHERE EMAIL LIKE '${h.Email}';"

        cursor = myDatabase?.rawQuery(query, null)

        return cursor!!.moveToFirst()

    }

    fun doLogin (h: Holder) : HolderHaveAccount {

        myDatabase = readableDatabase

        query = "SELECT ID_HOLDER, NAME, ID_ACCOUNT " +
                "FROM HOLDER INNER JOIN HAVE USING (ID_HOLDER) " +
                "INNER JOIN ACCOUNT USING(ID_ACCOUNT) WHERE EMAIL LIKE '${h.Email}' AND PASS LIKE '${h.Pass}'"

        cursor = myDatabase?.rawQuery(query, null)

        var holder = HolderHaveAccount(0, "", 0)

        if(cursor!!.moveToFirst()){

            do {

                holder.IDHolder = cursor!!.getInt(0)
                holder.Name = cursor!!.getString(1)
                holder.IDAccount = cursor!!.getInt(2)

            }while (cursor!!.moveToNext())

        }

        return holder
    }

    fun totalAmount (idAccount : Int) : Float {

        myDatabase = readableDatabase

        query = "SELECT TOTAL_AMOUNT FROM ACCOUNT WHERE ID_ACCOUNT = ${idAccount}"

        cursor = myDatabase?.rawQuery(query, null)

        var totalAmount = 0f

        if(cursor!!.moveToFirst()){

            do {

                totalAmount = cursor!!.getFloat(0)

            }while (cursor!!.moveToNext())

        }

        return totalAmount

    }

    fun addOperation (idAccount : Int, newAmount: Float, op : Operation ) {

        myDatabase = writableDatabase

        var addOperation  = ContentValues()
        addOperation.put("AMOUNT_OP", op.AmountOperation)
        addOperation.put("ID_TYPE_OP", op.IDType)

        myDatabase?.insert("OPERATIONS", null, addOperation)

        var makeOp = ContentValues()
        makeOp.put("ID_ACCOUNT", idAccount)
        makeOp.put("ID_OPERATION", op.IDOperation)

        myDatabase?.insert("MAKE", null, makeOp)

        var updateAccount = ContentValues ()
        updateAccount.put("TOTAL_AMOUNT", newAmount)

        myDatabase?.update("ACCOUNT", updateAccount, "ID_ACCOUNT = $idAccount", null)

    }

    fun allOperationsByAccount (idAccount : Int ) : ArrayList<ItemOperation> {

        var allOps : ArrayList<ItemOperation> = ArrayList()

        myDatabase = readableDatabase

        query = "SELECT ID_ACCOUNT, DATE_OP, ID_TYPE_OP, ID_OPERATION, AMOUNT_OP  " +
                "FROM ACCOUNT INNER JOIN MAKE USING (ID_ACCOUNT) INNER JOIN OPERATIONS USING (ID_OPERATION) " +
                "WHERE ID_ACCOUNT = $idAccount"

        cursor = myDatabase?.rawQuery(query, null)

        if(cursor!!.moveToFirst()){

            do{

                allOps?.add(ItemOperation(
                    cursor!!.getInt(0),
                    cursor!!.getString(1).substring(0,10),
                    cursor!!.getInt(2),
                    cursor!!.getInt(3),
                    cursor!!.getFloat(4)
                ))


            }while (cursor!!.moveToNext())

        }

        return allOps
    }




}