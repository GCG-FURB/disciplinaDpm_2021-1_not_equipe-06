package com.example.dispositivosmoveis3fservidor

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.widget.Toast
import java.io.IOException


class ConexaoThread() : Thread() {

    private var adapter: BluetoothAdapter? = null
    private var server: BluetoothServerSocket? = null
    private var activity: MainActivity? = null

    @Throws(IOException::class)
    constructor(activity: MainActivity, adapter: BluetoothAdapter) : this() {
        this.adapter = adapter
        this.activity = activity
        server = adapter.listenUsingRfcommWithServiceRecord(MainActivity.NAME, MainActivity.MY_UUID)
    }

    override fun run() {
        try {
            while (true) {
                val cliente = server!!.accept()
                TratarCliente(activity!!, cliente).start()
            }
        } catch (e: Exception) {
            Toast.makeText(activity, "Erro:" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

}