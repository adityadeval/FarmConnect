package ms.cs.farmconnect.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import ms.cs.farmconnect.R

class AddProductActivity : AppCompatActivity() {

    private lateinit var toolbar_add_product_activity : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        toolbar_add_product_activity  = findViewById(R.id.toolbar_add_product_activity )

        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_add_product_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }
    }

}