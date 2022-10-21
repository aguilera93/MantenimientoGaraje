package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.GarageViewModel
import aguilera.code.mantenimientogaraje.databinding.FragmentNewConceptVehicleBinding
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract.Events
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

private var rFechCheck = ""
private var binding: FragmentNewConceptVehicleBinding? = null

class NewConceptVehicleFragment : Fragment() {

    lateinit var viewModal: GarageViewModel
    var conceptId: Int? = null
    var matricula: String = ""
    var edit: Boolean = false
    var validate: Boolean = false
    lateinit var oldConcept: Concepto

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewConceptVehicleBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        getValues()

        setValues()

        binding?.etFecha?.setOnClickListener {
            val newFragment: DialogFragment = SelectDateFragment(1)
            fragmentManager?.let { it1 -> newFragment.show(it1, "DatePicker") }
        }

        changeFragmentActionBar()

        binding?.cbRecordar?.setOnClickListener {
            rFechCheck = ""
            if (rememberCheck(binding?.cbRecordar?.isChecked)) {
                val newFragment: DialogFragment = SelectDateFragment(2)
                fragmentManager?.let { it1 -> newFragment.show(it1, "DatePicker") }
            }
            if (rFechCheck.isNullOrBlank()) {
                binding?.cbRecordar?.isChecked = false
                binding?.cbRecordar?.setText(getString(R.string.remember))
            }
        }

