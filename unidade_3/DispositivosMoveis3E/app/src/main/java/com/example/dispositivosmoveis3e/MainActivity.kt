package com.example.dispositivosmoveis3e

import android.content.ContentValues
import android.database.DatabaseUtils
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        firstStart()
    }
override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun firstStart(){
        
        val dbHelper = banco(this)
        val db = dbHelper.readableDatabase
        
        var count = DatabaseUtils.queryNumEntries(db, "temperatura");
        
        
        if(count <1){
            val db = dbHelper.writableDatabase
        
            val values1 = ContentValues().apply {
                put("nomeCidade", "teste")
                put("descricao", "descrição testes")
                put("temperatura", "0")
            }

            // Insert the new row, returning the primary key value of the new row
            val newRowId1 = db?.insert("temperatura", null, values1)
            
            val values2 = ContentValues().apply {
                put("nomeCidade", "teste2")
                put("descricao", "descrição testes2")
                put("temperatura", "1")
            }

            // Insert the new row, returning the primary key value of the new row
            val newRowId2 = db?.insert("temperatura", null, values2)

        }
        
    }
}