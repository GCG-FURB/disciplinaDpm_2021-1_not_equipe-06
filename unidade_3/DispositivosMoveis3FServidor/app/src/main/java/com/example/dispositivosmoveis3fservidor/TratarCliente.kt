package com.example.dispositivosmoveis3fservidor

import android.bluetooth.BluetoothSocket
import android.widget.Toast
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

class TratarCliente() : Thread(){

    private var activity : MainActivity? = null
    private var cliente : BluetoothSocket? = null
    private var entrada : InputStream? = null
    private var saida : OutputStream? = null
    private var nome : String? = null
    private var mensagem : String? = null

    constructor(activity: MainActivity, cliente : BluetoothSocket) : this(){
        try{
            this.activity = activity
            this.cliente = cliente
            entrada = cliente.getInputStream()
            saida = cliente.getOutputStream()
        }catch(e : Exception){
            Toast.makeText(MainActivity(), "Erro:" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun run() {
        try{
            while(true) {
                var buffer: ByteArray? = null
                entrada?.read(buffer)
                var msg : String = String(buffer!!)
                nome = cliente?.getRemoteDevice()?.getName()

                activity?.runOnUiThread(Runnable() {
                    fun run() {
                        activity?.tvCliente?.setText("Cliente: " + nome.toString())
                        activity?.tvMsgEntrada?.setText("Mensagem recebida: " + msg.trim())
                    }
                })
                sleep(1000)

                saida?.write(("Recebido:" + msg).toByteArray())
                saida?.flush()
            }
        } catch(e : Exception) {
            Toast.makeText(activity, "Erro:" + e.message, Toast.LENGTH_SHORT).show()
        }
    }
}