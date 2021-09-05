package su.ju.osvi.bluelock.views

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import su.ju.osvi.bluelock.R

class SelectDeviceActivity : AppCompatActivity() {
    val btDevices = mutableListOf<BluetoothDevice>()

    companion object {
        const val TAG = "MYBTSCANNER"
        const val BLUETOOTH_REQUEST_CODE = 1
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    private fun makeRequestCoarse() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            1)
    }
    private fun makeRequestFine() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1)
    }
    private fun makeRequestBluetooth() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.BLUETOOTH),
            1)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_device)
        val select_device_refresh   = this.findViewById<Button>(R.id.select_device_refresh)
        val select_device_list      = this.findViewById<ListView>(R.id.select_device_list)
        val adapter                 = ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, btDevices)
        select_device_list.adapter  = adapter

        select_device_list.setOnItemClickListener { _, _, position, id ->
            val unitItem: BluetoothDevice = btDevices[position]
            val intent                    = Intent(this, ControlActivity::class.java)
            intent.putExtra("PAIRED_DEVICE", btDevices[0])
            startActivity(intent)
        }

        select_device_refresh.setOnClickListener {
            val select_device_list     = this.findViewById<ListView>(R.id.select_device_list)
            val deviceName             = this.findViewById<TextView>(R.id.device_name)
            val adapter                = ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, btDevices)
            select_device_list.adapter = adapter
            val permissionCoarse = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            val permissionFine = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val permissionBlueTooth = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            )
            //check if coarse location is permitted
            if (permissionCoarse != PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission for coarse location denied")
                makeRequestCoarse()
            }
            if (permissionCoarse == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission for coarse location granted")
            }
            //check if fine location is permitted
            if (permissionFine != PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission for fine location denied")
                makeRequestFine()
            }
            if (permissionCoarse == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission for fine location granted")
            }
            //check if bluetooth is permitted
            if (permissionBlueTooth != PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission for Blueooth denied")
                makeRequestBluetooth()
            }
            if (permissionBlueTooth == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission for Blueooth granted")
            }

            Log.v(TAG, "Button clicked")
            if (bluetoothAdapter.isEnabled) {
                //  start scanning
                Log.v(TAG, "Start scan called")
                startBLEScan()
            } else {
                Log.v(TAG, "Sending intent")
                val btIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(btIntent, BLUETOOTH_REQUEST_CODE)
            }
            if(btDevices.size != 0) {
                deviceName.setText(btDevices[0].name)
            }
            adapter.notifyDataSetChanged()
        }

    }
    private fun startBLEScan() {
        Log.v(TAG, "StartbleScan")
        val scanFilter                           = ScanFilter.Builder().build()
        val scanFilters: MutableList<ScanFilter> = mutableListOf()
        scanFilters.add(scanFilter)
        val scanSettings                         = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        Log.v(TAG, "Start scanning")
        bluetoothAdapter.bluetoothLeScanner.startScan(
            scanFilters,
            scanSettings,
            bleScanCallBack
        )
    }
    private val bleScanCallBack: ScanCallback by lazy {
        Log.v(TAG, "scanCallBack")
        object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {

                val bluetoothDevice       = result?.device
                if (bluetoothDevice?.name == "Adafruit Bluefruit LE" && btDevices.size == 0) {
                    Log.v(TAG, "Device Name ${result!!.device.name}")
                    btDevices.add(bluetoothDevice)
                    bluetoothAdapter.bluetoothLeScanner.stopScan(bleScanCallBack)

                    Log.v(TAG, "btDevices size: ${btDevices.size}")
                    Log.v(TAG, btDevices[0].name)
                    Log.v(TAG, bluetoothDevice.address)
                }
            }
        }
    }
}