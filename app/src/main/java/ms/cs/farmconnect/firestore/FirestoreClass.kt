package ms.cs.farmconnect.firestore

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import ms.cs.farmconnect.activities.LoginActivity
import ms.cs.farmconnect.activities.RegisterActivity
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

}