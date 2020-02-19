package com.varela.maytheforcebewith_pedrovarela

import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.varela.maytheforcebewith_pedrovarela.adapter.PersonAdapter
import com.varela.maytheforcebewith_pedrovarela.model.Person
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var displayList: MutableList<String> = ArrayList()

    var personsData: ArrayList<Person?> = ArrayList()

    var searchView: SearchView? = null

    var personAdapter: PersonAdapter? = null

    var totalOfPagesOfAPILoaded: Int = 1
    var stillExistDataToLoadFromServer = true

    var layoutManager: RecyclerView.LayoutManager? = null

    var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()

        getDataFromAPI(totalOfPagesOfAPILoaded++, getDataInterface = object : GetDataInterface {
            override fun onDataRecover(persons: ArrayList<Person>) {
                personsData.addAll(persons)
                getDataFromAPI(totalOfPagesOfAPILoaded++, getDataInterface = object : GetDataInterface {
                    override fun onDataRecover(persons: ArrayList<Person>) {
                        personsData.addAll(persons)
                        updateRecyclerView()
                    }
                })
            }
        })


        recycler_view.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (recycler_view.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE){

                    if (!isLoading && stillExistDataToLoadFromServer){
                        isLoading = true
                        personAdapter?.addLoadingView()
                        recycler_view.smoothScrollToPosition(personsData.size)
                        getDataFromAPI(totalOfPagesOfAPILoaded++, getDataInterface = object : GetDataInterface {
                            override fun onDataRecover(persons: ArrayList<Person>) {
                                isLoading = false
                                personAdapter?.removeLoadingView()
                                personsData.addAll(persons)
                                updateRecyclerView()
                            }
                        })
                    }
                }
            }
        })
        //recycler_view.addOnScrolledToEnd {

        //}

    }

    /**
     * Setup the recycler view and load the data
     * */
    private fun updateRecyclerView(){


        personAdapter!!.notifyDataSetChanged()

    }

    private fun setupRecyclerView(){
        recycler_view.layoutManager = LinearLayoutManager (this)
        personAdapter = PersonAdapter(personsData)
        recycler_view.adapter = personAdapter
    }

    override fun onResume() {
        super.onResume()
        searchView?.setQuery("", false)
        searchView?.isIconified = true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchItem = menu.findItem(R.id.search)
        if(searchItem != null){
            searchView = searchItem.actionView as SearchView

            searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView!!.clearFocus()
                    onBackPressed()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {

                        var newList: ArrayList<Person> = filterByName(newText)
                        updateRecyclerView()

                    }




                    //country_list.adapter.notifyDataSetChanged()
                    return true
                }

            })
        }

        return true
    }



    private fun filterByName(query: String): ArrayList<Person>{
        val filteredList: ArrayList<Person> = ArrayList()

        for (person: Person? in personsData){
            if (person != null) {
                if (person.name.toLowerCase().contains(query.toLowerCase())){
                    filteredList.add(person)
                }
            }
        }

        return filteredList
    }


    /**
     * This function will get all the people available on API
     * */
    private fun getDataFromAPI(numberOfPage: Int, getDataInterface: GetDataInterface){
        if (!stillExistDataToLoadFromServer){
            Toast.makeText(this, "There is no more data to load", Toast.LENGTH_SHORT).show()
            return
        }
        // URL of request
        val url = "https://swapi.co/api/people/?page=$numberOfPage"

        // Create array to save the objects from api
        val personList: ArrayList<Person> = arrayListOf()

        // execute the request
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->

                // check if there is more data in next page to load
                if (response.getString("next") == "null") stillExistDataToLoadFromServer = false

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
