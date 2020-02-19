package com.varela.maytheforcebewith_pedrovarela.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varela.maytheforcebewith_pedrovarela.Constants
import com.varela.maytheforcebewith_pedrovarela.DetailsActivity
import com.varela.maytheforcebewith_pedrovarela.R
import kotlinx.android.synthetic.main.person_item.view.*

class PersonAdapter(private val persons: ArrayList<com.varela.maytheforcebewith_pedrovarela.model.Person>) :
    RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        this.context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.person_item, parent, false)
        return PersonViewHolder(view)
    }

    override fun getItemCount(): Int {
        return persons.size
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(persons[position])

        // on click listener send the person object as extra
        holder.itemView.setOnClickListener{
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_PERSON, persons[position])
            context?.startActivity(intent)
        }
    }

    class PersonViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(person: com.varela.maytheforcebewith_pedrovarela.model.Person) {
            itemView.name_person.text = person.name
        }
    }
}