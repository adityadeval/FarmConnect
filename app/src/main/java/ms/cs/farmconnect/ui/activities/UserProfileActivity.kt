package ms.cs.farmconnect.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
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
    private lateinit var rb_female : CustomRadioButton

    private lateinit var toolbar_user_profile_activity : Toolbar
    private lateinit var tv_title : TextView

    // Creating and initializing mutable variable userDetails of type User class, to an instance of User class.
    private lateinit var mUserDetails: User

    private var mSelectedImageFileUri: Uri? = null

    private var mUserProfileImageURL: String = ""
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
        rb_female = findViewById(R.id.rb_female)
        toolbar_user_profile_activity = findViewById(R.id.toolbar_user_profile_activity)
        tv_title = findViewById(R.id.tv_title)

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

        // Set the existing values to the UI.
        et_first_name.setText(mUserDetails.firstName)
        et_last_name.setText(mUserDetails.lastName)
        et_email.setText(mUserDetails.email)

        et_email.isEnabled = false

        if (mUserDetails.profileCompleted == 0) {
            // Since profile isn;t yet complete, show the title as Please complete your profile.
            tv_title.text = resources.getString(R.string.title_complete_profile)

            // Setting isEnabled to false will now freeze that EditText and
            // the user would no longer be able to edit it.
            et_first_name.isEnabled = false
            et_last_name.isEnabled = false

        } else {

            // Display the customized toolbar which has a back button, only when user has already
            // completed his user profile.
            setupActionBar()

            // Since User's profile would have been completed at this point, show title as Edit profile.
            tv_title.text = resources.getString(R.string.title_edit_profile)

            // Load the image using the GlideLoader class with the use of Glide Library.
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image, iv_user_photo)

            if (mUserDetails.mobile != 0L) {
                et_mobile_number.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE) {
                rb_male.isChecked = true
            } else {
                rb_female.isChecked = true
            }
        }

//        // Setting isEnabled to false will now freeze that EditText and the user would no longer be able to edit it.
//        et_first_name.isEnabled = false
//        et_first_name.setText(mUserDetails.firstName)
//
//        et_last_name.isEnabled = false
//        et_last_name.setText(mUserDetails.lastName)
//
//        et_email.isEnabled = false
//        et_email.setText(mUserDetails.email)

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

                        showProgressDialog(resources.getString(R.string.please_wait))

                        // The mSelectedImageFileUri is going to contain the uri of the locally stored image.
                        // It is initialized by the onActivityResult() function which gets executed by Android right after :
                        // User clicks on iv_user_photo --> Constants.showImageChooser() is called --> startActivityForResult() is called in the context of UserProfileActivity
                        if (mSelectedImageFileUri != null) {
                            // The below function will :
                            // 1) Upload the image onto the cloud storage and
                            // 2) Call imageUploadSuccess() which will :
                            //    a) Initialize mUserProfileImageURL with the download link for our image from the Cloud storage.
                            //    b) Call updateUserProfileDetails() which will fetch user's mobile number and gender from the layout, the url for the user's profile image
                            //       on Cloud storage and update the user's existing document on Firestore with these newly added details.

                            FirestoreClass().uploadImageToCloudStorage(
                                this@UserProfileActivity,
                                mSelectedImageFileUri
                            )
                        }else{
                            updateUserProfileDetails()
                        }
                    }
                }

            } // end of  when (v.id)
        } // end of  if (v != null)
    } // end of onClick()


    // Below function will :
    // 1) Fetch the Mobile number, gender and url for user's profile image (ONLY IF user uploaded image in current session)
    //    and update all these details inside the current user's firestore document.
    // 2) It'll also call userProfileUpdateSuccess() which in turn hides the progress dialog and sends user to the MainActivity.
    private fun updateUserProfileDetails() {
        // In the below HashMap we'll store all the information that the user can enter on the UserProfile Activity.
        // So, in this activity we only need to get mobile number and gender. Rest of the fields in this Activity are non-editable.
        val userHashMap = HashMap<String, Any>()

        // Trim empty spaces and check if first name that the user entered actually differs from what
        // we already have. If it does, only then add the new username to hashmap.
        val firstName = et_first_name.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = et_last_name.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString() ) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        //showProgressDialog(resources.getString(R.string.please_wait))
        userHashMap[Constants.COMPLETE_PROFILE] = 1

        FirestoreClass().updateUserProfileData(
            this@UserProfileActivity,
            userHashMap
        )
    }


    // Below function is responsible for mainly moving the user to the MainActivity.
    fun userProfileUpdateSuccess() {
        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        // Redirect to the Main Screen after profile completion.
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
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


    // Below is the flow explaining how this function will get called.
    // User clicks on the imageView --> OnClick method is called, since the imageView has an OnClick Listener.
    // --> The listener checks if the app has the storage permission. If yes, it calls the showImageChooser() method inside Constants.
    // --> This method then builds up an intent with the action = Pick and location = the device's gallery (in simple words)
    // startActivityForResult is called in the Context of the UserProfileActivity with the intent we built above, and access code as PICK_IMAGE_REQUEST_CODE.
    // After the user selects an image from a pop-up dialog for uploading an image, the below function is called.
    // startActivityForResult() was called in the context of UserProfileActivity. Hence the onActivityResult() is also implemented in the same activity.
    // Keeping it simple : Below function is called, immediately after user selects an image from the gallery in the dialog shown to him/her.
    // What the function achieves :
    // 1) Stores the uri of the image selected by user into mSelectedImageFileUri which will also be used by uploadImageToCloudStorage() of the FirestoreClass.
    // 2) Displays this image in ImageView using glide.
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!

                        //iv_user_photo.setImageURI(selectedImageFileUri)
                        GlideLoader(this@UserProfileActivity).loadUserPicture(
                            mSelectedImageFileUri!!,
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

    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

}