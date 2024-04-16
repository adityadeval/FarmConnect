package ms.cs.farmconnect.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS: String = "users"
    const val FARMCONNECT_PREFERENCES: String = "FarmConnectPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"

    const val READ_STORAGE_PERMISSION_CODE = 2

    const val PICK_IMAGE_REQUEST_CODE = 1

    const val MALE: String = "male"
    const val FEMALE: String = "female"

    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"

    const val USER_PROFILE_IMAGE:String = "User_Profile_Image"

    // Below function would be used by multiple activities. For eg. The UserProfileActivity where the user needs to select a
    // profile image from the gallery. Also the Add Products activity where the user would need to select a product image from the gallery.
    // Adding this function in Constants allows us to easily reuse it in multiple activities.
    // The first parameter for below function is the instance of Activity that is calling this function.
    // It's being used here as, this function contains startActivityForResult() which requires an Activity instance.
    fun showImageChooser(activity: Activity) {
        // Creating an intent with first parameter setting the action to ACTION_PICK
        // which allows a user to pick an item from the device's data storage.
        // The second parameter has been set to URI points to the external content URI
        // for images, which generally refers to all images stored on the user's device in the external storage.

        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // The below method is used to start an activity that you expect a result from.
        // The first parameter has been set to the Intent we just created.
        // The second is a request code to identify our specific request.
        // This request code will be returned in onActivityResult() in our activity,
        // where we can then handle the result. This helps to differentiate between multiple different result intents
        // if we have more than one startActivityForResult() call in our activity.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}