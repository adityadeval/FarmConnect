package ms.cs.farmconnect.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ms.cs.farmconnect.R
import ms.cs.farmconnect.utils.CustomEditText
import ms.cs.farmconnect.utils.FCButton
import ms.cs.farmconnect.utils.FCTextViewBold

class RegisterActivity : BaseActivity() {

    private lateinit var et_first_name: CustomEditText
    private lateinit var et_last_name: CustomEditText
    private lateinit var et_email: CustomEditText
    private lateinit var et_password: CustomEditText
    private lateinit var et_confirm_password: CustomEditText
    private lateinit var cb_tandc: AppCompatCheckBox
    private lateinit var tv_login : FCTextViewBold
    private lateinit var btn_register : FCButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        et_first_name = findViewById(R.id.et_first_name)
        et_last_name = findViewById(R.id.et_last_name)
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
        et_confirm_password = findViewById(R.id.et_confirm_password)
        cb_tandc = findViewById(R.id.cb_tandc)
        tv_login = findViewById(R.id.tv_login)
        btn_register = findViewById(R.id.btn_register)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setupActionBar()

        tv_login.setOnClickListener{
            //val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            //startActivity(intent)

            // Here, onBackPressed makes sure that the user doesn't end up stacking
            // multiple login and register activities on top of each other.
            // Assume, user did : Login --> Register --> Login
            // Now, if he presses back he won't go to the Register screen.
            // Instead the app will now close.
            onBackPressed()

        }

        btn_register.setOnClickListener{
            registerUser()
        }
    } //End of onCreate()

    //Defining all custom methods for RegisterActivity

    fun setupActionBar() {
        val toolbar_registerActivity: Toolbar = findViewById(R.id.toolbar_register_activity)
        setSupportActionBar(toolbar_registerActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }
        toolbar_registerActivity.setNavigationOnClickListener { onBackPressed() }
    }

    // Function to check if user has entered valid registration details.
    private fun validateRegisterDetails(): Boolean {

        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            et_password.text.toString().trim { it <= ' ' } != et_confirm_password.text.toString()
                .trim { it <= ' ' } -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }
            !cb_tandc.isChecked -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to register the user with email and password using FirebaseAuth.
     */

    private fun registerUser() {

        // Before registration, first verify if all fields entered are correct using validateRegisterDetails().
        if (validateRegisterDetails()) {

            // Show progress dialog as soon as all entered data has been validated and the app has just started to
            // store everything in Firebase DB.
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = et_email.text.toString().trim { it <= ' ' }
            val password: String = et_password.text.toString().trim { it <= ' ' }

            // Create an instance and create a register a user with email and password.
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        // Hide the progress dialog
                        hideProgressDialog()

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            showCustomSnackBar(
                                "You have been registered successfully with user id : ${firebaseUser.uid}",
                                false
                            )

                            // Once user has been registered, sign him/her out and send them to the login page.
                            FirebaseAuth.getInstance().signOut()
                            // Call finish() to end register activity. As Login activity sends user to register activity, after
                            // calling finish, user would be directed to Login activity again.
                            finish()

                        } else {
                            // If the registering is not successful then show error message.
                            showCustomSnackBar(task.exception!!.message.toString(), true)
                        }
                    })
        }
    }

} //End of RegisterActivity class