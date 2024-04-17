package ms.cs.farmconnect.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import ms.cs.farmconnect.R
import ms.cs.farmconnect.utils.Constants

class MainActivity : AppCompatActivity() {
    private lateinit var tv_main : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_main = findViewById(R.id.tv_main)

        // First parameter of getSharedPreferences is name of the preferences file.
        // Second parameter specifies the operating mode of the preferences file.
        // Context.MODE_PRIVATE indicates that the file can only be accessed by the calling
        // application (or all applications sharing the same user ID, if you're sharing data across
        // applications that you own).
        val sharedPreferences =
            getSharedPreferences(Constants.FARMCONNECT_PREFERENCES, Context.MODE_PRIVATE)

        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        tv_main.text = "Hello $username"
    }
}