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

    val format = SimpleDateFormat("DD/MM/YYYY")

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

        matricula = arguments?.getString("matricula")

        val type = arguments?.getString("type")

        if (type == "Edit") {
            edit = true

            oldConcept = arguments?.getSerializable("concept") as Concepto

            conceptId = oldConcept.id_concept
        }

        setValues(type)

        binding.btnFecha.setOnClickListener {
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
            if (binding.btnFecha.text != "FECHA") {
                saveValues(type)
            } else {
                Toast.makeText(requireActivity(), "Debe elegir una fecha valida", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    fun rememberCheck(checked: Boolean): Int {
        if (checked) {
            return View.VISIBLE
        }
        return View.INVISIBLE
    }

    fun setValues(type: String?) {
        if (edit) {
            // setting data to edit text.
            binding.etConcepto.setText(oldConcept.concepto)
            binding.etConcepto.keyListener = null
            conceptId = arguments?.getString("id").toString().toIntOrNull()
            binding.btnFecha.setText(oldConcept.fecha)
            Log.i("miapp", "${oldConcept.precio}")
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

    fun saveValues(type: String?) {
        val concepto = binding.etConcepto.text.toString()
        val fecha = binding.btnFecha.text.toString()
        val precio = binding.etPrecio.text.toString().toFloatOrNull()
        val kms = binding.etKMSC.text.toString().toIntOrNull()
        val taller = binding.etTaller.text.toString()
        val detalles = binding.etDetallesC.text.toString()
        val recordar = binding.cbRecordar.isChecked
        var rfecha = if (recordar) getRFech() else ""
        var visible = true

        Log.i("miapp", "C: $concepto")

        val concept = matricula?.let { it1 ->
            Concepto(
                conceptId,
                it1,
                concepto,
                fecha,
                kms,
                precio,
                taller,
                detalles,
                recordar,
                rfecha,
                visible
            )
        }

        // checking the type and then saving or updating the data.
        if (edit) {
            if (concepto != "") {
                binding.etConcepto.error = null
                if (concept != null) {
                    if (format.parse(oldConcept.fecha) < format.parse(fecha)) {
                        concept.id_concept = null
                        viewModal.insertConcept(concept)
                        oldConcept.visible = false
                        viewModal.updateConcept(oldConcept)
                    } else {
                        concept.id_concept = oldConcept.id_concept
                        viewModal.updateConcept(concept)
                    }
                    Toast.makeText(requireActivity(), "Concepto Modificado", Toast.LENGTH_LONG)
                        .show()
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        } else {
            if (concepto != "") {
                binding.etConceptoLay.error = null
                if (concept != null) {
                    viewModal.insertConcept(concept)
                }
                Toast.makeText(requireActivity(), "Concepto Añadido", Toast.LENGTH_LONG).show()
                activity?.supportFragmentManager?.popBackStack()
            } else {
                binding.etConceptoLay.error = "Debe introducir un concepto valido"
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
        binding.btnFecha.setText(fechConcept)
    }
}