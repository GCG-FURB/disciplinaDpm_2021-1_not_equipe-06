package com.example.dispositivosmoveis3fservidor

import android.app.Activity
import android.os.Bundle
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import java.util.*


class MainActivity : Activity() {

    companion object {
        val NAME = "EccoServerBT"
        val MY_UUID: UUID = UUID.fromString(
            "fa87c0d0-afac-11de-8a39-0800200c9a66"
        )
    }

    var tvMsgEntrada: TextView? = null
    var tvCliente: TextView? = null

    private var adapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvMsgEntrada = findViewById<View>(R.id.tvMsgEntrada) as TextView
        tvCliente = findViewById<View>(R.id.tvCliente) as TextView

        try {
            adapter = BluetoothAdapter.getDefaultAdapter()
            if (!adapter?.isEnabled()!!) {
                val enableIntent = Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE
                )
                startActivityForResult(enableIntent, 2)
            }
            ConexaoThread(this, adapter!!).start()
        } catch (e: Exception) {
            Toast.makeText(this, "Erro:" + e.message, Toast.LENGTH_SHORT).show()
        }

    }
}