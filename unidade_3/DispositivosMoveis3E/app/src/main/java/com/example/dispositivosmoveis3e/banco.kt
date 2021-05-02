package com.example.dispositivosmoveis3e

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class banco(context: Context) : SQLiteOpenHelper( context,  DATABASE_NAME, null,  DATABASE_VERSION) {


    override fun  onCreate(sqld: SQLiteDatabase) {
        sqld.execSQL("CREATE TABLE temperatura ("
                + "IdTemperatura INTEGER PRIMARY KEY autoincrement,"
                + " nomeCidade varchar(45) NOT NULL ,"
                + " descricao varchar(45) NOT NULL,"
                + " temperatura varchar(45) NOT NULL"
                + ");");
    }

    override fun onUpgrade(sqld: SQLiteDatabase, i:Int, i1:Int) {
        //IMPLEMENTAR
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "temperaturas.db"
    }


}