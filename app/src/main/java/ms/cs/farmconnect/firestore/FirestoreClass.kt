package ms.cs.farmconnect.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ms.cs.farmconnect.ui.activities.LoginActivity
import ms.cs.farmconnect.ui.activities.RegisterActivity
import ms.cs.farmconnect.ui.activities.UserProfileActivity
import ms.cs.farmconnect.models.User
import ms.cs.farmconnect.ui.activities.AddProductActivity
import ms.cs.farmconnect.ui.activities.SettingsActivity
import ms.cs.farmconnect.utils.Constants

// This class will contain all operations performed in the Cloud Firestore.
class FirestoreClass {

    // Creating an instance of Firebase FireStore and storing it in mFireStore.
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // Checks if a collection called "users" exists in the FireStore instance mFireStore. Creates one if it doesn't exist.
        mFireStore.collection(Constants.USERS)
            // Pass the doc id, which in this case is the id field present in User class
            .document(userInfo.id)
            // Here userInfo contains multiple fields containing user's data.
            // The SetOptions.merge() is going to be helpful for the updateUserProfileData(), where
            // we would be passing key value pairs for the mobile number and gender in the form of a hashmap.
            // So we won't be completely erasing everything in the doc, and replacing it with the new key value pairs.
            // Instead, we'll just update the values of keys mobile and gender, and keep rest of the values intact.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                // RegisterActivity extends Base Activity
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    fun getCurrentUserID(): String {
        // Create an instance of FirebaseAuth's current user.
        val currentUser = FirebaseAuth.getInstance().currentUser

        // currentUserID will store the user id of an actual user who's currently logged in.
        // Note : The id is coming from the 'Authentication' module of Firebase and not the 'Firestore'
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {

        // We pass reference to the collection from which we need to fetch the data.
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
            // In the above line, 'document' is a DocumentSnapshot' returned by Firestore if the
                // read operation is successfull.
                Log.i(activity.javaClass.simpleName, document.toString())

                // Below line attempts to convert the 'DocumentSnapshot' into the structure of the User class created by us.
                // Object of User class is created with it's variables storing values of corresponding variables in the Firestore doc.
                // This newly generated object is referenced by 'user'
                val user = document.toObject(User::class.java)!!

                // getSharedPreferences(String name, int mode)
                // First parameter is the name of the preferences file that'll be created if it doesn't exist.
                // Second is the operating mode of the preferences file.
                // Current mode value ensures that the file can only be accessed by the calling application.
                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.FARMCONNECT_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                // The first parameter to putString is the key. Here it is "logged_in_username"
                // The second parameter to putString is the value. Here, it's the first name and last name, fetched from the firestore's document, which
                // is then converted into an object of type User class defined by us.
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()
                // So at the end the Shared Preferences file named "FarmConnectPrefs" would contain :
                // Key:Value -----> logged_in_username : Ganesh G. (If a user named Ganesh G. has logged in)

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }

                    is SettingsActivity ->{
                        activity.userDetailsSuccess(user)
                    }
                }

            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }

                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Access the collection named 'Users' from our Firestore.
        mFireStore.collection(Constants.USERS)
            // The currentUser's ID would also be the ID of the document present inside the Users collection in Firestore.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            // Example of what userHashMap would contain :
            // mobile : 7627481234, gender : male
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is UserProfileActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {


        // The FirebaseStorage.getInstance().reference gets you a Storage reference to the root path of your Firebase Storage bucket.
        // For eg. The root path would be something like gs://your-app-id.appspot.com/
        // When you call .child(pathString) on a StorageReference, it appends the pathString to the current reference's path,
        // effectively giving you a reference to that location.
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            // We create a name of the image file, that we upload into the Firebase storage.
            // For eg. User_Profile_Image1713243701.jpg
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        //Add the file present locally at imageFileURI to location sRef on the Firebase Cloud storage of our Firebase project.
        sRef.putFile(imageFileURI!!)
            // In case file was uploaded successfully to the sRef location, a taskSnapshot is returned by the SuccessListener.
            .addOnSuccessListener { taskSnapshot ->
                // Image has been uploaded successfully
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // taskSnapshot.metadata!!.reference!!.downloadUrl helps to retrieve the URL of the successfully uploaded file.
                // metadata can include things like size, content type, and more importantly for this case, a reference to the file in storage.
                // reference is a property of metadata. It points directly to where the file is stored in Firebase Storage.
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    // uri would be a link to the image stored on the Firebase Cloud storage. Image can be downloaded using this link.
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // Check which activity is using current function.
                        when (activity) {
                            is UserProfileActivity -> {
                                // In case current function is being used by UserProfileActivity, call its function imageUploadSuccess and
                                // pass the url returned by Firebase in the form of String as a parameter to it.
                                // This function will then store the url locally in a variable called mUserProfileImageURL inside its class.
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }

            // Image file couldn't be uploaded to the sRef location.
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }

                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

}