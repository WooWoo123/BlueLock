package su.ju.osvi.bluelock.views

import android.bluetooth.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.email
import org.jetbrains.anko.toast
import su.ju.osvi.bluelock.R
import java.lang.Exception
import java.util.*

class ControlActivity : AppCompatActivity() {
    /**
     * correctUser is used as a safety measure to make sure the correct user is sending commands to the bluetooth device.
     * the variable "device" is a lateinit var that allows the paired device which is fetched from the intent to be used in the whole class.
     * BluetoothGatt, characteristics and gattService are nessecary attributes used in connecting and communicating to a bluetooth device.
     */
    var correctUser : Boolean = false
    lateinit var device        : BluetoothDevice
    var characteristic         : BluetoothGattCharacteristic = BluetoothGattCharacteristic(m_myUUID, 1,1)
    lateinit var bluetoothGatt : BluetoothGatt
    lateinit var gattService   : BluetoothGattService

    companion object {
        /**
         * UUID is a tool used to identify a single bluetooth component/device.
         * UUID = Universially Unique Identifyer
         */
        var m_myUUID: UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")
        var m_myCharacteristicsUUID : UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        val intent = intent
        device = intent.getParcelableExtra("PAIRED_DEVICE")!!
        val control_led_on          = this.findViewById<Button>(R.id.control_led_on)
        val control_led_off         = this.findViewById<Button>(R.id.control_led_off)
        val control_led_connect     = this.findViewById<Button>(R.id.control_led_disconnect)


        /**
         * Buttons used to communicate to the bluetooth Device.
         */
        control_led_on.setOnClickListener{

            if (device != null) {
                if(correctUser) {
                    sendCommand("on")
                } else toast("Wrong User")
                //toast("LED is ON")
            }
        }

        control_led_off.setOnClickListener {
            //  toast("LED is OFF")
            if (device != null) {
                if(correctUser) {
                    sendCommand("off")
                } else toast("Wrong User")
            }
        }

        control_led_connect.setOnClickListener {
            connectToDevice()
        }

    }


    private fun sendCommand(input: String){
        /**
         * Assigning gattService and characteristics using UUID.
         * Sending a command to the Bluetooth device based on input from buttons.
         */
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

    /**
     * connecting bluetoothGatt using the "device" variable which came as a result from the bleScanCallback.
     */
    private fun connectToDevice(){
        Log.v("Control activity", "Connected to device")
        bluetoothGatt  = device.connectGatt(this, false, bleGattCallback)
        checkEmail()

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

    /**
     * Setting the current user email as the active on in the database. This locks the device to this particular email.
     * A password is also generated and saved in two places so that an identical email is not enough to unlock and use the device.
     */
    fun setEmail(){
        toast("Connected to device")
        correctUser = true
        val db  = FirebaseFirestore.getInstance()
        val map : MutableMap<String, Any> = HashMap()
        val mapTwo : MutableMap<String, Any> = HashMap()
        try {
            map["UserEmail"] = Firebase.auth.currentUser!!.email.toString()
            db.collection("LockUser")
                .document("LockUser")
                .set(map)
        }catch (e: Exception){
            toast(e.message.toString())
        }
        try {
            map["Password"] = (Math.random() * 100000000).toInt()
            db.collection("LockUser")
                    .document("LockUser")
                    .set(map)
            mapTwo["Password"] = (Math.random() * 100000000).toInt()
            db.collection("LockUser")
                    .document("UserPassword")
                    .set(map)
        }catch (e: Exception){
            toast(e.message.toString())
        }
    }

    /**
     * Checks if there is any saved email for the bluetooth device in the database, if not (userEmail is "0") or
     * if userEmail is not "0", check if the current users email matches the one that is saved as active for the bluetooth device in the database.
     * If the email check is successful, go to checkPassword and verify the password aswell.
     */
    fun checkEmail() {
        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("LockUser").document("LockUser")
            docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val data = document.data as Map<String, String>

                            if(data["UserEmail"].toString() ==  "0" || data["UserEmail"].toString() == Firebase.auth.currentUser!!.email.toString()){
                                checkPassword(data)
                            }else{
                                toast("Bluetooth Device not available")
                            }
                        }
        }
    }

    /**
     * Verifies if the bluetooth devices password matches the current users password. Both are fetched from the database.
     * If the verification is successful, go to setEmail.
     */
    fun checkPassword(emailMap : Map<String, String>){
        val dbTwo = FirebaseFirestore.getInstance()
        val userDocRef = dbTwo.collection("LockUser").document("UserPassword")
        userDocRef.get()
                .addOnSuccessListener { document ->

                    if(emailMap["Password"].toString() == "" || emailMap["Password"].toString() == document["Password"].toString()){
                        setEmail()
                    }else{
                        toast("Bluetooth Device not available")
                    }
                }
    }
}
