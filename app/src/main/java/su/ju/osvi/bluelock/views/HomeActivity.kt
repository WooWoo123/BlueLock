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
        private const val RESETED_DB_MAIL = "0"
        private const val RESETED_DB_PASSWORD = ""
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val unitsButton         = findViewById<Button>(R.id.btnUnits)
        val btnEnd              = findViewById<Button>(R.id.btnMyActiveUnit)

        btnEnd.setOnClickListener {
            disconnectEmail()
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
    fun disconnectEmail() {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("LockUser").document("LockUser")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val data = document.data as Map<String, String>
                    if(data["UserEmail"].toString() == RESETED_DB_MAIL || data["UserEmail"].toString() == Firebase.auth.currentUser!!.email.toString()) {
                        disconnectPassword(data)
                    }else{
                        toast("Wrong user, cant disconnect (email)")
                    }
                }
            }
    }

    fun disconnectPassword(emailMap : Map<String, String>){
        val dbTwo = FirebaseFirestore.getInstance()
        val userDocRef = dbTwo.collection("LockUser").document("UserPassword")
        userDocRef.get()
            .addOnSuccessListener { document ->
                if(emailMap["Password"].toString() == RESETED_DB_PASSWORD || emailMap["Password"].toString() == document["Password"].toString()){
                    resetEmail()
                }else{
                    toast("Wrong user, cant disconnect (password)")
                }

            }

    }

    fun resetEmail(){
        val db      = FirebaseFirestore.getInstance()
        val dbTwo   = FirebaseFirestore.getInstance()
        val mapTwo  : MutableMap<String, Any> = HashMap()
        val map     : MutableMap<String, Any> = HashMap()
        try {
            map["UserEmail"] = RESETED_DB_MAIL
            map["Password"]  = RESETED_DB_PASSWORD
            db.collection("LockUser")
                .document("LockUser")
                .set(map)
        }catch (e: Exception){
            toast(e.message.toString())
        }
        try {
            mapTwo["UserEmail"] = RESETED_DB_MAIL
            mapTwo["Password"]  = RESETED_DB_PASSWORD
            dbTwo.collection("LockUser")
                    .document("UserPassword")
                    .set(map)
        }catch (e: Exception){
            toast(e.message.toString())
        }
    }

}