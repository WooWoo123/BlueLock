package su.ju.osvi.bluelock.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import su.ju.osvi.bluelock.R

class ActiveUnitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_unit)

        val tvUnitName = this.findViewById<TextView>(R.id.activeUnitTvUnitName)
    }
}