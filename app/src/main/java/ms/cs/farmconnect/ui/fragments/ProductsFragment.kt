package ms.cs.farmconnect.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ms.cs.farmconnect.R
//import androidx.lifecycle.ViewModelProvider
import ms.cs.farmconnect.databinding.FragmentProductsBinding
import ms.cs.farmconnect.firestore.FirestoreClass
import ms.cs.farmconnect.models.Product
import ms.cs.farmconnect.ui.activities.AddProductActivity
import ms.cs.farmconnect.ui.adapters.MyProductsListAdapter

class ProductsFragment : BaseFragment() {

    private var _binding: FragmentProductsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

         /*
             Old version :
             val root = inflater.inflate(R.layout.fragment_products, container, false)
             New version :
          */
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

         /*
             Old version :
             val textView: TextView = root.findViewById(R.id.text_home)
             New version :
             val textView: TextView = binding.textHome
          */

        /*  Common line for both versions
            textView.text = "This is Products fragment"
         */

         /* In both versions the layout xml file contained a TextView named text_home
         <TextView
                android:id="@+id/text_home"
          */


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Continue with any initialization that requires the views, such as setting up adapters or listeners
    }

    override fun onResume() {
        super.onResume()

        getProductListFromFireStore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_add_product) {

            startActivity(Intent(activity, AddProductActivity::class.java))

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        // Hide Progress dialog.
        hideProgressDialog()

        // Here although in xml, we have field names as rv_my_product_items and tv_no_products_found,
        // we need to use rvMyProductItems and tvNoProductsFound instead.
        // In Kotlin, when using view binding with XML layouts, the generated binding class converts XML IDs into camel case properties.
        // his conversion is based on standard naming conventions used in Kotlin and Java, where XML attributes and IDs typically use snake case (like rv_my_product_items),
        // and these are transformed into camel case (like rvMyProductItems) for the properties in the binding class.

        if (productsList.size > 0) {
            binding.rvMyProductItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE

            // The LayoutManager assigned to the RecyclerView plays a crucial role in organizing the layout of the view holders
            // produced by the adapter. It dictates not only the arrangement but also the behavior of scrolling and recycling of the views.
            // Essentially, this line tells the RecyclerView to arrange its items in a linear vertical list (the default orientation for
            // LinearLayoutManager is vertical).
            binding.rvMyProductItems.layoutManager = LinearLayoutManager(activity)
            // Below line notifies the RecyclerView that its size is not affected by the adapter contents.
            // So the recycler view doesn't keep recalculating its size every time the content of its adapter changes.
            binding.rvMyProductItems.setHasFixedSize(true)

            // requireActivity() returns the activity associated with the fragment.
            val adapterProducts =
                MyProductsListAdapter(requireActivity(), productsList, this)
            binding.rvMyProductItems.adapter = adapterProducts
        } else {
            binding.rvMyProductItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }
    }

    // When this method is called :
    // It calls FirestoreClass.getProductsList() --> It fetches all relevant products data from Firestore
    // --> Then calls ProductsFragment.successProductsListFromFireStore() --> This binds the MyProductsListAdapter to
    // the recycler view present inside the ProductsFragment --> The adapter then displays all the products
    private fun getProductListFromFireStore() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getProductsList(this@ProductsFragment)
    }

    // Below function would be called from the onBindViewHolder() method of the MyProductsListAdapter,
    // when the user clicks on the dustbin icon for any product present inside the Products fragment.
    fun deleteProduct(productID: String) {

        showAlertDialogToDeleteProduct(productID)
    }

    fun productDeleteSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        // Below function will fetch the updated product data from Firestore and also display
        // the products again inside the ProductsFragment. It's equivalent to doing a screen 'Refresh'
        // once a product is deleted.
        getProductListFromFireStore()
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            showProgressDialog(resources.getString(R.string.please_wait))

            // Call the function of Firestore class.
            FirestoreClass().deleteProduct(this@ProductsFragment, productID)

            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Below line doesn't allow user to close the dialog. User can only click on Yes or No.
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}