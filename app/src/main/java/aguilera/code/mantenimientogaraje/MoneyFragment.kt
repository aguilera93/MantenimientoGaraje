package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.GarageViewModel
import aguilera.code.mantenimientogaraje.data.ui.MoneyAdapter
import aguilera.code.mantenimientogaraje.data.ui.MoneyClickInterface
import aguilera.code.mantenimientogaraje.databinding.FragmentMoneyBinding
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MoneyFragment : Fragment(), MoneyClickInterface {

    private lateinit var viewModel: GarageViewModel
    private lateinit var moneyAdapter: MoneyAdapter

    private var binding: FragmentMoneyBinding? = null

    private var month: String? = null
    private var monthN: String? = null

    var matricula = ""
    var marca = ""
    var modelo = ""
    var money = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoneyBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            month = it.getString("month")
            monthN = it.getString("monthN")
            matricula = it.getString("matricula").toString()
            marca = it.getString("marca").toString()
            modelo = it.getString("modelo").toString()
        }

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        moneyAdapter = MoneyAdapter(this)

        initView()
        observeEvents()
        changeFragmentActionBar()
    }

    private fun initView() {
        binding?.moneyRV?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = moneyAdapter
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
                            it.fecha,
                            DateTimeFormatter.ofPattern("d/M/y")
                        )
                    }
                        .filter { it.matricula == matricula }
                        .forEach { c ->
                            if (checkMonthFecha(c.fecha)) {
                                listC.add(c)
                                if (!c.precio.toString()
                                        .isNullOrBlank() && c.precio.toString() != "null"
                                ) {
                                    money += c.precio!!
                                }
                            }
                        }
                    moneyAdapter.updateList(listC)
                    if (listC.isEmpty()) {
                        //binding?.fMoney?.visibility = View.GONE
                    }
                    binding?.total?.text = "TOTAL: ${money.toString()}€"
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

    fun changeFragmentActionBar() {
        (activity as MainActivity).changeActionBar("$marca $modelo", "Gastos: $matricula")
    }

    fun checkMonthFecha(fecha: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("d/M/y")
        val date = formatter.parse(fecha)
        val nMonth = DateTimeFormatter.ofPattern("M").format(date).toString().toInt()
        val nYear = DateTimeFormatter.ofPattern("YYYY").format(date).toString().toInt()
        val year = Calendar.getInstance().get(Calendar.YEAR)
        if (monthN?.toInt() == 0 && year == nYear) {
            binding?.mes?.text =
                Calendar.getInstance().get(Calendar.YEAR).toString()
            return true
        }
        if (nMonth == monthN?.toInt() && year == nYear) {
            binding?.mes?.text =
                DateFormatSymbols().getMonths()[monthN?.toInt()!! - 1].replaceFirstChar(Char::titlecase)
            return true
        }
        return false
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

    companion object {

        @JvmStatic
        fun newInstance(arg1: String, arg2: String, arg3: String, arg4: String, arg5: String) =
            MoneyFragment().apply {
                arguments = Bundle().apply {
                    putString("month", arg1)
                    putString("monthN", arg2)
                    putString("matricula", arg3)
                    putString("marca", arg4)
                    putString("modelo", arg5)
                }
            }
    }
}
