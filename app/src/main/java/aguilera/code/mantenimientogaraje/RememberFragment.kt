package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.*
import aguilera.code.mantenimientogaraje.databinding.FragmentRememberBinding
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RememberFragment : Fragment(), ConceptRememberClickInterface,
    ConceptRememberDeleteIconClickInterface {

    private lateinit var viewModel: GarageViewModel
    private lateinit var conceptRememberAdapter: ConceptRememberAdapter

    private var binding: FragmentRememberBinding? = null

    var matricula = ""
    var marca = ""
    var modelo = ""
    lateinit var listC: List<Concepto>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRememberBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        conceptRememberAdapter = ConceptRememberAdapter(this, this)

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
            adapter = conceptRememberAdapter
        }

    }

    private fun observeEvents() {
        activity?.let {
            viewModel.allConcepts.observe(it, Observer { list ->
                list?.let { listado ->
                    // updates the list.
                    //conceptAdapter.updateList(it)
                    //Filtrado segun matricula y muestra solo ultimo registro del concepto si tiene que ser recordado
                    var listF = listado.sortedByDescending {
                        LocalDate.parse(
                            it.fecha.toString(),
                            DateTimeFormatter.ofPattern("d/M/y")
                        )
                    }
                        .filter { it.matricula == matricula && it.visible && it.recordar}
                    conceptRememberAdapter.updateList(listF)
                    listC=listF
                }
            })
        }

    }

    private fun updateList(list: List<Concepto>, con: String) {
        var listF = list.sortedByDescending {
            LocalDate.parse(
                it.fecha.toString(),
                DateTimeFormatter.ofPattern("d/M/y")
            )
        }
            .filter {
                it.matricula == matricula && it.concepto == con
            }
        conceptRememberAdapter.updateList(listF)
    }

    override fun onConceptDeleteIconClick(concepto: Concepto) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        @Suppress("DEPRECATION")
        dialogBuilder.setMessage(
            Html.fromHtml("${getString(R.string.que_remember)} ''<b>${concepto.rFecha}</b>''?")
        )
            // if the dialog is cancelable
            .setCancelable(true)
            .setPositiveButton(
                "${getString(R.string.accept)}",
                DialogInterface.OnClickListener { dialog, id ->
                    viewModel.clearRememberConceptByUpdate(concepto)
                    (activity as MainActivity).toast("${concepto.concepto} ${getString(R.string.deleted)}")
                    dialog.dismiss()
                    //Actualizacion del listado tras el borrado de un concepto
                    listC = listC.filter { it.id_concept != concepto.id_concept }
                    updateList(listC, concepto.concepto)
                    //----------------------------------------------------------------------
                })
        dialogBuilder.setNegativeButton("${getString(R.string.cancel)}",
            DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("${concepto.concepto}:")
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
        (activity as MainActivity).changeActionBar("$marca $modelo", "Recordatorios: $matricula")
    }

}
