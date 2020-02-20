package com.varela.maytheforcebewith_pedrovarela

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
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
    // used to save all the data already loaded from server
    var personsData: ArrayList<Person?> = ArrayList()

    // used to save the data filtered by name from server
    var personsFilteredByNameData: ArrayList<Person> = ArrayList()

    // used to manage the pagination of data
    var totalOfPagesOfAPILoaded: Int = 1
    var stillExistDataToLoadFromServer = true

    // used to avoid make more than one request
    var isLoading: Boolean = false

    var searchView: SearchView? = null
    var personAdapter: PersonAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()

        // start by populating data in main recycler view
        getDataFromAPI(totalOfPagesOfAPILoaded++, getDataInterface = object : GetDataInterface {
            override fun onDataRecover(persons: ArrayList<Person>) {
                personsData.addAll(persons)
                getDataFromAPI(
                    totalOfPagesOfAPILoaded++,
                    getDataInterface = object : GetDataInterface {
                        override fun onDataRecover(persons: ArrayList<Person>) {
                            personsData.addAll(persons)
                            personAdapter!!.notifyDataSetChanged()
                        }
                    })
            }
        })


        // load more data when recycler view is in the last item
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (linearLayoutManager?.let { isLastItemVisible(it) }!!){
                    // if nothing is loading and there is still data to load from server
                    if (!isLoading && stillExistDataToLoadFromServer) {
                        showLoadingProgressBar()

                        // load more data using pagination
                        getDataFromAPI(
                            totalOfPagesOfAPILoaded++,
                            getDataInterface = object : GetDataInterface {
                                override fun onDataRecover(persons: ArrayList<Person>) {
                                    dismissLoadingProgressBar()
                                    personsData.addAll(persons)
                                    personAdapter!!.notifyDataSetChanged()
                                }
                            })
                    }
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        resetSearchView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        // setup search view
        val searchItem = menu.findItem(R.id.search)
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView

            // setup search view and suggestions layout
            val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
            val to = intArrayOf(R.id.text1)

            // adapter for suggestions
            val cursorAdapter = SimpleCursorAdapter(
                this@MainActivity,
                R.layout.item_two_line,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
            )
            searchView!!.suggestionsAdapter = cursorAdapter

            // setup query listener
            searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView!!.hideKeyboard()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        // call API and search by name
                        searchByNameInAPI(newText, object : GetDataInterface {
                            override fun onDataRecover(persons: ArrayList<Person>) {
                                // new cursor
                                val cursor = MatrixCursor(
                                    arrayOf(
                                        BaseColumns._ID,
                                        SearchManager.SUGGEST_COLUMN_TEXT_1
                                    )
                                )

                                // remove previous results from previous query
                                personsFilteredByNameData.clear()

                                // add results to suggestions
                                persons.forEachIndexed { index, suggestion ->
                                    personsFilteredByNameData.add(persons[index])
                                    cursor.addRow(arrayOf(index, suggestion.name))
                                }

                                cursorAdapter.changeCursor(cursor)
                            }
                        })
                    }
                    return true
                }
            })

            // setup suggestion listener
            searchView!!.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean {
                    return true
                }

                override fun onSuggestionClick(position: Int): Boolean {
                    // open details activity of selected suggestion
                    val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PERSON, personsFilteredByNameData[position])
                    startActivity(intent)
                    return true
                }
            })
        }

        return true
    }

    /**
     * Check if is the last item visible on recycler view
     * */
    private fun isLastItemVisible(layoutManager: LinearLayoutManager): Boolean{
        val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

        val numItems = personAdapter?.itemCount
        if (numItems != null) return (lastVisiblePosition >= numItems - 1)
        return false
    }

    /**
     * Extension to hide keyboard
     * */
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    /**
     * Reset search view to original state
     * */
    private fun resetSearchView(){
        searchView?.setQuery("", false)
        searchView?.isIconified = true
    }

    /**
     * Show progress bar in end of recycler view
     * */
    private fun showLoadingProgressBar() {
        isLoading = true
        personAdapter?.addLoadingView()
        recycler_view.smoothScrollToPosition(personsData.size)
    }

    /**
     * Remove progress bar from end of recycler view
     * */
    private fun dismissLoadingProgressBar() {
        isLoading = false
        personAdapter?.removeLoadingView()
    }

    /**
     * Setup the recycler view
     * */
    private fun setupRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this)
        recycler_view.layoutManager = linearLayoutManager
        personAdapter = PersonAdapter(personsData)
        recycler_view.adapter = personAdapter
    }

    /**
     * This function will get all the people available on API
     * */
    private fun searchByNameInAPI(query: String, getDataInterface: GetDataInterface) {
        // URL of request
        val url = "https://swapi.co/api/people/?search=$query"

        // Create array to save the objects from api
        val personList: ArrayList<Person> = arrayListOf()

        // execute the request
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->

                // get the objects inside results
                val persons = response.getJSONArray("results")

                // get object one by one and add it to the array
                for (i in 0 until persons.length()) {
                    val item =
                        Gson().fromJson(persons.getJSONObject(i).toString(), Person::class.java)
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
     * This function will get all the people available on API
     * */
    private fun getDataFromAPI(numberOfPage: Int, getDataInterface: GetDataInterface) {
        if (!stillExistDataToLoadFromServer) {
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
                    val item =
                        Gson().fromJson(persons.getJSONObject(i).toString(), Person::class.java)
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
