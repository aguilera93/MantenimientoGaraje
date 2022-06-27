package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.databinding.FragmentNewVehicleBinding
import aguilera.code.mantenimientogaraje.databinding.FragmentShowVehicleBinding
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

class ShowVehicleFragment : Fragment() {
    private var _binding: FragmentShowVehicleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val matricula = arguments?.getString("matricula")

        //(activity as AppCompatActivity).supportActionBar?.setSubtitle("$matricula")

        binding.btnNewC.setOnClickListener {
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, NewConceptVehicleFragment())
                    .addToBackStack("NewConceptVehicleFragment")
                    .commit()
            }
        }
    }
}
