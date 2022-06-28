package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.*
import aguilera.code.mantenimientogaraje.databinding.FragmentShowVehicleBinding
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

class ShowVehicleFragment : Fragment(), ConceptClickInterface, ConceptDeleteIconClickInterface {

    private lateinit var viewModel: GarageViewModel
    private lateinit var conceptAdapter: ConceptAdapter

    private var _binding: FragmentShowVehicleBinding? = null
    private val binding get() = _binding!!

    var matricula = ""
    var marca = ""
    var modelo = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        conceptAdapter = ConceptAdapter(this, this)

        initView()
        observeEvents()
        changeFragmentActionBar()

    }

    private fun initView() {

        matricula = arguments?.getString("matricula").toString()
        marca = arguments?.getString("marca").toString()
        modelo = arguments?.getString("modelo").toString()

        binding.btnAdd.setOnClickListener {
            activity?.let {
                val fragment = NewConceptVehicleFragment()
                fragment.arguments = Bundle().apply {
                    putString("matricula", matricula)
                }
                it.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment)
                    .addToBackStack("NewConceptVehicleFragment").commit()
            }
        }

        binding.conceptsRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = conceptAdapter
        }

    }

    private fun observeEvents() {
        activity?.let {
            viewModel.allConcepts.observe(it, Observer { list ->
                list?.let {
                    // updates the list.
                    //conceptAdapter.updateList(it)
                    conceptAdapter.updateList(it.filter { it.matricula == matricula })
                }
            })
        }

    }

    private fun clearConcept() {
        val dialog = activity?.let {
            AlertDialog.Builder(
                it,
                androidx.constraintlayout.widget.R.style.ThemeOverlay_AppCompat_Dialog
            )
        }
        if (dialog != null) {
            dialog.setTitle("Eliminar Conceptos")
                .setMessage("¿Esta seguro de querer eliminar todos los conceptos?")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.clearConcepts().also {
                        Toast.makeText(activity, "Concepto Eliminado", Toast.LENGTH_LONG).show()
                    }
                }.setNegativeButton(android.R.string.cancel, null).create().show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clearItem -> clearConcept()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConceptDeleteIconClick(concepto: Concepto) {
        viewModel.deleteConcept(concepto)
        Toast.makeText(requireActivity(), "Concepto Eliminado", Toast.LENGTH_LONG).show()
    }

    override fun onConceptClick(concepto: Concepto) {
        // opening a new intent and passing a data to it.
        activity?.let {
            val fragment = NewConceptVehicleFragment()
            fragment.arguments = Bundle().apply {
                putString("type", "Edit")
                putString("matricula", matricula)
                putString("id", concepto.id_concept.toString())
                putString("concepto", concepto.concepto)
                putString("fecha", concepto.fecha)
                if (concepto.kms != null) {
                    putString("kms", concepto.kms.toString())
                }
                if (concepto.precio != null) {
                    putString("precio", concepto.precio.toString())
                }
                putString("taller", concepto.taller)
                putString("detalles", concepto.detalles)
                putString("recordar", concepto.recordar.toString())
                if (concepto.recordar) {
                    putString("rfecha", concepto.rFecha)
                    if (concepto.rKms != null) {
                        putString("rkms", concepto.rKms.toString())
                    }
                }
            }
            it.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment)
                .addToBackStack("NewConceptVehicleFragment").commit()
        }
    }

    fun changeFragmentActionBar() {
        (activity as MainActivity).changeActionBar("$marca $modelo", "$matricula")
    }
}
