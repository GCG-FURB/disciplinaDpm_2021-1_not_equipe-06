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
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
        view.findViewById<Button>(R.id.button_first5).setOnClickListener {
            atualizarInformacao(view)
        }

        view.findViewById<Button>(R.id.button_first6).setOnClickListener {
            salvarInfo(view)
        }
        a(view)
    }


    fun a(view :View){
        var nome  ="";
        var temp ="";
        var desc ="";

        val textview_second =  view.findViewById(R.id.textview_second )as TextView;

        textview_second.text = nome;

        val idSecond = view.findViewById(R.id.idSecond) as TextView;

        idSecond.text = desc;

        val tempe = view.findViewById( R.id.temperratura2 )as TextView;



        val dbHelper = banco(requireActivity().applicationContext)
        val db = dbHelper.readableDatabase

        val cursor = db.query("temperatura",null,"IdTemperatura = 2",null,null,null,null)
        val itemIds = mutableListOf<Long>()
        with(cursor) {
            while (moveToNext()) {
                nome = getString(getColumnIndexOrThrow("nomeCidade"))
                temp = getString(getColumnIndexOrThrow("temperatura"))
                desc = getString(getColumnIndexOrThrow("descricao"))
            }
        }
        textview_second.text = nome
        idSecond.text = desc
        tempe.text = temp
    }
    fun atualizarInformacao(view: View){



        val textview_second =  view.findViewById(R.id.textview_second )as TextView;


        val idSecond = view.findViewById(R.id.idSecond) as TextView;

        val  tempe= view.findViewById( R.id.temperratura2 )as TextView;

        var nome  =textview_second.text;
        var temp =tempe.text ;
        var desc = idSecond.text;

        val client = OkHttpClient()
        val url = URL("http://192.168.1.154:3333/1");
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
            }  catch(e:Exception ){
                    // a
                }
        }

        textview_second.text = nome;

        idSecond.text = desc;
        tempe.text = temp

        val dbHelper = banco(requireActivity().applicationContext)
        val db = dbHelper.readableDatabase
        val values3 = ContentValues().apply {
            put("nomeCidade",nome.toString())
            put("temperatura",temp.toString())
            put("descricao",desc.toString())
        }

        val count = db.update("temperatura",values3,"IdTemperatura = 2",null)


    }
	
	
    fun salvarInfo(view: View){

        val client = OkHttpClient()
        val url = URL("http://192.168.1.154:3333/1");

		val textview_second =  view.findViewById(R.id.textview_second )as TextView;



        val idSecond = view.findViewById(R.id.idSecond) as TextView;


        val tempe = view.findViewById( R.id.temperratura2 )as TextView;
        var body = "{\"nome\": \"${textview_second.text}\",\"temp\":\"${tempe.text}\", \"desc\":\"${idSecond.text}\"}"

        val job = runBlocking {
            try{
                val request = Request.Builder()
                        .url(url)
                        .put(RequestBody.Companion.create("application/json; charset=utf-8".toMediaType(), body))
                        .build()

                val response = client.newCall(request).execute()
            } catch(e:Exception ){
            // a
        }
        }


        val dbHelper = banco(requireActivity().applicationContext)
        val db = dbHelper.readableDatabase
        val values3 = ContentValues().apply {
            put("nomeCidade",textview_second.text.toString())
            put("temperatura",tempe.text.toString())
            put("descricao",idSecond.text.toString())
        }

        val count = db.update("temperatura",values3,"IdTemperatura = 2",null)
        }
}