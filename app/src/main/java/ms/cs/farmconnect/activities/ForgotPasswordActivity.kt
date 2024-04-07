package ms.cs.farmconnect.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import ms.cs.farmconnect.R
import ms.cs.farmconnect.utils.CustomEditText
import ms.cs.farmconnect.utils.FCButton

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var toolbar_forgot_password_activity: Toolbar
    private lateinit var button_submit: FCButton
    private lateinit var et_email: CustomEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        button_submit = findViewById(R.id.button_submit)
        et_email = findViewById(R.id.et_email)
        toolbar_forgot_password_activity = findViewById(R.id.toolbar_forgot_password_activity)
        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_forgot_password_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }

        toolbar_forgot_password_activity.setNavigationOnClickListener { onBackPressed() }

        button_submit.setOnClickListener{
            val email: String = et_email.text.toString().trim { it <= ' ' }
            if (email.isEmpty()) {
                showCustomSnackBar(resources.getString(R.string.err_msg_enter_email), true)
            }else {

                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))

                // This piece of code is used to send the reset password link to the user's email id if the user is registered.
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->

                        // Hide the progress dialog
                        hideProgressDialog()

                        if (task.isSuccessful) {
                            // Show the toast message and finish the forgot password activity to go back to the login screen.
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                resources.getString(R.string.email_sent_success),
                                Toast.LENGTH_LONG
                            ).show()

                            finish()
                        } else {
                            showCustomSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
            }
        }

    }

}