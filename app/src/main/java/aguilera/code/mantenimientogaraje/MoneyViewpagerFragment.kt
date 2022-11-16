package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.ui.GarageViewModel
import aguilera.code.mantenimientogaraje.data.ui.MoneyPagerAdapter
import aguilera.code.mantenimientogaraje.databinding.FragmentMoneyViewpagerBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider


class MoneyViewpagerFragment : Fragment() {

    private lateinit var viewModel: GarageViewModel

    private var binding: FragmentMoneyViewpagerBinding? = null

    var matricula = ""
    var marca = ""
    var modelo = ""
    var listN :List<Int> = arrayListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoneyViewpagerBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(GarageViewModel::class.java)

        matricula = arguments?.getString("matricula").toString()
        marca = arguments?.getString("marca").toString()
        modelo = arguments?.getString("modelo").toString()
        listN = arguments?.getSerializable("listN") as List<Int>

        Log.e("miapp", "${listN.distinct().toString()}")

        binding?.viewPager2?.adapter = MoneyPagerAdapter(this, matricula, marca, modelo, listN)

    }
}
