package ms.cs.farmconnect.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import ms.cs.farmconnect.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                finish()
            },
            3000
        )

        //val tvAppName: TextView = findViewById(R.id.tv_app_name)
        //val typeface: Typeface = Typeface.createFromAsset(assets, "Montserrat-Bold.ttf")
        //tvAppName.typeface = typeface
    }
}