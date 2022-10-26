package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.*
import aguilera.code.mantenimientogaraje.databinding.FragmentRememberBinding
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

private lateinit var viewModel: GarageViewModel

class RememberFragment : Fragment(), ConceptRememberClickInterface,
    ConceptRememberMenuIconClickInterface {

    private lateinit var conceptRememberAdapter: ConceptRememberAdapter

    private var binding: FragmentRememberBinding? = null

    var matricula = ""
    var marca = ""
    var modelo = ""

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
        setHasOptionsMenu(true)

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
        updateList()
    }

    private fun updateList() {
        activity?.let {
            viewModel.allConcepts.observe(it, Observer { list ->
                list?.let { listado ->
                    // updates the list.
                    val listC = arrayListOf<Concepto>()
                    listado.sortedByDescending {
                        LocalDate.parse(
                            it.fecha.toString(),
                            DateTimeFormatter.ofPattern("d/M/y")
                        )
                    }
                        .filter { it.matricula == matricula && it.visible && it.recordar }
                        .forEach { c ->
                            listC.add(c)
                        }
                    conceptRememberAdapter.updateList(listC)
                    if (listC.isEmpty()) activity?.supportFragmentManager?.popBackStack()
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
            "${concepto.concepto}\n" +
                    "${getString(R.string.date)}: ${concepto.fecha}\n" +
                    "${getString(R.string.kms)}: $kms\n" +
                    "${getString(R.string.taller)}: $taller\n" +
                    "${getString(R.string.price)}: $precio\n" +
                    "${getString(R.string.details)}: $detalles"
        )
        val btnHistory = dialog.findViewById<FloatingActionButton>(R.id.btn_history)
        val btnEdit = dialog.findViewById<FloatingActionButton>(R.id.btn_edit)
        val btnDelete = dialog.findViewById<FloatingActionButton>(R.id.btn_delete)
        /*btnHistory.setOnClickListener {
            newIntent(concepto, "h")
            dialog.dismiss()
        }*/
        btnHistory.visibility = View.GONE
        (dialog.findViewById(R.id.txt_history) as TextView).visibility = View.GONE
        btnEdit.setOnClickListener {
            //newIntent(concepto, "e")
            val newFragment: DialogFragment = ChangeDateFragment(concepto)
            fragmentManager?.let { it1 -> newFragment.show(it1, "DatePicker") }
            dialog.dismiss()
        }
        btnDelete.setOnClickListener {
            newIntent(concepto, "d")
            dialog.dismiss()
        }
        dialog.show()
    }

    fun delete(concepto: Concepto) {
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
                    updateList()
                })
        dialogBuilder.setNegativeButton("${getString(R.string.cancel)}",
            DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("${concepto.concepto}:")
        alert.show()
    }

    override fun onConceptMenuIconClick(concepto: Concepto) {
        /*val dialogBuilder = AlertDialog.Builder(requireContext())
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
        val alert = dialogBuilder.create()
        alert.setTitle("${concepto.concepto}")
        alert.show()*/
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
        (activity as MainActivity).changeActionBar("$marca $modelo", "Recordatorios: $matricula")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // navigate to settings screen
                true
            }
            R.id.action_clear -> {
                // delete all vehicles
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

class ChangeDateFragment(concepto: Concepto) : DialogFragment(),
    DatePickerDialog.OnDateSetListener {
    var c = concepto
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar: Calendar = Calendar.getInstance()
        val yy: Int = calendar.get(Calendar.YEAR)
        val mm: Int = calendar.get(Calendar.MONTH)
        val dd: Int = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireActivity(), this, yy, mm, dd)
    }

    override fun onDateSet(view: DatePicker, yy: Int, mm: Int, dd: Int) {
        populateSetDate(yy, mm + 1, dd)
    }

    fun populateSetDate(year: Int, month: Int, day: Int) {
        var fechConcept = "$day/$month/$year"
        c.id_concept?.let { viewModel.updateFechConcept(it, fechConcept) }
    }
}