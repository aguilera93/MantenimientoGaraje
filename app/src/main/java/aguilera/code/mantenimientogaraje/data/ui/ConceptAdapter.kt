package aguilera.code.mantenimientogaraje.data.ui

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.databinding.ConceptRvItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ConceptAdapter(
    val conceptMenuIconClickInterface: ConceptMenuIconClickInterface,
    val conceptClickInterface: ConceptClickInterface
) : RecyclerView.Adapter<ConceptViewHolder>() {

    private val allConcepts = ArrayList<Concepto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConceptViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = ConceptRvItemBinding.inflate(inflater, parent, false)
        return ConceptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConceptViewHolder, position: Int) {
        // sets data to item of recycler view.
        holder.binding.tvTitle.text = "${allConcepts.get(position).concepto}"
        holder.binding.tvDescription.text = "${allConcepts.get(position).fecha}"

        // adding click listener to our delete image view icon.
        holder.binding.imgMenu.setOnClickListener {
            // call eventDeleteIconClickInterface.onEventDeleteIconClick() and pass position to it.
            conceptMenuIconClickInterface.onConceptMenuIconClick(allConcepts.get(position))
        }

        // adding click listener to our recycler view item.
        holder.itemView.setOnClickListener {
            // call eventClickInterface.onEventClick() and pass position to it.
            conceptClickInterface.onConceptClick(allConcepts.get(position))
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

interface ConceptMenuIconClickInterface {
    // creating a method for click
    // action on delete image view.
    fun onConceptMenuIconClick(concepto: Concepto)
}

interface ConceptClickInterface {
    // creating a method for click action
    // on recycler view item for updating it.
    fun onConceptClick(concepto: Concepto)
}

class ConceptViewHolder(val binding: ConceptRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {}