package ms.cs.farmconnect.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import ms.cs.farmconnect.R
import ms.cs.farmconnect.firestore.FirestoreClass
import ms.cs.farmconnect.models.User
import ms.cs.farmconnect.utils.Constants
import ms.cs.farmconnect.utils.FCButton
import ms.cs.farmconnect.utils.FCTextView
import ms.cs.farmconnect.utils.FCTextViewBold
import ms.cs.farmconnect.utils.GlideLoader

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User

    private lateinit var toolbar_settings_activity : Toolbar
    private lateinit var iv_user_photo : ImageView
    private lateinit var tv_name : FCTextViewBold
    private lateinit var tv_gender : FCTextView
    private lateinit var tv_email : FCTextView
    private lateinit var tv_mobile_number : FCTextView
    private lateinit var btn_logout : FCButton
    private lateinit var tv_edit : FCTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolbar_settings_activity = findViewById(R.id.toolbar_settings_activity)
        iv_user_photo = findViewById(R.id.iv_user_photo)
        tv_name = findViewById(R.id.tv_name)
        tv_gender = findViewById(R.id.tv_gender)
        tv_email = findViewById(R.id.tv_email)
        tv_mobile_number = findViewById(R.id.tv_mobile_number)
        btn_logout = findViewById(R.id.btn_logout)
        tv_edit = findViewById(R.id.tv_edit)
        setupActionBar()

        btn_logout.setOnClickListener(this@SettingsActivity)
        tv_edit.setOnClickListener(this@SettingsActivity)
    }

    // Create a back button in the toolbar of the Settings Activity.
    private fun setupActionBar() {

        setSupportActionBar(toolbar_settings_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        // If user clicks on the back button (ic_white_color_back_24dp) call the
        // onBackPressed() which is basically clicking Back once which makes us go back
        // to whichever activity was present before the Settings Activity.
        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails() {

        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class to get the user details from firestore.
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }


    // Below function would be called from the Firestore class by the getUserDetails() function's
    // onSuccessListener(). So in simple words, if the User's firestore doc was fetched successfully
    // from Firestore, then userDetailsSuccess() would be called if Settings Activity was the one who
    // requested for User Details from Firestore.

    fun userDetailsSuccess(user: User) {

        mUserDetails = user

        hideProgressDialog()

        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, iv_user_photo)

        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        tv_mobile_number.text = "${user.mobile}"
    }

    // Below method is called when user opens the activity for the first time, or when the user
    // leaves the app momentarily and comes back to it again.
    override fun onResume() {
        super.onResume()

        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_logout -> {

                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    // NEW_TASK flag : This flag is used when you're starting a new activity outside the context of an existing activity.
                    // CLEAR_TASK flag : This flag will remove any existing activities in the current task stack before starting the new activity.
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }

            }
        }
    }

}