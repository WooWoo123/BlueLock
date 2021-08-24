package su.ju.osvi.bluelock.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_create_account.*
import su.ju.osvi.bluelock.R
import su.ju.osvi.bluelock.extentions.Extensions.toast
import su.ju.osvi.bluelock.utils.FirebaseUtils.firebaseAuth
import su.ju.osvi.bluelock.utils.FirebaseUtils.firebaseUser

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var scrollView                 : NestedScrollView
    private lateinit var userEmail                  : String
    private lateinit var userPassword               : String

    private lateinit var createAccountInputsArray   : Array<TextInputEditText>
    private lateinit var createAccountButton        : AppCompatButton
    private lateinit var alreadyAccountButton       : AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        createAccountInputsArray = arrayOf(Email, Password, ConfirmPassword)

        scrollView              = findViewById<NestedScrollView>(R.id.nestedScrollView)


        createAccountButton     = findViewById<AppCompatButton>(R.id.CreateAccount)
        alreadyAccountButton    = findViewById<AppCompatTextView>(R.id.LoginLink)



        createAccountButton.setOnClickListener {
           signIn()
        }

        alreadyAccountButton.setOnClickListener {
            toast("please sign into your account")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            startActivity(Intent(this, HomeActivity::class.java))
            toast("welcome back")
        }
    }

    private fun notEmpty(): Boolean = Email.text.toString().trim().isNotEmpty() &&
            Password.text.toString().trim().isNotEmpty() &&
            ConfirmPassword.text.toString().trim().isNotEmpty()

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() &&
            Password.text.toString().trim() == ConfirmPassword.text.toString().trim()
        ) {
            identical = true
        } else if (!notEmpty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        } else {
            toast("passwords are not matching !")
        }
        return identical
    }

    private fun signIn() {
        if (identicalPassword()) {
            // identicalPassword() returns true only  when inputs are not empty and passwords are identical
            userEmail = Email.text.toString().trim()
            userPassword = Password.text.toString().trim()

            /*create a user*/
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("created account successfully !")
                        sendEmailVerification()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        toast("failed to Authenticate !")
                    }
                }
        }
    }

    /* send verification email to the new user. This will only
    *  work if the firebase user is not null.
    */

    private fun sendEmailVerification() {
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("email sent to $userEmail")
                }
            }
        }
    }
}
