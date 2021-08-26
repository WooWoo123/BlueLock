package su.ju.osvi.bluelock.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.toast
import su.ju.osvi.bluelock.R
import java.util.*

class ActiveUnitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_unit)

        val btnEnd = this.findViewById<Button>(R.id.btnMyActiveUnit)


        btnEnd.setOnClickListener {
            toast("hejhej")
        }
    }


}