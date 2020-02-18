package com.varela.maytheforcebewith_pedrovarela

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.varela.maytheforcebewith_pedrovarela.adapter.PersonAdapter
import com.varela.maytheforcebewith_pedrovarela.model.Person
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getDataFromAPI(getDataInterface = object : GetDataInterface {
            override fun onDataRecover(persons: ArrayList<Person>) {
                updateRecyclerView(persons)
            }
        })

    }

    /**
     * Setup the recycler view and load the data
     * */
    private fun updateRecyclerView(persons: ArrayList<Person>){
        recycler_view.layoutManager = LinearLayoutManager (this)
        recycler_view.adapter = PersonAdapter(persons)
    }

    /**
     * This function will get all the people available on API
     * */
    private fun getDataFromAPI(getDataInterface: GetDataInterface){
        // URL of request
        val url = "https://swapi.co/api/people/"

        // Create array to save the objects from api
        val personList: ArrayList<Person> = arrayListOf()

        // execute the request
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->

                // get the objects inside results
                val persons = response.getJSONArray("results")

                // get object one by one and add it to the array
                for (i in 0 until persons.length()) {
                    val item = Gson().fromJson(persons.getJSONObject(i).toString(), Person::class.java)
                    personList.add(item)

                    // call the callback if is the last object in array
                    if (i == persons.length() - 1) getDataInterface.onDataRecover(personList)
                }
            },
            // Toast message in case of error during request
            Response.ErrorListener {
                Toast.makeText(this, "Error on web request", Toast.LENGTH_SHORT).show()
            }
        )

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    /**
     * Interface used to call when web request is completed
     * */
    interface GetDataInterface {
        fun onDataRecover(persons: ArrayList<Person>)
    }
}
