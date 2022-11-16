package aguilera.code.mantenimientogaraje.data.ui

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.databinding.MoneyRvItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MoneyAdapter(
    private val MoneyClickInterface: MoneyClickInterface
) : RecyclerView.Adapter<MoneyViewHolder>() {

    private val allConcepts = ArrayList<Concepto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoneyViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = MoneyRvItemBinding.inflate(inflater, parent, false)
        return MoneyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoneyViewHolder, position: Int) {
        // sets data to item of recycler view.
        holder.binding.tvTitle.text = "${allConcepts.get(position).concepto}"
        holder.binding.tvDescription.text = "${allConcepts.get(position).precio}€"

        // adding click listener to our recycler view item.
        holder.itemView.setOnClickListener {
            // call eventClickInterface.onEventClick() and pass position to it.
            MoneyClickInterface.onConceptClick(allConcepts.get(position))
        }
    }

    override fun getItemCount(): Int {
        // return list size.
        return allConcepts.size
    }

    // update the list of events.
    fun updateList(newList: List<Concepto>) {

        // clear the allEvents array list
        allConcepts.clear()

        // adds a new list to our allEvents list.
        allConcepts.addAll(newList)

        // call notifyDataSetChanged() to notify our adapter.
        notifyDataSetChanged()
    }
}

interface MoneyClickInterface {
    // creating a method for click action
    // on recycler view item for updating it.
    fun onConceptClick(concepto: Concepto)
}

class MoneyViewHolder(val binding: MoneyRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {}