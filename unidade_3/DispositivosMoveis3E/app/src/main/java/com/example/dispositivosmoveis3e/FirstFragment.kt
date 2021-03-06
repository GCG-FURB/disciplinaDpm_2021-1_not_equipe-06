package com.example.dispositivosmoveis3e

import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.joinAll
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first3).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        view.findViewById<Button>(R.id.button_first).setOnClickListener{
            atualizarInformacao(view)
        }

        view.findViewById<Button>(R.id.button_first2).setOnClickListener{
            salvarInfo(view)
        }
        a(view)
    }

    fun a(view :View){
        var nome  ="";
        var temp ="";
        var desc ="";
        val textview_first = view.findViewById(R.id.textview_first) as TextView;

        textview_first.text = nome

        val idFirst = view.findViewById( R.id.idFirst )as TextView;

        idFirst.text = desc


        val tempe = view.findViewById( R.id.temperatura )as TextView;

        tempe.text = temp


        val dbHelper = banco(requireActivity().applicationContext)
        val db = dbHelper.readableDatabase
        
        val cursor = db.query("temperatura",null,"IdTemperatura = 1",null,null,null,null)
        val itemIds = mutableListOf<Long>()
        with(cursor) {
            while (moveToNext()) {
                nome = getString(getColumnIndexOrThrow("nomeCidade"))
                temp = getString(getColumnIndexOrThrow("temperatura"))
                desc = getString(getColumnIndexOrThrow("descricao"))
            }
        }
        textview_first.text = nome
        idFirst.text = desc
        tempe.text = temp

    }


    fun atualizarInformacao(view: View){



        val textview_first = view.findViewById(R.id.textview_first) as TextView;

        val idFirst = view.findViewById( R.id.idFirst )as TextView;

        val tempe = view.findViewById( R.id.temperatura )as TextView;
        var nome  =textview_first.text;
        var temp =idFirst.text ;
        var desc = idFirst.text;


        val client = OkHttpClient()
        val url = URL("http://192.168.1.154:3333/0");

        val job = runBlocking  {
        try {
            val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

            val response = client.newCall(request).execute()

            val responseBody = response.body!!.string()


            val jsonObj = JSONObject(responseBody)
            nome = jsonObj.getString("nome")
            desc = jsonObj.getString("desc")
            temp = jsonObj.getString("temp")

        }
        catch(e:Exception ){
            // a
        }
        }

        textview_first.text = nome;

        idFirst.text = desc;
        tempe.text = temp

        val dbHelper = banco(requireActivity().applicationContext)
        val db = dbHelper.readableDatabase
        val values3 = ContentValues().apply {
            put("nomeCidade",nome.toString())
            put("temperatura",temp.toString())
            put("descricao",desc.toString())
        }

        val count = db.update("temperatura",values3,"IdTemperatura = 1",null)

    }

    fun salvarInfo(view: View){

        val client = OkHttpClient()
        val url = URL("http://192.168.1.154:3333/0");

        val textview_first = view.findViewById(R.id.textview_first) as TextView;


        val idFirst = view.findViewById( R.id.idFirst )as TextView;


        val tempe = view.findViewById( R.id.temperatura )as TextView;
        var body = "{\"nome\": \"${textview_first.text}\",\"temp\":\"${tempe.text}\", \"desc\":\"${idFirst.text}\"}"

        val job = runBlocking {
            try {
                val request = Request.Builder()
                        .url(url)
                        .put(RequestBody.Companion.create("application/json; charset=utf-8".toMediaType(), body))
                        .build()

                val response = client.newCall(request).execute()
            }catch(e:Exception ){
                // a
            }
        }

        val dbHelper = banco(requireActivity().applicationContext)
        val db = dbHelper.readableDatabase
        val values3 = ContentValues().apply {
            put("nomeCidade",textview_first.text.toString())
            put("temperatura",tempe.text.toString())
            put("descricao",idFirst.text.toString())
        }

        val count = db.update("temperatura",values3,"IdTemperatura = 1",null)

    }
}