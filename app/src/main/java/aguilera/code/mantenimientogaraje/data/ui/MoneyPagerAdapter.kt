package aguilera.code.mantenimientogaraje.data.ui

import aguilera.code.mantenimientogaraje.MoneyFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MoneyPagerAdapter(
    fa: Fragment,
    matricula: String,
    marca: String,
    modelo: String,
    listN: List<Int>
) :
    FragmentStateAdapter(fa) {

    var matricula = matricula
    var marca = marca
    var modelo = modelo
    var listN = listN.distinct().sorted()

    override fun getItemCount(): Int = listN.size + 1

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return MoneyFragment.newInstance(
                "Año",
                "0",
                matricula,
                marca,
                modelo
            )
        }
        return MoneyFragment.newInstance(
            "Año",
            listN[position - 1].toString(),
            matricula,
            marca,
            modelo
        )
    }
}