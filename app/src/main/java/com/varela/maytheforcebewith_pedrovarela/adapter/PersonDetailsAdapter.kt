package com.varela.maytheforcebewith_pedrovarela.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varela.maytheforcebewith_pedrovarela.R
import com.varela.maytheforcebewith_pedrovarela.model.TwoItems
import kotlinx.android.synthetic.main.item_two_line.view.*

class PersonDetailsAdapter(private val person: ArrayList<TwoItems>) :
    RecyclerView.Adapter<PersonDetailsAdapter.PersonDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonDetailsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_two_line, parent, false)
        return PersonDetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return person.size
    }

    override fun onBindViewHolder(holder: PersonDetailsViewHolder, position: Int) {
        holder.bind(person[position].item1, person[position].item2)
    }

    class PersonDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(key: String, value: String) {
            itemView.text1.text = value
            itemView.text2.text = key
        }
    }

}