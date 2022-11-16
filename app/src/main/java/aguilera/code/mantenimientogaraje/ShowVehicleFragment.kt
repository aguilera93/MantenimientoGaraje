package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.db.entity.Vehiculo
import aguilera.code.mantenimientogaraje.data.ui.*
import aguilera.code.mantenimientogaraje.databinding.FragmentShowVehicleBinding
import android.app.Dialog
import android.content.ClipData.newIntent
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Locale.filter

class ShowVehicleFragment : Fragment(), ConceptClickInterface {

    private lateinit var viewModel: GarageViewModel
    private lateinit var conceptAdapter: ConceptAdapter

    private var binding: FragmentShowVehicleBinding? = null

    lateinit var vehiculo: Vehiculo
    var matricula = ""
    var marca = ""
    var modelo = ""
    var listN = arrayListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowVehicleBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        conceptAdapter = ConceptAdapter(this)

        initView()
        observeEvents()
        changeFragmentActionBar()

    }

    private fun initView() {

        matricula = arguments?.getString("matricula").toString()
        marca = arguments?.getString("marca").toString()
        modelo = arguments?.getString("modelo").toString()
        getVehiculo()
        getConceptsMoney()

        binding?.btnAdd?.setOnClickListener {
            activity?.let {
                val fragment = NewConceptVehicleFragment()
                fragment.arguments = Bundle().apply {
                    putString("matricula", matricula)
                }
                it.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment)
                    .addToBackStack("NewConceptVehicleFragment").commit()
            }
        }

        binding?.btnMoney?.setOnClickListener {
            activity?.let {
                val fragment = MoneyViewpagerFragment()
                fragment.arguments = Bundle().apply {
                    putString("matricula", matricula)
                    putString("marca", marca)
                    putString("modelo", modelo)
                    putSerializable("listN", listN)
                }
                it.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment)
                    .addToBackStack("MoneyViewpagerFragment").commit()
            }
        }

        binding?.btnRemember?.setOnClickListener {
            activity?.let {
                val fragment = RememberFragment()
                fragment.arguments = Bundle().apply {
                    putString("matricula", matricula)
                    putString("marca", marca)
                    putString("modelo", modelo)
                }
                it.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment)
                    .addToBackStack("RememberFragment").commit()
            }
        }

        binding?.conceptsRV?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = conceptAdapter
        }
    }

    private fun observeEvents() {
        activity?.let {
            viewModel.allConcepts.observe(it, Observer { list ->
                list?.let { listado ->
                    //Filtrado segun matricula y muestra solo ultimo registro del concepto
                    var listF = listado.sortedByDescending {
                        LocalDate.parse(
                            it.fecha.toString(),
                            DateTimeFormatter.ofPattern("d/M/y")
                        )
                    }
                        .filter { it.matricula == matricula && it.visible }
                    conceptAdapter.updateList(listF)

                    //Muestra el boton money si existe algun registro ------
                    var p = 0
                    listF.forEach { concepto ->
                        if (concepto.precio?.toString() != "null") p++
                    }
                    if (p > 0) binding?.btnMoney?.visibility =
                        View.VISIBLE else binding?.btnMoney?.visibility = View.INVISIBLE
                    //---------------------------------------------------------------------

                    //Muestra el boton remember si existe algun registro que recordar------
                    var n = 0
                    listF.forEach { concepto ->
                        if (concepto.recordar) n++
                    }
                    if (n > 0) binding?.btnRemember?.visibility =
                        View.VISIBLE else binding?.btnRemember?.visibility = View.INVISIBLE
                    //---------------------------------------------------------------------
                }
            })
        }

    }

    override fun onConceptClick(concepto: Concepto) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.menu_alert)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        var taller = concepto.taller
        if (taller == "null" || taller?.length == 0) taller = "-"

        var kms = concepto.kms.toString()
        if (kms.toString() == "null" || kms?.toString()?.length == 0) kms = "-"

        var precio = concepto.precio.toString()
        if (precio == "null" || precio?.length == 0) precio = "-" else precio += "€"

        var detalles = concepto.detalles
        if (detalles == "null" || detalles?.length == 0) detalles = "-"
        val txtDetails = dialog.findViewById(R.id.txtDetails) as TextView
        txtDetails.setText(
            Html.fromHtml(
                "<u><b>${concepto.concepto}</b></u><br />" +
                        "<b>${getString(R.string.details)}:</b> $detalles<br />" +
                        "<b>${getString(R.string.date)}:</b> ${concepto.fecha}<br />" +
                        "<b>${getString(R.string.kms)}:</b> $kms<br />" +
                        "<b>${getString(R.string.price)}:</b> $precio<br />" +
                        "<b>${getString(R.string.taller)}:</b> $taller"

            )
        )
        val btnHistory = dialog.findViewById<FloatingActionButton>(R.id.btn_history)
        val btnEdit = dialog.findViewById<FloatingActionButton>(R.id.btn_edit)
        val btnDelete = dialog.findViewById<FloatingActionButton>(R.id.btn_delete)
        btnHistory.setOnClickListener {
            newIntent(concepto, "h")
            dialog.dismiss()
        }
        btnEdit.setOnClickListener {
            newIntent(concepto, "e")
            dialog.dismiss()
        }
        btnDelete.setOnClickListener {
            newIntent(concepto, "d")
            dialog.dismiss()
        }
        dialog.show()
    }

    fun getVehiculo() {
        CoroutineScope(Dispatchers.IO).launch {
            vehiculo = viewModel.getVehicleByMatricula(matricula)
            marca = vehiculo.marca.toString()
            modelo = vehiculo.modelo.toString()
        }
    }

    fun getConceptsMoney() {
        CoroutineScope(Dispatchers.IO).launch {

            val formatter = DateTimeFormatter.ofPattern("d/M/y")
            lateinit var date: TemporalAccessor
            var nMonth = 0

            var conceptos = viewModel.getConceptsByMatricula(matricula)

            conceptos.forEach {
                date = formatter.parse(it.fecha)
                nMonth = DateTimeFormatter.ofPattern("M").format(date).toString().toInt()
                listN.add(nMonth)
            }
        }
    }

    fun delete(concepto: Concepto) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        @Suppress("DEPRECATION")
        dialogBuilder.setMessage(
            Html.fromHtml("${getString(R.string.que_delete)} ''<b>${concepto.concepto}</b>''?")
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

    fun delete(vehiculo: Vehiculo) {
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

    fun newIntent(concepto: Concepto, option: String) {
        // opening a new intent and passing a data to it.
        when (option) {
            "h" -> activity?.let {
                val fragment = HistoryFragment()
                fragment.arguments = Bundle().apply {
                    putString("matricula", matricula)
                    putString("marca", marca)
                    putString("modelo", modelo)
                    putString("concepto", concepto.concepto)
                }
                it.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment)
                    .addToBackStack("HistoryFragment").commit()
            }
            "e" -> activity?.let {
                val fragment = NewConceptVehicleFragment()
                fragment.arguments = Bundle().apply {
                    putString("type", "Edit")
                    putString("matricula", matricula)
                    putSerializable("concept", concepto)
                }
                it.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment)
                    .addToBackStack("NewConceptVehicleFragment").commit()
            }
            "d" -> delete(concepto)
        }
    }

    fun changeFragmentActionBar() {
        (activity as MainActivity).changeActionBar("$marca $modelo", "Conceptos: $matricula")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.show_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                activity?.let {
                    val fragment = NewVehicleFragment()
                    fragment.arguments = Bundle().apply {
                        putString("type", "Edit")
                        putString("matricula", vehiculo.matricula)
                        putSerializable("vehiculo", vehiculo)
                    }
                    it.supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, fragment)
                        .addToBackStack("NewVehicleFragment").commit()

                    getVehiculo()
                    changeFragmentActionBar()
                }
                true
            }
            R.id.action_delete -> {
                delete(vehiculo)
                true
            }
            R.id.action_info -> {
                // show info screen
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}