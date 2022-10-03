package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.GarageViewModel
import aguilera.code.mantenimientogaraje.databinding.FragmentNewConceptVehicleBinding
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.ContentValues
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract.Events
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


private var fechConcept = ""
private var binding: FragmentNewConceptVehicleBinding? = null

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
            val newFragment: DialogFragment = SelectDateFragment()
            fragmentManager?.let { it1 -> newFragment.show(it1, "DatePicker") }
        }

        changeFragmentActionBar()

        binding?.dpRDate?.visibility = rememberCheck(binding?.cbRecordar?.isChecked)

        binding?.cbRecordar?.setOnCheckedChangeListener { buttonView, isChecked ->
            binding?.dpRDate?.visibility = rememberCheck(isChecked)
        }

        // adding click listener to our save button.
        binding?.btnSave?.setOnClickListener {
            if (binding?.etFecha?.text.toString() != "FECHA") {
                saveValues()
            } else {
                (activity as MainActivity).toast(getString(R.string.err_fecha))
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

    fun rememberCheck(checked: Boolean?): Int {
        if (checked == true) {
            return View.VISIBLE
        }
        return View.INVISIBLE
    }

    fun setValues() {
        if (edit) {
            // setting data.
            binding?.etConcepto?.setText(oldConcept.concepto)
            //Esconde la vista que contiene el campo del concepto
            binding?.etConceptoLay?.visibility = View.GONE
            //Para no poder editar el nombre del concepto
            //binding?.etConcepto?.keyListener = null
            binding?.etFecha?.setText(oldConcept.fecha)
            binding?.etPrecio?.setText(if (oldConcept.precio == null) "" else oldConcept.precio.toString())
            binding?.etKMSC?.setText(if (oldConcept.kms == null) "" else oldConcept.kms.toString())
            binding?.etTaller?.setText(oldConcept.taller)
            binding?.etDetallesC?.setText(oldConcept.detalles)
            binding?.cbRecordar?.isChecked = oldConcept.recordar
            if (oldConcept.rFecha?.isEmpty() == false) oldConcept.rFecha?.let { setRFech(it) }
            binding?.btnSave?.setText(getString(R.string.btn_update))
        } else {
            binding?.btnSave?.setText(getString(R.string.btn_save))
        }
    }

    fun saveValues() {
        val check = binding?.cbRecordar?.isChecked
        val concept = matricula?.let { it1 ->
            Concepto(
                conceptId,
                it1,
                binding?.etConcepto?.text.toString(),
                binding?.etFecha?.text.toString(),
                binding?.etKMSC?.text.toString().toIntOrNull(),
                binding?.etPrecio?.text.toString().toFloatOrNull(),
                binding?.etTaller?.text.toString(),
                binding?.etDetallesC?.text.toString(),
                if (check != null) check else false,
                if (check != null && check) getRFech() else "",
                true
            )
        }

        // checking the type and then saving or updating the data.
        if (edit) {
            if (concept?.concepto != "") {
                binding?.etConcepto?.error = null
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
                    (activity as MainActivity).toast("${concept.concepto} ${R.string.update}")
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        } else {
            if (concept?.concepto != "") {
                binding?.etConceptoLay?.error = null
                if (concept != null) {
                    viewModal.insertConcept(concept)
                }
                (activity as MainActivity).toast("${concept?.concepto} ${R.string.save}")
                activity?.supportFragmentManager?.popBackStack()
            } else {
                binding?.etConceptoLay?.error = getString(R.string.err_concepto)
            }
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

    fun getRFech(): String {
        val day: Int? = binding?.dpRDate?.getDayOfMonth()
        val month: Int? = binding?.dpRDate?.getMonth()
        val year: Int? = binding?.dpRDate?.getYear()

        //setCalendarEvent("$day/$month/$year")

        return "$day/$month/$year"
    }

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

    fun setRFech(rFech: String) {
        var fecha = rFech
        val day: Int = fecha.substring(0, fecha.indexOf("/")).toInt()
        fecha = fecha.substring(fecha.indexOf("/") + 1)
        val month: Int = fecha.substring(0, fecha.indexOf("/")).toInt()
        fecha = fecha.substring(fecha.indexOf("/") + 1)
        val year: Int = fecha.toInt()
        binding?.dpRDate?.updateDate(year, month, day)
    }

    fun compareFech(oldFech: String, newFech: String): Boolean {
        var d1 = LocalDate.parse("$oldFech", DateTimeFormatter.ofPattern("d/M/y"))
        var d2 = LocalDate.parse("$newFech", DateTimeFormatter.ofPattern("d/M/y"))
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
        binding?.etFecha?.setText(fechConcept)
    }
}