package ms.cs.farmconnect.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils
import ms.cs.farmconnect.R
import ms.cs.farmconnect.utils.Constants
import ms.cs.farmconnect.utils.CustomEditText
import ms.cs.farmconnect.utils.FCButton
import ms.cs.farmconnect.utils.GlideLoader
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {

    private lateinit var toolbar_add_product_activity : Toolbar
    private lateinit var iv_add_update_product : ImageView
    private lateinit var iv_product_image : ImageView
    private lateinit var et_product_title : CustomEditText
    private lateinit var et_product_price : CustomEditText
    private lateinit var et_product_description : CustomEditText
    private lateinit var et_product_quantity : CustomEditText
    private lateinit var btn_submit : FCButton

    private var mSelectedImageFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        toolbar_add_product_activity  = findViewById(R.id.toolbar_add_product_activity )
        iv_add_update_product = findViewById(R.id.iv_add_update_product)
        iv_product_image = findViewById(R.id.iv_product_image)
        et_product_title = findViewById(R.id.et_product_title)
        et_product_price = findViewById(R.id.et_product_price)
        et_product_description = findViewById(R.id.et_product_description)
        et_product_quantity = findViewById(R.id.et_product_quantity)
        btn_submit = findViewById(R.id.btn_submit)

        setupActionBar()

        // Set on Click listener for the little add_image icon that appears just beside the product's image.
        iv_add_update_product.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_add_update_product -> {
                    // Check if External storage permission has been granted by user already. If not, ask for it.
                    // We already ask for this permission during profile creation for the profile photo.
                    // But user might choose not to add profile image, in which case, the app won't have asked for the storage permissions.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Above if is true, which means app has storage permission.
                        // In this case call the ImageChooser function to allow user to select product image from the storage.
                        // This function in turn calls the StartActivityForResult(), which would allow the user to pick an image from the storage
                        // in the pop up dialog. Once done, Android will call the onActivityResult() method.
                        Constants.showImageChooser(this@AddProductActivity)
                    } else {
                        // Request storage permissions.
                        // Once they are granted or denied in the popup dialog, the result is handled by the
                        // onRequestPermissionsResult function.
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit -> {
                    if (validateProductDetails()) {
                        showCustomSnackBar("Product details are valid", false)
                    }
                }

            }
        }
    }

    // This function executes, once the popup dialog for requesting permissions from user is shown and
    // the user clicks on some option.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // If the result is for the read storage permission.
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted by the user.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                //Displaying toast if permission is not granted.
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    iv_add_update_product.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@AddProductActivity,
                            R.drawable.ic_vector_edit
                        )
                    )

                    // data parameter of onActivityResult will contain data returned by the popup screen for selecting image.
                    // data.data would be the uri of the locally stored image that the user selected.
                    // Storing this uri in SelectedImageFileUri
                    mSelectedImageFileUri = data.data!!

                    try {
                        // Load the product image in the ImageView.
                        GlideLoader(this@AddProductActivity).loadUserPicture(
                            mSelectedImageFileUri!!,
                            iv_product_image
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Product image selection cancelled")
        }

    }

    private fun validateProductDetails(): Boolean {
        return when {

            mSelectedImageFileUri == null -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                showCustomSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                showCustomSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_product_quantity.text.toString().trim { it <= ' ' }) -> {
                showCustomSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false
            }
            else -> {
                true
            }

        }
    }


}