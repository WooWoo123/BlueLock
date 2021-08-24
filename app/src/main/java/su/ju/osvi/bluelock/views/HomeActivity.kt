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
import su.ju.osvi.bluelock.R
import su.ju.osvi.bluelock.extentions.Extensions.toast

class HomeActivity : AppCompatActivity() {
    val databaseObj = Database()

    private companion object {
        private const val TAG = "HomeActivity"
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val collectionButton = findViewById<Button>(R.id.btn_addCollection)
        val unitsButton         = findViewById<Button>(R.id.btnUnits)
        val myActiveUnitButton  = findViewById<Button>(R.id.btnMyActiveUnit)

        collectionButton.setOnClickListener {
            addCollection()
        }

        unitsButton.setOnClickListener {
            val intent = Intent(this, SelectDeviceActivity::class.java)
            startActivity(intent)
        }
        myActiveUnitButton.setOnClickListener {
            val intent = Intent(this, ActiveUnitActivity::class.java)
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

    fun addCollection(){
        databaseObj.password = (Math.random() * 100000000).toInt()

        val db = FirebaseFirestore.getInstance()
        db.collection("TestCollection")
            .add(databaseObj)
    }
}