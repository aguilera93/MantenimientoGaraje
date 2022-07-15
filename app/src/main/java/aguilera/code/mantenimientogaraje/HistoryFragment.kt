package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.ConceptAdapter
import aguilera.code.mantenimientogaraje.data.ui.ConceptClickInterface
import aguilera.code.mantenimientogaraje.data.ui.ConceptDeleteIconClickInterface
import aguilera.code.mantenimientogaraje.data.ui.GarageViewModel
import aguilera.code.mantenimientogaraje.databinding.FragmentHistoryBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager


class HistoryFragment : Fragment(), ConceptClickInterface, ConceptDeleteIconClickInterface {

    private lateinit var viewModel: GarageViewModel
    private lateinit var conceptAdapter: ConceptAdapter

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    var matricula = ""
    var marca = ""
    var modelo = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        conceptAdapter = ConceptAdapter(this, this)

        initView()
        observeEvents()
        //changeFragmentActionBar()

    }

    private fun initView() {

        matricula = arguments?.getString("matricula").toString()
        marca = arguments?.getString("marca").toString()
        modelo = arguments?.getString("modelo").toString()

        binding.menuL.hint = "Historico - $marca $modelo - $matricula"

        binding.conceptsRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = conceptAdapter
        }

    }

    private fun observeEvents() {
        activity?.let {
            viewModel.allConcepts.observe(it, Observer { list ->
                list?.let { listado ->
                    // updates the list.
                    val listConcept = arrayListOf<String>()
                    listado.filter { it.matricula == matricula && it.visible }.forEach { c ->
                        listConcept.add(c.concepto)
                    }
                    val adapter = ArrayAdapter(requireActivity(), R.layout.list_item, listConcept)
                    binding.menu.setAdapter(adapter)

                    binding.menu.setOnItemClickListener { adapterView, view, i, l ->
                        conceptAdapter.updateList(listado.filter {
                            it.matricula == matricula && it.concepto == listConcept.get(
                                i
                            )
                        })
                        Log.i("miapp", "${listConcept.get(i)}")
                    }
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
                putSerializable("concept", concepto as Concepto)
            }
            it.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment)
                .addToBackStack("NewConceptVehicleFragment").commit()
        }
    }

    fun changeFragmentActionBar() {
        (activity as MainActivity).changeActionBar("Historico", "")
    }

}
