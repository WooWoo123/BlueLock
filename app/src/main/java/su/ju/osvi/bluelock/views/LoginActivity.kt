package su.ju.osvi.bluelock.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import su.ju.osvi.bluelock.R
import su.ju.osvi.bluelock.extentions.Extensions.toast
import su.ju.osvi.bluelock.utils.FirebaseUtils.firebaseAuth

class LoginActivity : AppCompatActivity() {

    private companion object {
        private const val TAG = "LoginActivity"
        private const val RC_GOOGLE_SIGN_IN = 4926
    }

    private lateinit var auth               : FirebaseAuth
    private lateinit var googleSignInButton : SignInButton
    private lateinit var nestedScrollView   : NestedScrollView
    private lateinit var signInEmail        : String
    private lateinit var signInPassword     : String
    private lateinit var signInInputsArray  : Array<TextInputEditText>
    private lateinit var loginButton        : AppCompatButton
    private lateinit var createAccountButton: AppCompatTextView
    private lateinit var resetPasswordButton: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        signInInputsArray = arrayOf(SignInEmail, SignInPassword)
        // hiding the action bar
        supportActionBar!!.hide()


        // Initialize Firebase Auth
        auth                = Firebase.auth
        googleSignInButton  = findViewById<SignInButton>(R.id.googleSignInButton)
        nestedScrollView    = findViewById<NestedScrollView>(R.id.nestedScrollView)
        loginButton         = findViewById<AppCompatButton>(R.id.appCompatButtonLogin)
        createAccountButton = findViewById<AppCompatTextView>(R.id.textViewLinkRegister)
        resetPasswordButton = findViewById<AppCompatTextView>(R.id.reset_password)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleClient = GoogleSignIn.getClient(this, gso)

        googleSignInButton.setOnClickListener {
            val signInIntent = googleClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)

        }

        loginButton.setOnClickListener {
            signInUser()
        }

        createAccountButton.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
            finish()
        }
        resetPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
        //if(currentUser != null){

        //}
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user == null)    {
            Log.w(TAG, "User is null, not going to navigate")
            return
        }

        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
        
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()

    private fun signInUser() {
        signInEmail = SignInEmail.text.toString().trim()
        signInPassword = SignInPassword.text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        toast("signed in successfully")
                        finish()
                    } else {
                        toast("sign in failed")
                    }
                }
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }
    }
}
