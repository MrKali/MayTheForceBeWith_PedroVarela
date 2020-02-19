package com.varela.maytheforcebewith_pedrovarela.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.varela.maytheforcebewith_pedrovarela.Constants
import com.varela.maytheforcebewith_pedrovarela.DetailsActivity
import com.varela.maytheforcebewith_pedrovarela.R
import com.varela.maytheforcebewith_pedrovarela.model.Person
import kotlinx.android.synthetic.main.item_one_line.view.*


class PersonAdapter(private val persons: ArrayList<Person?>) :
    RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    private var context: Context? = null

    // used to distinguish progress bar from real items
    private val viewItem = 1
    private val viewLoad = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        this.context = parent.context

        // load different layout in case if is a progress bar or a real item
        val view: View = if (viewType == viewItem) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_one_line, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.row_progress, parent, false)
        }

        return PersonViewHolder(view)
    }

    override fun getItemCount(): Int {
        return persons.size
    }

    override fun getItemViewType(position: Int): Int {
        // progress bar item is identified because is null
        return if (persons[position] != null) viewItem else viewLoad
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        if (persons[position] != null) {
            holder.bind(persons[position])

            // on click listener send the person object as extra to a new activity
            holder.itemView.setOnClickListener {
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PERSON, persons[position])
                context?.startActivity(intent)
            }
        }
    }

    /**
     * used to add the progressbar
     * */
    fun addLoadingView() {
        Handler().post {
            persons.add(null)
            notifyItemInserted(persons.size - 1)
        }
    }

    /**
     * used to remove the progressbar
     * */
    fun removeLoadingView() {
        persons.removeAt(persons.size - 1)
        notifyItemRemoved(persons.size)
    }


    class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(person: Person?) {
            itemView.text1.text = person?.name
        }
    }
}