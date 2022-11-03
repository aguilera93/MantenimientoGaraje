package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.*
import aguilera.code.mantenimientogaraje.databinding.FragmentHistoryBinding
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData.newIntent
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    var concept = ""

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
        setHasOptionsMenu(true)

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
        concept = arguments?.getString("concepto").toString()

        binding?.conceptsRV?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = conceptHistoryAdapter
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
                        .filter { it.matricula == matricula && it.concepto == concept }
                        .forEach { c ->
                            listC.add(c)
                        }
                    conceptHistoryAdapter.updateList(listC)
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
        btnHistory.visibility = View.GONE
        (dialog.findViewById(R.id.txt_history) as TextView).visibility = View.GONE
        btnEdit.visibility = View.GONE
        (dialog.findViewById(R.id.txt_edit) as TextView).visibility = View.GONE
        btnDelete.visibility = View.GONE
        (dialog.findViewById(R.id.txt_delete) as TextView).visibility = View.GONE
        dialog.show()
    }

    fun delete(concepto: Concepto) {
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
                    updateList()
                    //----------------------------------------------------------------------
                })
        dialogBuilder.setNegativeButton("${getString(R.string.cancel)}",
            DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("${getString(R.string.delete)}:")
        alert.show()
    }

    override fun onConceptDeleteIconClick(concepto: Concepto) {
        delete(concepto)
    }

    fun changeFragmentActionBar() {
        (activity as MainActivity).changeActionBar("$concept", "Historial: $matricula")
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
