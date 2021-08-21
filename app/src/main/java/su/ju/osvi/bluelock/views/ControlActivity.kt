package su.ju.osvi.bluelock.views

import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import org.jetbrains.anko.toast
import su.ju.osvi.bluelock.R
import java.lang.Exception
import java.util.*

class ControlActivity : AppCompatActivity() {
    lateinit var device        : BluetoothDevice
    var characteristic         : BluetoothGattCharacteristic = BluetoothGattCharacteristic(m_myUUID, 1,1)
    lateinit var bluetoothGatt : BluetoothGatt
    lateinit var gattService   : BluetoothGattService

    companion object {
        var m_myUUID: UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
        var m_myCharacteristicsUUID : UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        val intent = intent
        device = intent.getParcelableExtra("DEVICE_PAIRED")!!
        val control_led_on          = this.findViewById<Button>(R.id.control_led_on)
        val control_led_off         = this.findViewById<Button>(R.id.control_led_off)
        val control_led_connect     = this.findViewById<Button>(R.id.control_led_disconnect)

        control_led_on.setOnClickListener{

            if (device != null) {
                sendCommand("on")
                //toast("LED is ON")
            }
        }

        control_led_off.setOnClickListener {
            //  toast("LED is OFF")
            if (device != null) {
                sendCommand("off")
            }
        }

        control_led_connect.setOnClickListener {
            connectToDevice()
        }
    }

    private fun sendCommand(input: String){
        try {
            gattService  = bluetoothGatt.getService(m_myUUID)
        }catch (e : Exception){
            toast("gattService declaration failed")
        }

        try{
            characteristic = gattService.getCharacteristic(m_myCharacteristicsUUID)!!
        }catch(e : Exception){
            toast("characteristics declaration failed")
        }

        try{
            characteristic.setValue(input)
        }catch(e : Exception){
            toast("characterstics.setValue failed")
        }
        try{
            bluetoothGatt.writeCharacteristic(characteristic)
        }catch(e : Exception){
            toast("writeCharact failed")
        }
    }

    private fun connectToDevice(){
        Log.v("Control activity", "Connected to device")
        toast("Connected to device")
        bluetoothGatt  = device.connectGatt(this, false, bleGattCallback)
    }

    private val bleGattCallback : BluetoothGattCallback by lazy {
        object : BluetoothGattCallback() {

            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                Log.v("Control Activity", "onConnectionStateChanged")
                if(newState == BluetoothProfile.STATE_CONNECTED){
                    bluetoothGatt.discoverServices()
                }
            }
            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                Log.v("Control Activity", "onServicesDiscovered")
                val service               = gatt!!.getService(m_myUUID)
                val characteristics       = service.getCharacteristic(m_myCharacteristicsUUID)
                characteristics.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                Log.v("Control Activity", "onCharacteristicsRead")
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                Log.v("Control Activity", "onCharacteristicsChanged")
            }
        }
    }
}