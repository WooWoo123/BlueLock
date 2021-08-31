package su.ju.osvi.bluelock.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.toast
import su.ju.osvi.bluelock.R
import su.ju.osvi.bluelock.extentions.Extensions.toast
import java.util.*

class HomeActivity : AppCompatActivity() {


    private companion object {
        private const val TAG = "HomeActivity"
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val unitsButton         = findViewById<Button>(R.id.btnUnits)
        val btnEnd              = findViewById<Button>(R.id.btnMyActiveUnit)



        btnEnd.setOnClickListener {
            disconnect()
        }

        unitsButton.setOnClickListener {
            val intent = Intent(this, SelectDeviceActivity::class.java)
            startActivity(intent)
        }

        auth = Firebase.auth
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.googleLogout) {
            Log.i(TAG, "Logout")
            //Logout the user
            auth.signOut()
            val logoutIntent = Intent(this, LoginActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntent)

        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {

    }
    fun disconnect() {
        val db = FirebaseFirestore.getInstance()
        val dbTwo = FirebaseFirestore.getInstance()
        val docRef = db.collection("LockUser").document("LockUser")
        val userDocRef = dbTwo.collection("LockUser").document("UserPassword")
        var password : String = "hej"

        userDocRef.get()
                .addOnSuccessListener { document ->
                    password =  document["Password"].toString()
                }

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val data = document.data as Map<String, String>


                    if(data["UserEmail"].toString().equals("0") || data["UserEmail"].toString() == Firebase.auth.currentUser!!.email.toString()
                            && data["Password"].toString() == "" || data["Password"].toString() == password){
                        resetEmail()
                    }else{
                        toast("Wrong user, cant disconnect")
                    }
                }
            }
    }

    fun resetEmail(){
        toast("resetEmail")
        val db  = FirebaseFirestore.getInstance()
        val dbTwo = FirebaseFirestore.getInstance()
        val mapTwo : MutableMap<String, Any> = HashMap()
        val map : MutableMap<String, Any> = HashMap()
        try {
            map["UserEmail"] = "0"
            map["Password"]  = ""
            db.collection("LockUser")
                .document("LockUser")
                .set(map)
        }catch (e: Exception){
            toast(e.message.toString())
        }
        try {
            mapTwo["UserEmail"] = "0"
            mapTwo["Password"]  = ""
            dbTwo.collection("LockUser")
                    .document("UserPasword")
                    .set(map)
        }catch (e: Exception){
            toast(e.message.toString())
        }
    }

}