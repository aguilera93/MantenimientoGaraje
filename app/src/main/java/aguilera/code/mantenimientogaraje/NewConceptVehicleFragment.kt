package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.GarageViewModel
import aguilera.code.mantenimientogaraje.databinding.FragmentNewConceptVehicleBinding
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

private var fechConcept = ""
private var _binding: FragmentNewConceptVehicleBinding? = null
private val binding get() = _binding!!

class NewConceptVehicleFragment : Fragment() {

    lateinit var viewModal: GarageViewModel
    var conceptId: Int? = null

    var matricula: String? = ""
    var edit: Boolean = false

    lateinit var oldConcept: Concepto

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewConceptVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        getValues()

        setValues()

        binding.etFecha.setOnClickListener {
            val newFragment: DialogFragment = SelectDateFragment()
            fragmentManager?.let { it1 -> newFragment.show(it1, "DatePicker") }
        }

        changeFragmentActionBar()

        binding.dpRDate.visibility = rememberCheck(binding.cbRecordar.isChecked)

        binding.cbRecordar.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.dpRDate.visibility = rememberCheck(isChecked)
        }

        // adding click listener to our save button.
        binding.btnSave.setOnClickListener {
            if (binding.etFecha.text.toString() != "FECHA") {
                saveValues()
            } else {
                (activity as MainActivity).toast("Debe elegir una fecha valida")
            }
        }

    }

    fun getValues() {
        matricula = arguments?.getString("matricula")

        if (arguments?.getString("type") == "Edit") {
            edit = true
            oldConcept = arguments?.getSerializable("concept") as Concepto
            conceptId = oldConcept.id_concept
        }
    }

    fun rememberCheck(checked: Boolean): Int {
        if (checked) {
            return View.VISIBLE
        }
        return View.INVISIBLE
    }

    fun setValues() {
        if (edit) {
            // setting data.
            binding.etConcepto.setText(oldConcept.concepto)
            // Para no poder editar el nombre del concepto
            binding.etConcepto.keyListener = null
            binding.etFecha.setText(oldConcept.fecha)
            binding.etPrecio.setText(if (oldConcept.precio == null) "" else oldConcept.precio.toString())
            binding.etKMSC.setText(if (oldConcept.kms == null) "" else oldConcept.kms.toString())
            binding.etTaller.setText(oldConcept.taller)
            binding.etDetallesC.setText(oldConcept.detalles)
            binding.cbRecordar.isChecked = oldConcept.recordar
            if (oldConcept.rFecha?.isEmpty() == false) oldConcept.rFecha?.let { setRFech(it) }
            binding.btnSave.setText("Update")
        } else {
            binding.btnSave.setText("Save")
        }
    }

    fun saveValues() {
        val concept = matricula?.let { it1 ->
            Concepto(
                conceptId,
                it1,
                binding.etConcepto.text.toString(),
                binding.etFecha.text.toString(),
                binding.etKMSC.text.toString().toIntOrNull(),
                binding.etPrecio.text.toString().toFloatOrNull(),
                binding.etTaller.text.toString(),
                binding.etDetallesC.text.toString(),
                binding.cbRecordar.isChecked,
                if (binding.cbRecordar.isChecked) getRFech() else "",
                true
            )
        }

        // checking the type and then saving or updating the data.
        if (edit) {
            if (concept?.concepto != "") {
                binding.etConcepto.error = null
                if (concept != null) {
                    if (compareFech(oldConcept.fecha, concept.fecha)) {
                        concept.id_concept = null
                        viewModal.insertConcept(concept)
                        oldConcept.visible = false
                        viewModal.updateConcept(oldConcept)
                    } else {
                        concept.fecha = oldConcept.fecha
                        viewModal.updateConcept(concept)
                    }
                    (activity as MainActivity).toast("${concept.concepto} Modificado")
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        } else {
            if (concept?.concepto != "") {
                binding.etConceptoLay.error = null
                if (concept != null) {
                    viewModal.insertConcept(concept)
                }
                (activity as MainActivity).toast("${concept?.concepto} Añadido")
                activity?.supportFragmentManager?.popBackStack()
            } else {
                binding.etConceptoLay.error = "Debe introducir un nombre de concepto valido"
            }
        }
    }

    fun changeFragmentActionBar() {
        if (edit) {
            (activity as MainActivity).changeActionBar(
                "Modificar ${binding.etConcepto.text.toString()}",
                "$matricula"
            )
        } else {
            (activity as MainActivity).changeActionBar("Añadir Concepto a", "$matricula")
        }
    }

    fun getRFech(): String {

        //val sdf = SimpleDateFormat("DD/MM/YYYY")
        val day: Int = binding.dpRDate.getDayOfMonth()
        val month: Int = binding.dpRDate.getMonth()
        val year: Int = binding.dpRDate.getYear()

        return "$day/$month/$year"
    }

    fun setRFech(rFech: String) {
        var fecha = rFech
        val day: Int = fecha.substring(0, fecha.indexOf("/")).toInt()
        fecha = fecha.substring(fecha.indexOf("/") + 1)
        val month: Int = fecha.substring(0, fecha.indexOf("/")).toInt()
        fecha = fecha.substring(fecha.indexOf("/") + 1)
        val year: Int = fecha.toInt()
        binding.dpRDate.updateDate(year, month, day)
    }

    fun compareFech(oldFech: String, newFech: String): Boolean {
        /*var f1 = oldFech
        var f2 = newFech
        val d1: Int = f1.substring(0, f1.indexOf("/")).toInt()
        val d2: Int = f2.substring(0, f2.indexOf("/")).toInt()
        f1 = f1.substring(f1.indexOf("/") + 1)
        f2 = f2.substring(f2.indexOf("/") + 1)
        val m1: Int = f1.substring(0, f1.indexOf("/")).toInt()
        val m2: Int = f2.substring(0, f2.indexOf("/")).toInt()
        f1 = f1.substring(f1.indexOf("/") + 1)
        f2 = f2.substring(f2.indexOf("/") + 1)
        val y1: Int = f1.toInt()
        val y2: Int = f2.toInt()

        var sumF1 = y1 + m1 + d1
        var sumF2 = y2 + m2 + d2

        return (sumF1 < sumF2)*/

        var d1 = LocalDate.parse("$oldFech", DateTimeFormatter.ofPattern("d/M/y"))
        var d2 = LocalDate.parse("$newFech", DateTimeFormatter.ofPattern("d/M/y"))
        //Log.i("miapp","${d1.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))}")
        //Log.i("miapp","${d2.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))}")

        return d1.isBefore(d2)

    }

}

class SelectDateFragment : DialogFragment(),
    OnDateSetListener {
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
        fechConcept = "$day/$month/$year"
        binding.etFecha.setText(fechConcept)
    }
}