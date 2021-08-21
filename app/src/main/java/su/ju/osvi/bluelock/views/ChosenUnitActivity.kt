package su.ju.osvi.bluelock.views

import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import su.ju.osvi.bluelock.R

class ChosenUnitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chosen_unit)
        val btnConfirm               = this.findViewById<Button>(R.id.ChosenUnitConfirmButton)
        val intent                   = intent
        val device: BluetoothDevice? = intent.getParcelableExtra<BluetoothDevice>("PAIRED_DEVICE")
        btnConfirm.setOnClickListener {
            val intent               = Intent(this, ControlActivity::class.java)
            intent.putExtra("DEVICE_PAIRED", device)
            startActivity(intent)
        }
    }
}