package com.varela.maytheforcebewith_pedrovarela

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.varela.maytheforcebewith_pedrovarela.model.Person

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val person = intent.getSerializableExtra(Constants.EXTRA_PERSON) as? Person
        Toast.makeText(this, person?.name.toString(), Toast.LENGTH_SHORT).show()
    }
}
