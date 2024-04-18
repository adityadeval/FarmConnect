package ms.cs.farmconnect.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import ms.cs.farmconnect.R
import ms.cs.farmconnect.firestore.FirestoreClass
import ms.cs.farmconnect.models.User
import ms.cs.farmconnect.utils.FCTextView
import ms.cs.farmconnect.utils.FCTextViewBold
import ms.cs.farmconnect.utils.GlideLoader

class SettingsActivity : BaseActivity() {

    private lateinit var toolbar_settings_activity : Toolbar
    private lateinit var iv_user_photo : ImageView
    private lateinit var tv_name : FCTextViewBold
    private lateinit var tv_gender : FCTextView
    private lateinit var tv_email : FCTextView
    private lateinit var tv_mobile_number : FCTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        toolbar_settings_activity = findViewById(R.id.toolbar_settings_activity)
        iv_user_photo = findViewById(R.id.iv_user_photo)
        tv_name = findViewById(R.id.tv_name)
        tv_gender = findViewById(R.id.tv_gender)
        tv_email = findViewById(R.id.tv_email)
        tv_mobile_number = findViewById(R.id.tv_mobile_number)
        setupActionBar()
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

}