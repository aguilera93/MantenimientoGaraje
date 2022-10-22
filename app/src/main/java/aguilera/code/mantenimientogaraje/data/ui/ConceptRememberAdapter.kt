package aguilera.code.mantenimientogaraje.data.ui

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.databinding.RememberRvItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ConceptRememberAdapter(
    val conceptRememberMenuIconClickInterface: ConceptRememberMenuIconClickInterface,
    val conceptRememberClickInterface: ConceptRememberClickInterface
) : RecyclerView.Adapter<ConceptRememberViewHolder>() {

    private val allConcepts = ArrayList<Concepto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConceptRememberViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = RememberRvItemBinding.inflate(inflater, parent, false)
        return ConceptRememberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConceptRememberViewHolder, position: Int) {
        // sets data to item of recycler view.
        holder.binding.tvTitle.text = "${allConcepts.get(position).concepto}"
        holder.binding.tvDescription.text = "${allConcepts.get(position).rFecha}"

        // adding click listener to our delete image view icon.
        holder.binding.imgMenu.setOnClickListener {
            // call eventDeleteIconClickInterface.onEventDeleteIconClick() and pass position to it.
            conceptRememberMenuIconClickInterface.onConceptMenuIconClick(allConcepts.get(position))
        }

        // adding click listener to our recycler view item.
        holder.itemView.setOnClickListener {
            // call eventClickInterface.onEventClick() and pass position to it.
            conceptRememberClickInterface.onConceptClick(allConcepts.get(position))
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

interface ConceptRememberMenuIconClickInterface {
    // creating a method for click
    // action on delete image view.
    fun onConceptMenuIconClick(concepto: Concepto)
}

interface ConceptRememberClickInterface {
    // creating a method for click action
    // on recycler view item for updating it.
    fun onConceptClick(concepto: Concepto)
}

class ConceptRememberViewHolder(val binding: RememberRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {}