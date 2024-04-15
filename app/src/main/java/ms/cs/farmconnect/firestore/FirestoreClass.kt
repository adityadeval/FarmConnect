package ms.cs.farmconnect.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import ms.cs.farmconnect.activities.LoginActivity
import ms.cs.farmconnect.activities.RegisterActivity
import ms.cs.farmconnect.activities.UserProfileActivity
import ms.cs.farmconnect.models.User
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
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }
                }

            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
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

}