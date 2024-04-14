package ms.cs.farmconnect.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ms.cs.farmconnect.R
import ms.cs.farmconnect.models.User
import ms.cs.farmconnect.utils.Constants
import ms.cs.farmconnect.utils.CustomEditText

class UserProfileActivity : AppCompatActivity() {

    private lateinit var et_first_name : CustomEditText
    private lateinit var et_last_name : CustomEditText
    private lateinit var et_email : CustomEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        et_first_name = findViewById(R.id.et_first_name)
        et_last_name = findViewById(R.id.et_last_name)
        et_email = findViewById(R.id.et_email)

        // Creating and initializing mutable variable userDetails of type User class, to an instance of User class.
        var userDetails: User = User()
        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {

            // getParcelableExtra() with just one String parameter is deprecated.
            // However, the new getParcelableExtra() which takes two arguments, first is a String which is the name of the Extra,
            // and second Class object representing the type of the Parcelable, requires API level 33 or higher.
            // So below if condition first checks the version of the device. If it's API 33 or up, it uses the new method,
            // else it uses the deprecated method.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS, User::class.java)!!
            } else {
                // Use the deprecated method or handle differently for older devices
                userDetails = intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)!!
            }
            // Get the user details from intent as a ParcelableExtra.
            //userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS, User::class.java) ?: User()
        }

        // Setting isEnabled to false will now freeze that EditText and the user would no longer be able to edit it.
        et_first_name.isEnabled = false
        et_first_name.setText(userDetails.firstName)

        et_last_name.isEnabled = false
        et_last_name.setText(userDetails.lastName)

        et_email.isEnabled = false
        et_email.setText(userDetails.email)
    }
}