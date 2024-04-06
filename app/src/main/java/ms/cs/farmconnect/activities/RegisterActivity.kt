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
import ms.cs.farmconnect.R
import ms.cs.farmconnect.utils.CustomEditText
import ms.cs.farmconnect.utils.FCButton
import ms.cs.farmconnect.utils.FCTextViewBold

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

        val tv_login : FCTextViewBold = findViewById(R.id.tv_login)
        tv_login.setOnClickListener{
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        val btn_register : FCButton = findViewById(R.id.btn_register)
        btn_register.setOnClickListener{
            validateRegisterDetails()
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

    private fun validateRegisterDetails(): Boolean {

        val et_first_name : CustomEditText = findViewById(R.id.et_first_name)
        val et_last_name : CustomEditText = findViewById(R.id.et_last_name)
        val et_email : CustomEditText = findViewById(R.id.et_email)
        val et_password : CustomEditText = findViewById(R.id.et_password)
        val et_confirm_password : CustomEditText = findViewById(R.id.et_confirm_password)
        val cb_tandc : AppCompatCheckBox = findViewById(R.id.cb_tandc)

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
                showCustomSnackBar(resources.getString(R.string.registration_details_are_valid), false)
                true
            }
        }
    }

} //End of RegisterActivity class