        // adding click listener to our save button.
        binding?.btnSave?.setOnClickListener {
            saveValues()
        }

    }

    fun getValues() {
        matricula = arguments?.getString("matricula").toString()

        if (arguments?.getString("type") == "Edit") {
            edit = true
            oldConcept = arguments?.getSerializable("concept") as Concepto
            conceptId = oldConcept.id_concept
        }
    }

    fun rememberCheck(checked: Boolean?): Boolean {
        if (checked == true) {
            //return View.VISIBLE
            return true
        }
        //return View.INVISIBLE
        return false
    }

    fun setValues() {
        if (edit) {
            // setting data.
            binding?.etConcepto?.setText(oldConcept.concepto)
            //Esconde la vista que contiene el campo del concepto
            binding?.etConceptoLay?.visibility = View.GONE
            //---------------------------------------------------
            binding?.etFecha?.setText(oldConcept.fecha)
            binding?.etPrecio?.setText(if (oldConcept.precio == null) "" else oldConcept.precio.toString())
            binding?.etKMSC?.setText(if (oldConcept.kms == null) "" else oldConcept.kms.toString())
            binding?.etTaller?.setText(oldConcept.taller)
            binding?.etDetallesC?.setText(oldConcept.detalles)
            binding?.cbRecordar?.isChecked = oldConcept.recordar
            //if (oldConcept.rFecha?.isEmpty() == false) oldConcept.rFecha?.let { setRFech(it) }
            if (oldConcept.rFecha?.isEmpty() == false) rFechCheck =
                oldConcept.rFecha?.toString().toString()
            binding?.cbRecordar?.setText("Recordar: ${oldConcept.rFecha}")
            binding?.btnSave?.setText(getString(R.string.btn_update))
        } else {
            binding?.btnSave?.setText(getString(R.string.btn_save))
        }
    }

    fun saveValues() {

        val concepto = binding?.etConcepto?.text.toString()
        val fecha = binding?.etFecha?.text.toString()
        val precio = binding?.etPrecio?.text.toString().toFloatOrNull()
        val kms = binding?.etKMSC?.text.toString().toIntOrNull()
        val taller = binding?.etTaller?.text.toString()
        val detalles = binding?.etDetallesC?.text.toString()
        val check = binding?.cbRecordar?.isChecked

        val concept = Concepto(
            conceptId,
            matricula,
            concepto,
            fecha,
            kms,
            precio,
            taller,
            detalles,
            if (check != null) check else false,
            //if (check != null && check) getRFech() else "",
            if (check != null && check) rFechCheck else "",
            true
        )

        validateFields()

        if (edit) {
            if (!concepto.isNullOrBlank() && validate) {
                if (compareFech(oldConcept.fecha, concept.fecha)) {
                    concept.id_concept = null
                    viewModal.insertConcept(concept)
                    oldConcept.visible = false
                    //Se limpian los campos para recordar
                    oldConcept.recordar = false
                    oldConcept.rFecha = ""
                    //-----------------------------------
                    viewModal.updateConcept(oldConcept)
                } else {
                    concept.fecha = oldConcept.fecha
                    viewModal.updateConcept(concept)
                }
                (activity as MainActivity).toast("${concept.concepto} ${getString(R.string.update)}")
                activity?.supportFragmentManager?.popBackStack()
            }
        } else {
            if (!concepto.isNullOrBlank() && validate) {
                viewModal.insertConcept(concept)
                (activity as MainActivity).toast("${concept?.concepto} ${getString(R.string.saved)}")
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    fun validateFields() {
        if (!binding?.etFecha?.text.isNullOrBlank()) {
            binding?.etFechaLay?.error = null
            validate = true
        } else {
            binding?.etFechaLay?.error = getString(R.string.err_campo_obligatorio)
            validate = false
        }

        if (!binding?.etConcepto?.text.isNullOrBlank()) {
            binding?.etConceptoLay?.error = null
            if (validate) {
                validate = true
            }
        } else {
            binding?.etConceptoLay?.error = getString(R.string.err_campo_obligatorio)
            validate = false
        }

        if (!binding?.etDetallesC?.text.isNullOrBlank()) {
            binding?.etDetallesCLay?.error = null
            if (validate) {
                validate = true
            }
        } else {
            binding?.etDetallesCLay?.error = getString(R.string.err_campo_obligatorio)
            validate = false
        }
    }

    fun changeFragmentActionBar() {
        if (edit) {
            (activity as MainActivity).changeActionBar(
                //"Modificar ${binding?.etConcepto?.text.toString()}",
                "${binding?.etConcepto?.text.toString()}",
                "$matricula"
            )
        } else {
            //(activity as MainActivity).changeActionBar("Añadir Concepto", "$matricula")
        }
    }

    /*fun getRFech(): String {
        val day: Int? = binding?.dpRDate?.getDayOfMonth()
        val month: Int? = binding?.dpRDate?.getMonth()
        val year: Int? = binding?.dpRDate?.getYear()

        //setCalendarEvent("$day/$month/$year")

        return "$day/$month/$year"
    }*/

    fun setCalendarEvent(dateStart: String) {
        val event = ContentValues()
        event.put(Events.CALENDAR_ID, 1)

        event.put(Events.TITLE, "PRUEBA")
        event.put(Events.DESCRIPTION, "DESCRIPCION")
        //event.put(Events.EVENT_LOCATION, location)

        event.put(Events.DTSTART, System.currentTimeMillis())
        event.put(Events.RRULE, "FREQ=YEARLY")
        event.put(Events.ALL_DAY, 0) // 0 for false, 1 for true
        event.put(Events.HAS_ALARM, 1) // 0 for false, 1 for true

        val timeZone = TimeZone.getDefault().id
        event.put(Events.EVENT_TIMEZONE, timeZone)

        val baseUri: Uri
        baseUri = if (Build.VERSION.SDK_INT >= 8) {
            Uri.parse("content://com.android.calendar/events")
        } else {
            Uri.parse("content://calendar/events")
        }

        requireContext().contentResolver.insert(baseUri, event)
    }

    /*fun setRFech(rFech: String) {
        var fecha = rFech
        val day: Int = fecha.substring(0, fecha.indexOf("/")).toInt()
        fecha = fecha.substring(fecha.indexOf("/") + 1)
        val month: Int = fecha.substring(0, fecha.indexOf("/")).toInt()
        fecha = fecha.substring(fecha.indexOf("/") + 1)
        val year: Int = fecha.toInt()
        binding?.dpRDate?.updateDate(year, month, day)
    }*/

    fun compareFech(oldFech: String, newFech: String): Boolean {
        var d1 = LocalDate.parse("$oldFech", DateTimeFormatter.ofPattern("d/M/y"))
        var d2 = LocalDate.parse("$newFech", DateTimeFormatter.ofPattern("d/M/y"))
        return d1.isBefore(d2)
    }
}

class SelectDateFragment(i: Int) : DialogFragment(),
    OnDateSetListener {
    var n = i
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
        if (n == 1) {
            binding?.etFecha?.setText(fechConcept)
        } else if (n == 2) {
            rFechCheck = fechConcept
            binding?.cbRecordar?.isChecked = true
            binding?.cbRecordar?.setText("${getString(R.string.remember)} $rFechCheck")
        }
    }
}