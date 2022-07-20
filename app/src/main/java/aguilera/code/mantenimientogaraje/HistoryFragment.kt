package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.*
import aguilera.code.mantenimientogaraje.databinding.FragmentHistoryBinding
import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

class HistoryFragment : Fragment(), ConceptHistoryClickInterface,
    ConceptHistoryDeleteIconClickInterface {

    private lateinit var viewModel: GarageViewModel
    private lateinit var conceptHistoryAdapter: ConceptHistoryAdapter

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

        //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        conceptHistoryAdapter = ConceptHistoryAdapter(this, this)

        initView()
        observeEvents()
        changeFragmentActionBar()

    }

    private fun initView() {

        matricula = arguments?.getString("matricula").toString()
        marca = arguments?.getString("marca").toString()
        modelo = arguments?.getString("modelo").toString()

        binding.conceptsRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = conceptHistoryAdapter
        }

    }

    private fun observeEvents() {
        activity?.let {
            viewModel.allConcepts.observe(it, Observer { list ->
                list?.let { listado ->
                    // updates the list.
                    val listConcept = arrayListOf<String>()
                    listado.sortedBy { it.concepto }
                        .filter { it.matricula == matricula && it.visible }
                        .forEach { c ->
                            listConcept.add(c.concepto)
                        }
                    val adapter =
                        context?.let { it1 -> ArrayAdapter(it1, R.layout.list_item, listConcept) }
                    binding.menu.setAdapter(adapter)

                    binding.menu.setOnItemClickListener { adapterView, view, i, l ->
                        conceptHistoryAdapter.updateList(listado.sortedByDescending { it.fecha }.filter {
                            it.matricula == matricula && it.concepto == listConcept.get(
                                i
                            )
                        })
                    }
                }
            })
        }

    }

    override fun onConceptDeleteIconClick(concepto: Concepto) {
        viewModel.deleteConcept(concepto)
        (activity as MainActivity).toast("${concepto.concepto} Eliminado")
    }

    override fun onConceptClick(concepto: Concepto) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        var taller = concepto.taller
        if (taller == "null" || taller?.length == 0) taller = "-"

        var precio = concepto.precio.toString()
        if (precio == "null" || precio?.length == 0) precio = "-" else precio+="€"

        var detalles = concepto.detalles
        if (detalles == "null" || detalles?.length == 0) detalles = "-"

        dialogBuilder.setMessage(
            "Fecha: ${concepto.fecha}\n" +
                    "Taller: $taller\n" +
                    "Precio: $precio\n" +
                    "Detalles: $detalles"
        )
        // if the dialog is cancelable
        /*.setCancelable(false)
        .setPositiveButton("Ok", DialogInterface.OnClickListener {
                dialog, id ->
            dialog.dismiss()

        })*/

        val alert = dialogBuilder.create()
        alert.setTitle("${concepto.concepto}")
        alert.show()
    }

    fun changeFragmentActionBar() {
        (activity as MainActivity).changeActionBar("$marca $modelo", "$matricula")
    }

}
