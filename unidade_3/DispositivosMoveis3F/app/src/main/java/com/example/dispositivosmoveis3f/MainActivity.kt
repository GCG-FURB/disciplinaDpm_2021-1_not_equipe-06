 package com.example.dispositivosmoveis3f

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


 class MainActivity : Activity() {

    companion object {
        private const val RECUPERA_DISPOSITIVO = 0
        val BTUUID : UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66")
    }

    private var etMsgSaida: EditText? = null
    private var tvMsgEntrada: TextView? = null
    private var tvServidor: TextView? = null
    private var btConectar: Button? = null
    private var btEnviar: Button? = null
    private var dispositivo: String? = null
    private var endereco: String? = null
    private var adapter: BluetoothAdapter? = null
    private var device: BluetoothDevice? = null
    private var servidor: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etMsgSaida = findViewById<EditText>(R.id.etMsgSaida)
        tvMsgEntrada = findViewById<TextView>(R.id.tvMsgEntrada)
        tvServidor = findViewById<TextView>(R.id.tvServidor)
        btConectar = findViewById<Button>(R.id.btConectar)
        btEnviar = findViewById<Button>(R.id.btEnviar)
        adapter = BluetoothFactory.getBluetooth()
        btConectar?.setOnClickListener { onClickConectar() }
        btEnviar?.setOnClickListener { onClickEnviar() }
    }

     override fun onActivityResult(requestCod: Int, resultCode: Int, data: Intent) {
         if (requestCod == RECUPERA_DISPOSITIVO) {
             dispositivo = data.extras?.getString("nome")
             endereco = data.extras?.getString("endereco")
         }
         if (conectarDispositivo(endereco.toString())) {
             val nomeServidor = "Conectado com: " + device?.getName().toString()
             tvServidor?.text = "Servidor: $nomeServidor"
             Toast.makeText(
                 this,
                 "Conectado com " + dispositivo.toString() + "!",
                 Toast.LENGTH_SHORT
             ).show()
         } else {
             Toast.makeText(this, "Não foi possível estabelecer conexão!", Toast.LENGTH_SHORT).show()
         }
     }

     private fun conectarDispositivo(enderecoDispositivo: String): Boolean {
         device = adapter?.getRemoteDevice(enderecoDispositivo)

         val tmp: BluetoothSocket? = try {
             device?.createRfcommSocketToServiceRecord(BTUUID)
         } catch (e: Exception) {
             return false
         }

         servidor = tmp
         adapter?.cancelDiscovery()

         try {
             servidor?.connect()
         } catch (e: IOException) {
             try {
                 servidor?.close()
             } catch (ex: IOException) {
                 return false
             }
         }
         return true
     }

    private fun onClickConectar() {
        val i = Intent(this, ListaDispositivos.javaClass)
        startActivityForResult(i, RECUPERA_DISPOSITIVO)
    }

    private fun onClickEnviar() {
        val ips : InputStream
        val ops : OutputStream

        try{
            ips = servidor!!.inputStream
            ops = servidor!!.outputStream

            val msg : String = etMsgSaida?.text.toString();

            ops.write(msg.toByteArray());
            ops.flush();

            val buffer = ByteArray(1024);
            ips.read(buffer);

            tvMsgEntrada?.text = String(buffer);
            tvServidor?.text = servidor?.remoteDevice?.name;

        }catch(e : Exception){
            Toast.makeText(this, "Erro: " + e.message, Toast.LENGTH_SHORT).show();
        }
    }
}