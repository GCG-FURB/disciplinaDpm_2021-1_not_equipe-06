package com.example.dispositivosmoveis3f

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import java.util.*

class ListaDispositivos : Activity() {

    companion object {
        private const val RECUPERA_DISPOSITIVO = 0
    }

    private var adapter : BluetoothAdapter? = null
    private var arrayDispositivos : ArrayAdapter<String>? = null
    private var enderecos : HashMap<String, String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_dispositivos)

        val lvDispositivos: ListView = findViewById<ListView>(R.id.lvDispositivos)
        val btPesquisar : Button = findViewById<Button>(R.id.btPesquisar)

        arrayDispositivos = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        lvDispositivos.adapter = arrayDispositivos
        enderecos = HashMap<String, String>();
        adapter = BluetoothFactory.getBluetooth()

        lvDispositivos.onItemClickListener = OnItemClickListener {
                lista, _, id, _ -> onItemClickDispositivos(lista, id)
        }

        registerReceiver( actionFoundReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND ) );

        procurarDispositivos()

        btPesquisar.setOnClickListener { procurarDispositivos() }

    }

    private val actionFoundReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, itn: Intent?) {
            val action : String? = itn?.action
            if (BluetoothDevice.ACTION_FOUND == action) {

                val dispositivo = itn.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                if(arrayDispositivos != null) {
                    arrayDispositivos?.add(dispositivo?.getName());
                    enderecos?.put(dispositivo!!.getName(), dispositivo.getAddress());
                    arrayDispositivos?.notifyDataSetChanged();
                }

                if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action){
                    arrayDispositivos?.add("Pesquisa concluída.");
                }
            }
        }
    }

    private fun onItemClickDispositivos(lista: AdapterView<*>, id: Int) {
        var dispositivoSelecionado : String = lista.getItemAtPosition(id) as String

        var itn : Intent = intent
        itn.putExtra("nome", dispositivoSelecionado)
        itn.putExtra("endereco", enderecos?.get(dispositivoSelecionado))
        setResult(RECUPERA_DISPOSITIVO, itn)

        finish()
    }

    private fun procurarDispositivos() {
        adapter?.cancelDiscovery()
        arrayDispositivos?.clear()
        enderecos?.clear()
        adapter?.startDiscovery()
    }
}