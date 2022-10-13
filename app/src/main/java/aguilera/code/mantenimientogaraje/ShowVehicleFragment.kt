package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import aguilera.code.mantenimientogaraje.data.ui.*
import aguilera.code.mantenimientogaraje.databinding.FragmentShowVehicleBinding
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale.filter

class ShowVehicleFragment : Fragment(), ConceptClickInterface, ConceptDeleteIconClickInterface {

    private lateinit var viewModel: GarageViewModel
    private lateinit var conceptAdapter: ConceptAdapter

    private var binding: FragmentShowVehicleBinding? = null

    var matricula = ""
    var marca = ""
    var modelo = ""

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

        //(activity as AppCompatActivity?)!!.supportActionBar!!.show()

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        conceptAdapter = ConceptAdapter(this, this)

        initView()
        observeEvents()
        changeFragmentActionBar()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.show_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // navigate to settings screen
                true
            }
            R.id.action_edit -> {
                // edit vehicle
                true
            }
            R.id.action_info -> {
                // show info screen
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {

        matricula = arguments?.getString("matricula").toString()
        marca = arguments?.getString("marca").toString()
        modelo = arguments?.getString("modelo").toString()

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

        binding?.btnHistory?.setOnClickListener {
            activity?.let {
                val fragment = HistoryFragment()
                fragment.arguments = Bundle().apply {
                    putString("matricula", matricula)
                    putString("marca", marca)
                    putString("modelo", modelo)
                }
                it.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment)
                    .addToBackStack("HistoryFragment").commit()
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
                    // updates the list.
                    //conceptAdapter.updateList(it)
                    //Filtrado segun matricula y muestra solo ultimo registro del concepto
                    var listF = listado.sortedByDescending {
                        LocalDate.parse(
                            it.fecha.toString(),
                            DateTimeFormatter.ofPattern("d/M/y")
                        )
                    }
                        .filter { it.matricula == matricula && it.visible }
                    conceptAdapter.updateList(listF)

                    if (listF.size > 0) binding?.btnHistory?.visibility =
                        View.VISIBLE else binding?.btnHistory?.visibility = View.INVISIBLE

                    //Muestra el boton remember si existe algun registro que recordar
                    var n=0
                    listF.forEach { concepto ->
                        if(concepto.recordar) n++
                    }
                    if (n>0) binding?.btnRemember?.visibility =
                        View.VISIBLE else binding?.btnRemember?.visibility = View.INVISIBLE
                    //---------------------------------------------------------------------
                }
            })
        }

    }

    override fun onConceptDeleteIconClick(concepto: Concepto) {
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

    override fun onConceptClick(concepto: Concepto) {
        // opening a new intent and passing a data to it.
        activity?.let {
            val fragment = NewConceptVehicleFragment()
            fragment.arguments = Bundle().apply {
                putString("type", "Edit")
                putString("matricula", matricula)
                putSerializable("concept", concepto as Concepto)
            }
            it.supportFragmentManager.beginTransaction().replace(R.id.mainContainer, fragment)
                .addToBackStack("NewConceptVehicleFragment").commit()
        }
    }

    fun changeFragmentActionBar() {
        (activity as MainActivity).changeActionBar("$marca $modelo", "$matricula")
    }
}
