package ms.cs.farmconnect.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import ms.cs.farmconnect.activities.RegisterActivity
import ms.cs.farmconnect.models.User

// This class will contain all operations performed in the Cloud Firestore.
class FirestoreClass {

    // Creating an instance of Firebase FireStore and storing it in mFireStore.
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // Checks if a collection called "users" exists in the FireStore instance mFireStore. Creates one if it doesn't exist.
        mFireStore.collection("users")
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

}