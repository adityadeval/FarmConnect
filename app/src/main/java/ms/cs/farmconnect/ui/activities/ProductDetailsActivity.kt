package ms.cs.farmconnect.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import ms.cs.farmconnect.R
import ms.cs.farmconnect.firestore.FirestoreClass
import ms.cs.farmconnect.models.Product
import ms.cs.farmconnect.utils.Constants
import ms.cs.farmconnect.utils.FCTextView
import ms.cs.farmconnect.utils.FCTextViewBold
import ms.cs.farmconnect.utils.GlideLoader

class ProductDetailsActivity : BaseActivity() {

    private lateinit var toolbar_product_details_activity : Toolbar
    private var mProductId: String = ""
    private lateinit var tv_product_details_title : FCTextViewBold
    private lateinit var iv_product_detail_image : ImageView
    private lateinit var tv_product_details_price : FCTextView
    private lateinit var tv_product_details_description : FCTextView
    private lateinit var tv_product_details_available_quantity : FCTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        toolbar_product_details_activity = findViewById(R.id.toolbar_product_details_activity)
        tv_product_details_title = findViewById(R.id.tv_product_details_title )
        iv_product_detail_image = findViewById(R.id.iv_product_detail_image)
        tv_product_details_price = findViewById(R.id.tv_product_details_price)
        tv_product_details_description = findViewById(R.id.tv_product_details_description)
        tv_product_details_available_quantity = findViewById(R.id.tv_product_details_available_quantity)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product Id", mProductId)
        }

        getProductDetails()
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductDetails() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the product details.
        FirestoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    // This function is responsible for actually displaying a product's details on the screen.
    // It would be called from inside the Firestore class's getProductDetails() method, when data of
    // a product would have been successfully retrieved from Firestore DB.
    fun productDetailsSuccess(product: Product) {

        // Hide Progress dialog.
        hideProgressDialog()

        // Populate the product details in the UI.
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            iv_product_detail_image
        )

        tv_product_details_title.text = product.title
        tv_product_details_price.text = "$${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_available_quantity.text = product.stock_quantity
    }
}