package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Vehiculo
import aguilera.code.mantenimientogaraje.data.ui.*
import aguilera.code.mantenimientogaraje.databinding.FragmentMainBinding
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager

class MainFragment : Fragment(), VehicleClickInterface, VehicleDeleteIconClickInterface {

    private lateinit var viewModel: GarageViewModel
    private lateinit var vehicleAdapter: VehicleAdapter

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        vehicleAdapter = VehicleAdapter(this, this)

        initView()
        observeEvents()

    }

    private fun initView() {
        binding.btnAdd.setOnClickListener {
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, NewVehicleFragment())
                    .addToBackStack("NewVehicleFragment")
                    .commit()
            }
        }

        binding.vehiclesRV.apply {
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

    private fun clearVehicle() {
        val dialog = activity?.let {
            AlertDialog.Builder(
                it,
                androidx.constraintlayout.widget.R.style.ThemeOverlay_AppCompat_Dialog
            )
        }
        if (dialog != null) {
            dialog.setTitle("Eliminar Vehiculos")
                .setMessage("¿Esta seguro de querer eliminar todos los vehiculos?")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.clearVehicles().also {
                        Toast.makeText(activity, "Vehiculo Eliminado", Toast.LENGTH_LONG).show()
                    }
                }.setNegativeButton(android.R.string.cancel, null).create().show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clearItem -> clearVehicle()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onVehicleDeleteIconClick(vehiculo: Vehiculo) {
        viewModel.deleteVehicle(vehiculo)
        Toast.makeText(requireActivity(), "Vehiculo Eliminado", Toast.LENGTH_LONG).show()
    }

    override fun onVehicleClick(vehiculo: Vehiculo) {
        // opening a new intent and passing a data to it.
        activity?.let {
            val fragment = ShowVehicleFragment()
            fragment.arguments = Bundle().apply {
                putString("matricula", vehiculo.matricula)
            }
            it.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment)
                .addToBackStack("ShowVehicleFragment").commit()
        }
    }
}