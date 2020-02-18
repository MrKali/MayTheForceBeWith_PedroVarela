package com.varela.maytheforcebewith_pedrovarela.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varela.maytheforcebewith_pedrovarela.R
import kotlinx.android.synthetic.main.person_item.view.*

class PersonAdapter(private val persons: ArrayList<com.varela.maytheforcebewith_pedrovarela.model.Person>) :
    RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.person_item, parent, false)
        return PersonViewHolder(view)
    }

    override fun getItemCount(): Int {
        return persons.size
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(persons[position])
    }

    class PersonViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(person: com.varela.maytheforcebewith_pedrovarela.model.Person) {
            itemView.name_person.text = person.name
        }
    }
}