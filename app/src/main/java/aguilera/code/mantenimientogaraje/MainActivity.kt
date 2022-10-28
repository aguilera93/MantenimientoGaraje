package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.ui.GarageViewModel
import aguilera.code.mantenimientogaraje.databinding.ActivityMainBinding
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: GarageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        ).get(GarageViewModel::class.java)

        checkRememberConcepts()

        supportFragmentManager.beginTransaction().replace(R.id.mainContainer, MainFragment())
            .commit()
    }

    fun changeActionBar(title: String, subtitle: String) {
        supportActionBar?.setTitle("$title")
        supportActionBar?.setSubtitle("$subtitle")
    }

    fun toast(message: String) {
        Toast.makeText(this, "$message", Toast.LENGTH_SHORT).show()
    }

    fun checkRememberConcepts() {
        CoroutineScope(Dispatchers.IO).launch {
            val listRemember = viewModel.getRememberConcepts()
            val sdf = SimpleDateFormat("dd/M/yyyy")
            val f: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
            val today = sdf.format(Date())
            var notificationId = 0

            listRemember.forEach {
                val start: LocalDate = LocalDate.parse(today, f)
                val stop: LocalDate = LocalDate.parse(it.rFecha, f)
                val isBefore: Boolean = start.isBefore(stop)
                if (!isBefore) {
                    Intent(baseContext, NotificationPush::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }.also { intent ->
                        intent.putExtra("concept", it)
                        intent.putExtra("notificationId", notificationId++)
                        startService(intent)
                    }
                }
            }
        }
    }
}

