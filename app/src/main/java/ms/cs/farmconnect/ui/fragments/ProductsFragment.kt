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
import androidx.fragment.app.Fragment
import ms.cs.farmconnect.R
//import androidx.lifecycle.ViewModelProvider
import ms.cs.farmconnect.databinding.FragmentProductsBinding
import ms.cs.farmconnect.firestore.FirestoreClass
import ms.cs.farmconnect.models.Product
import ms.cs.farmconnect.ui.activities.AddProductActivity

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

        for (i in productsList){
            Log.i("Product Name", i.title)
        }
    }

    private fun getProductListFromFireStore() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getProductsList(this@ProductsFragment)
    }

}