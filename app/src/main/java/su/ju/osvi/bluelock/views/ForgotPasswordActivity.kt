package su.ju.osvi.bluelock.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_forgot_password.*
import su.ju.osvi.bluelock.R
import su.ju.osvi.bluelock.views.SelectDeviceActivity.Companion.TAG

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        val email = send_to_email

        reset_password_button.setOnClickListener{
            Firebase.auth.sendPasswordResetEmail(email.toString()).addOnCompleteListener {
                task -> if (task.isSuccessful) {
                Log.d(TAG, "Password reset sent to submitted email")
            }

            }
        }
    }
}