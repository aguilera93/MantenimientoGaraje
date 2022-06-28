package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportFragmentManager.beginTransaction().replace(R.id.mainContainer, MainFragment())
            .commit()
    }

    fun changeActionBar(title: String, subtitle: String){
        supportActionBar?.setTitle("$title")
        supportActionBar?.setSubtitle("$subtitle")
    }

}

