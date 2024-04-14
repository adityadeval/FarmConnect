package ms.cs.farmconnect.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import ms.cs.farmconnect.R
import ms.cs.farmconnect.firestore.FirestoreClass
import ms.cs.farmconnect.models.User
import ms.cs.farmconnect.utils.Constants
import ms.cs.farmconnect.utils.CustomEditText
import ms.cs.farmconnect.utils.FCButton
import ms.cs.farmconnect.utils.FCTextView
import ms.cs.farmconnect.utils.FCTextViewBold

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var tv_register : FCTextViewBold
    private lateinit var button_login : FCButton
    private lateinit var tv_forgot_password : FCTextView
    private lateinit var et_email: CustomEditText
    private lateinit var et_password: CustomEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tv_register = findViewById(R.id.tv_register)
        button_login = findViewById(R.id.button_login)
        tv_forgot_password = findViewById(R.id.tv_forgot_password)
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // Click event assigned to Forgot Password text view.
        tv_forgot_password.setOnClickListener(this)
        // Click event assigned to Login button.
        button_login.setOnClickListener(this)
        // Click event assigned to the Register text view.
        tv_register.setOnClickListener(this)
    }

    fun userLoggedInSuccess(user: User) {

        // Hide the progress dialog.
        hideProgressDialog()

        // Print the user details in the log as of now.
        Log.i("First Name: ", user.firstName)
        Log.i("Last Name: ", user.lastName)
        Log.i("Email: ", user.email)


        if (user.profileCompleted == 0) {
            // If the user profile is incomplete then launch the UserProfileActivity.
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            // Redirect the user to Main Screen after log in.
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
        finish()
    }

    // 'v' is the view that's being clicked on by the user.
    // v.id return the id of that view.
    // Based on this id, we're going to write, what happens for a certain id.
    // For eg. If id of Forgot password is is returned, it means forgot password was clicked on.
    // So we implement the functionality for forgot password in the {}.
    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.tv_forgot_password -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.button_login -> {

                    logInRegisteredUser()

                }

                R.id.tv_register -> {
                    // Launch the register screen when the user clicks on the text.
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }


    private fun logInRegisteredUser() {

        if (validateLoginDetails()) {

            showProgressDialog(resources.getString((R.string.please_wait)))

            val email = et_email.text.toString().trim {it <= ' '}
            val password = et_password.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {task ->

                    if (task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LoginActivity)
                    } else {
                        hideProgressDialog()
                        showCustomSnackBar(task.exception!!.message.toString(), true)
                    }
                }

        }
    }

}