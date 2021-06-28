package com.furb.br.nathan.tourguider

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.furb.br.nathan.tourguider.objects.Route
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {

    private var routeList: ArrayList<Route> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = RoutesAdapter(routeList)

        val button = findViewById<FloatingActionButton>(R.id.addMapButton)
        button.setOnClickListener { navigateToNewRoute() }

        loadRoutes()
    }

    private val json = Json { ignoreUnknownKeys = true }

    private fun loadRoutes() {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://60d94dcaeec56d00174776a6.mockapi.io/Route"

        // Request a string response from the provided URL.
        val stringReq = StringRequest(
            Request.Method.GET, url,
            { response ->
                run {
                    if (response.isEmpty()) return@run

                    routeList.clear()
                    val responseString = response.toString()
                    (json.decodeFromString(responseString) as List<Route>).forEach { routeList.add(it) }
                }
            },
            { Log.d("API", "that didn't work") })
        queue.add(stringReq)
    }

    private fun navigateToNewRoute() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    // https://guides.codepath.com/android/using-the-recyclerview#creating-the-recyclerview-adapter
    class RoutesAdapter(private val routes: List<Route>) :
        RecyclerView.Adapter<RoutesAdapter.ViewHolder>() {

        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // Your holder should contain and initialize a member variable
            // for any view that will be set as you render a row
            val nameTextView: TextView = itemView.findViewById(R.id.routes_recycler_text_view)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            // Inflate the custom layout
            val contactView = inflater.inflate(R.layout.routes_recicler_row, parent, false)
            // Return a new holder instance
            return ViewHolder(contactView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.nameTextView.text = "Rota ${routes[position]}"
        }

        override fun getItemCount(): Int {
            return routes.size
        }
    }

}