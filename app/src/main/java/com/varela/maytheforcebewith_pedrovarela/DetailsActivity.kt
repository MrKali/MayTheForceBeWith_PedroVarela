package com.varela.maytheforcebewith_pedrovarela

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.varela.maytheforcebewith_pedrovarela.adapter.PersonDetailsAdapter
import com.varela.maytheforcebewith_pedrovarela.model.Person
import com.varela.maytheforcebewith_pedrovarela.model.TwoItems
import kotlinx.android.synthetic.main.activity_main.*

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val person = intent.getSerializableExtra(Constants.EXTRA_PERSON) as? Person

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = person?.name

        val array: ArrayList<TwoItems> = ArrayList()

        array.add(TwoItems("Birth Year", person?.birth_year.toString()))
        array.add(TwoItems("Eye Color", person?.eye_color.toString().capitalizeWords()))
        array.add(TwoItems("Gender", person?.gender.toString().capitalizeWords()))
        array.add(TwoItems("Hair Color", person?.hair_color.toString().capitalizeWords()))
        array.add(TwoItems("Height", person?.height.toString().capitalizeWords()))
        array.add(TwoItems("Mass", person?.mass.toString().capitalizeWords()))
        array.add(TwoItems("Name", person?.name.toString().capitalizeWords()))
        array.add(TwoItems("Skin color", person?.skin_color.toString().capitalizeWords()))
        array.add(TwoItems("Url", person?.url.toString().capitalizeWords()))

        updateRecyclerView(array)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Setup the recycler view and load the data
     * */
    private fun updateRecyclerView(person: ArrayList<TwoItems>){
        recycler_view.layoutManager = LinearLayoutManager (this)
        recycler_view.adapter = PersonDetailsAdapter(person)
    }

    /**
     * This method will capitalize all the letter from a string
     * */
    @SuppressLint("DefaultLocale")
    private fun String.capitalizeWords(): String{
        return split(" ").joinToString(" ") { it.capitalize() }
    }

}
