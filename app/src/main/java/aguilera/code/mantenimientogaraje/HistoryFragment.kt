package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.*
import aguilera.code.mantenimientogaraje.databinding.FragmentHistoryBinding
import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Html
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoryFragment : Fragment(), ConceptHistoryClickInterface,
    ConceptHistoryDeleteIconClickInterface {

    private lateinit var viewModel: GarageViewModel
    private lateinit var conceptHistoryAdapter: ConceptHistoryAdapter

    private var binding: FragmentHistoryBinding? = null

    var matricula = ""
    var marca = ""
    var modelo = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding!!.root
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

        binding?.conceptsRV?.apply {
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
                    binding?.menu?.setAdapter(adapter)

                    //Solucion al problema de padding para AutoCompleteTextView
                    binding?.menu?.setDropDownBackgroundResource(R.color.app)

                    binding?.menu?.setOnItemClickListener { adapterView, view, i, l ->
                        conceptHistoryAdapter.updateList(listado.sortedByDescending {
                            LocalDate.parse(
                                it.fecha.toString(),
                                DateTimeFormatter.ofPattern("d/M/y")
                            )
                        }
                            .filter {
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
        val dialogBuilder = AlertDialog.Builder(requireContext())
        @Suppress("DEPRECATION")
        dialogBuilder.setMessage(
            Html.fromHtml("${getString(R.string.que_delete)} ''<b>${concepto.concepto} - ${concepto.detalles}</b>''?")
        )
            // if the dialog is cancelable
            .setCancelable(true)
            .setPositiveButton(
                "${getString(R.string.accept)}",
                DialogInterface.OnClickListener { dialog, id ->
                    viewModel.deleteConcept(concepto)
                    viewModel.showPreviusConceptByUpdate(concepto)
                    (activity as MainActivity).toast("${concepto.concepto} ${getString(R.string.deleted)}")
                    dialog.dismiss()
                })
        dialogBuilder.setNegativeButton("${getString(R.string.cancel)}",
            DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("${getString(R.string.delete)}:")
        alert.show()
    }

    override fun onConceptClick(concepto: Concepto) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        var taller = concepto.taller
        if (taller == "null" || taller?.length == 0) taller = "-"

        var precio = concepto.precio.toString()
        if (precio == "null" || precio?.length == 0) precio = "-" else precio += "€"

        var detalles = concepto.detalles
        if (detalles == "null" || detalles?.length == 0) detalles = "-"

        dialogBuilder.setMessage(
            "${getString(R.string.date)}: ${concepto.fecha}\n" +
                    "${getString(R.string.taller)}: $taller\n" +
                    "${getString(R.string.price)}: $precio\n" +
                    "${getString(R.string.details)}: $detalles"
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
