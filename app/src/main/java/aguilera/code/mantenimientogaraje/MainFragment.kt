package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.db.entity.Vehiculo
import aguilera.code.mantenimientogaraje.data.ui.*
import aguilera.code.mantenimientogaraje.databinding.FragmentMainBinding
import android.app.Dialog
import android.content.ClipData.newIntent
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment(), VehicleClickInterface, VehicleDeleteIconClickInterface {

    private lateinit var viewModel: GarageViewModel
    private lateinit var vehicleAdapter: VehicleAdapter

    private var binding: FragmentMainBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(
            this,
            AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        vehicleAdapter = VehicleAdapter(this, this)

        initView()
        observeEvents()
        changeFragmentActionBar()

    }

    private fun initView() {
        binding?.btnAdd?.setOnClickListener {
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, NewVehicleFragment())
                    .addToBackStack("NewVehicleFragment")
                    .commit()
            }
        }

        binding?.vehiclesRV?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = vehicleAdapter
        }

    }

    private fun observeEvents() {
        activity?.let {
            viewModel.allVehicles.observe(it, Observer { list ->
                list?.let {
                    // updates the list.
                    vehicleAdapter.updateList(it)
                }
            })
        }
    }

    override fun onVehicleDeleteIconClick(vehiculo: Vehiculo) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        @Suppress("DEPRECATION")
        dialogBuilder.setMessage(
            Html.fromHtml(
                "${getString(R.string.que_delete)} " +
                        "''<b>${vehiculo.marca} ${vehiculo.modelo} - ${vehiculo.matricula}</b>''?"
            )
        )
            // if the dialog is cancelable
            .setCancelable(true)
            .setPositiveButton(
                "${getString(R.string.accept)}",
                DialogInterface.OnClickListener { dialog, id ->
                    viewModel.deleteVehicle(vehiculo)
                    viewModel.deleteConceptsByMatricula(vehiculo.matricula)
                    (activity as MainActivity).toast("${vehiculo.matricula} ${getString(R.string.deleted)}")
                    dialog.dismiss()
                    fragmentManager?.popBackStack()
                })
        dialogBuilder.setNegativeButton("${getString(R.string.cancel)}",
            DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("${getString(R.string.delete)}:")
        alert.show()
    }

    override fun onVehicleClick(vehiculo: Vehiculo) {
        activity?.let {
            val fragment = ShowVehicleFragment()
            fragment.arguments = Bundle().apply {
                putString("matricula", vehiculo.matricula)
                putString("marca", vehiculo.marca)
                putString("modelo", vehiculo.modelo)
            }
            it.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment)
                .addToBackStack("ShowVehicleFragment").commit()
        }
    }

    fun changeFragmentActionBar() {
        (activity as MainActivity).changeActionBar(getString(R.string.vehicle_list_title), "")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> {
                // show info screen
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}