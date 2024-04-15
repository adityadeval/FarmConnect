package ms.cs.farmconnect.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ms.cs.farmconnect.R
import ms.cs.farmconnect.firestore.FirestoreClass
import ms.cs.farmconnect.models.User
import ms.cs.farmconnect.utils.Constants
import ms.cs.farmconnect.utils.CustomEditText
import ms.cs.farmconnect.utils.CustomRadioButton
import ms.cs.farmconnect.utils.FCButton
import ms.cs.farmconnect.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var et_first_name : CustomEditText
    private lateinit var et_last_name : CustomEditText
    private lateinit var et_email : CustomEditText
    private lateinit var et_mobile_number : CustomEditText
    private lateinit var iv_user_photo : ImageView
    private lateinit var btn_submit : FCButton
    private lateinit var rb_male : CustomRadioButton

    // Creating and initializing mutable variable userDetails of type User class, to an instance of User class.
    private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        et_first_name = findViewById(R.id.et_first_name)
        et_last_name = findViewById(R.id.et_last_name)
        et_email = findViewById(R.id.et_email)
        et_mobile_number = findViewById(R.id.et_mobile_number)
        iv_user_photo = findViewById(R.id.iv_user_photo)
        btn_submit = findViewById(R.id.btn_submit)
        rb_male = findViewById(R.id.rb_male)

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {

            // getParcelableExtra() with just one String parameter is deprecated.
            // However, the new getParcelableExtra() which takes two arguments, first is a String which is the name of the Extra,
            // and second Class object representing the type of the Parcelable, requires API level 33 or higher.
            // So below if condition first checks the version of the device. If it's API 33 or up, it uses the new method,
            // else it uses the deprecated method.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS, User::class.java)!!
            } else {
                // Use the deprecated method or handle differently for older devices
                mUserDetails = intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)!!
            }
            // Get the user details from intent as a ParcelableExtra.
            //userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS, User::class.java) ?: User()
        }

        // Setting isEnabled to false will now freeze that EditText and the user would no longer be able to edit it.
        et_first_name.isEnabled = false
        et_first_name.setText(mUserDetails.firstName)

        et_last_name.isEnabled = false
        et_last_name.setText(mUserDetails.lastName)

        et_email.isEnabled = false
        et_email.setText(mUserDetails.email)

        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_submit.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {

                    // Here we will check if the READ_EXTERNAL_STORAGE permission is already granted for our app.
                    // If not, then execution of else happens where we request for the permission.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        //showCustomSnackBar("You already have the storage permission.", false)
                        Constants.showImageChooser(this@UserProfileActivity)
                    } else {

                        /*Below function requests storage permission from the user */

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit ->{

                    if(validateUserProfileDetails()){

                        // In the below HashMap we'll store all the information that the user can enter on the UserProfile Activity.
                        // So, in this activity we only need to get mobile number and gender. Rest of the fields in this Activity are non-editable.
                        val userHashMap = HashMap<String, Any>()

                        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

                        val gender = if (rb_male.isChecked) {
                            Constants.MALE
                        } else {
                            Constants.FEMALE
                        }

                        if (mobileNumber.isNotEmpty()) {
                            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
                        }

                        userHashMap[Constants.GENDER] = gender

                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().updateUserProfileData(
                            this@UserProfileActivity,
                            userHashMap
                        )
                    }
                }
            }
        }
    }

    fun userProfileUpdateSuccess() {
        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        // Redirect to the Main Screen after profile completion.
        startActivity(Intent(this@UserProfileActivity, MainActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            // Below if would be true, when on the Grant permission dialog, the user clicked on one of the options,
            // which would be stored in grantResults[0] (0th position as the permissions array also has the Storage permission in the 0th position),
            // so essentially grantResults array won't be empty AND the array's first position would store an integer equivalent to the
            // permission granted state's integer.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //showCustomSnackBar("The storage permission is granted.", false)
                Constants.showImageChooser(this@UserProfileActivity)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        val selectedImageFileUri = data.data!!

                        //iv_user_photo.setImageURI(selectedImageFileUri)
                        GlideLoader(this@UserProfileActivity).loadUserPicture(
                            selectedImageFileUri,
                            iv_user_photo
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    // Below function checks if details entered by user on the UserProfile page are valid.
    // It's called when user clicls on the submit button of the UserProfileActivity.
    // Here we only check if the mobile number field has been left empty or not.
    // The reason for checking only this is that the first name, last name and email are non-editable,
    // the gender radio buttons are set to select male by default and the user image is not mandatory.
    private fun validateUserProfileDetails(): Boolean {
        return when {
            // Check if mobile number is empty. If it is, inform user that mobile number is mandatory using a SnackBar.
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }




}