package aguilera.code.mantenimientogaraje.data.ui

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.databinding.ConceptRvItemBinding
import aguilera.code.mantenimientogaraje.databinding.HistoryRvItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ConceptHistoryAdapter(
    val conceptHistoryDeleteIconClickInterface: ConceptHistoryDeleteIconClickInterface,
    val conceptHistoryClickInterface: ConceptHistoryClickInterface
) : RecyclerView.Adapter<ConceptHistoryViewHolder>() {

    private val allConcepts = ArrayList<Concepto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConceptHistoryViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = HistoryRvItemBinding.inflate(inflater, parent, false)
        return ConceptHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConceptHistoryViewHolder, position: Int) {
        // sets data to item of recycler view.
        holder.binding.tvTitle.text = "${allConcepts.get(position).detalles}"
        holder.binding.tvDescription.text = "${allConcepts.get(position).fecha}"

        // adding click listener to our delete image view icon.
        holder.binding.imgDelete.setOnClickListener {
            // call eventDeleteIconClickInterface.onEventDeleteIconClick() and pass position to it.
            conceptHistoryDeleteIconClickInterface.onConceptDeleteIconClick(allConcepts.get(position))
        }

        // adding click listener to our recycler view item.
        holder.itemView.setOnClickListener {
            // call eventClickInterface.onEventClick() and pass position to it.
            conceptHistoryClickInterface.onConceptClick(allConcepts.get(position))
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

interface ConceptHistoryDeleteIconClickInterface {
    // creating a method for click
    // action on delete image view.
    fun onConceptDeleteIconClick(concepto: Concepto)
}

interface ConceptHistoryClickInterface {
    // creating a method for click action
    // on recycler view item for updating it.
    fun onConceptClick(concepto: Concepto)
}

class ConceptHistoryViewHolder(val binding: HistoryRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {}