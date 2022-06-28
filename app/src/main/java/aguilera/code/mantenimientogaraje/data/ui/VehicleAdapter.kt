package aguilera.code.mantenimientogaraje.data.ui

import aguilera.code.mantenimientogaraje.data.db.entity.Vehiculo
import aguilera.code.mantenimientogaraje.databinding.VehicleRvItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class VehicleAdapter(
    val vehicleDeleteIconClickInterface: VehicleDeleteIconClickInterface,
    val vehicleClickInterface: VehicleClickInterface
) : RecyclerView.Adapter<VehicleViewHolder>() {

    private val allVehicles = ArrayList<Vehiculo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = VehicleRvItemBinding.inflate(inflater, parent, false)
        return VehicleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        // sets data to item of recycler view.
        holder.binding.tvTitle.text = "${allVehicles.get(position).marca} ${allVehicles.get(position).modelo}"
        holder.binding.tvDescription.text = "${allVehicles.get(position).matricula}"

        // adding click listener to our delete image view icon.
        holder.binding.imgDelete.setOnClickListener {
            // call eventDeleteIconClickInterface.onEventDeleteIconClick() and pass position to it.
            vehicleDeleteIconClickInterface.onVehicleDeleteIconClick(allVehicles.get(position))
        }

        // adding click listener to our recycler view item.
        holder.itemView.setOnClickListener {
            // call eventClickInterface.onEventClick() and pass position to it.
            vehicleClickInterface.onVehicleClick(allVehicles.get(position))
        }
    }

    override fun getItemCount(): Int {
        // return list size.
        return allVehicles.size
    }

    // update the list of events.
    fun updateList(newList: List<Vehiculo>) {

        // clear the allEvents array list
        allVehicles.clear()

        // adds a new list to our allEvents list.
        allVehicles.addAll(newList)


        // call notifyDataSetChanged() to notify our adapter.
        notifyDataSetChanged()
    }
}

interface VehicleDeleteIconClickInterface {
    // creating a method for click
    // action on delete image view.
    fun onVehicleDeleteIconClick(vehiculo: Vehiculo)
}

interface VehicleClickInterface {
    // creating a method for click action
    // on recycler view item for updating it.
    fun onVehicleClick(vehiculo: Vehiculo)
}

class VehicleViewHolder(val binding: VehicleRvItemBinding) : RecyclerView.ViewHolder(binding.root) {}