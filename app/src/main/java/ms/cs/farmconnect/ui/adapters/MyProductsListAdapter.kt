package ms.cs.farmconnect.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ms.cs.farmconnect.R
import ms.cs.farmconnect.models.Product
import ms.cs.farmconnect.ui.activities.ProductDetailsActivity
import ms.cs.farmconnect.ui.fragments.ProductsFragment
import ms.cs.farmconnect.utils.FCTextView
import ms.cs.farmconnect.utils.FCTextViewBold
import ms.cs.farmconnect.utils.GlideLoader

open class MyProductsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>,
    private val fragment: ProductsFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // onCreateViewHolder is called when RecyclerView needs a new ViewHolder to represent an item.
    // This function creates every single list item (each item displays data for one product).
    // It uses the item_list_layout.xml as the view for every list item in the recycler view.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    // Below function tells the adapter how many list items it's going to hold.
    // (In current case, each list item displays one product's info).
    override fun getItemCount(): Int {
        return list.size
    }

    // This function takes care of all the individual components inside one list item.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        // Although our adapter only creates instances of MyViewHolder, it is capable of managing
        // different types of data that are displayed with different layouts (requiring different ViewHolder classes).
        // In such cases, the below if is helpful to ensure the correct binding logic is applied based on the holder's type.
        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(model.image, holder.iv_item_image)

            holder.tv_item_name.text = model.title
            holder.tv_item_price.text = "$${model.price}"
            holder.ib_delete_product.setOnClickListener {
                fragment.deleteProduct(model.product_id)
            }

            holder.itemView.setOnClickListener{
                val intent = Intent(context, ProductDetailsActivity::class.java)
                context.startActivity(intent)
            }

        }
    }


    // Below class is used for holding references to the actual views present for one holder object's layout
    // or in simple words, views present inside one list item.
    // So as we scroll through the list, the same views can be reused for new upcoming data.
    // This helps to reduce the number of findViewById calls.
    // As an example, consider our recycler view is configured to show 5 products data at a time.
    // For each product there are three fields iv_item_image, tv_item_name, tv_item_price.
    // So, irrespective of how many products the user scrolls through, the total number of calls to findViewById
    // would be just 3*5 = 15. In other words only 15 views would be used in total.
    // New data will be bound to them as they are recycled, but you won't create more ViewHolder instances or
    // call findViewById more than these initial 15 times.

    // An instance of this class is called in the onCreateViewHolder, which calls the constructor of this class, which
    // initializes all these variables with references to the views.
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var iv_item_image: ImageView = view.findViewById(R.id.iv_item_image)
        var tv_item_name: FCTextViewBold = view.findViewById(R.id.tv_item_name)
        var tv_item_price: FCTextView = view.findViewById(R.id.tv_item_price)
        var ib_delete_product : ImageButton = view.findViewById(R.id.ib_delete_product)
    }

}

// Imp about Recycler Views :
// As an example, consider our recycler view is configured to show 5 products data at a time.
// For each product there are three fields iv_item_image, tv_item_name, tv_item_price.
// As mentioned above, irrespective of how many products the user scrolls through, the total number of calls to findViewById
// would be just 3*5 = 15. In other words only 15 views would be used in total.

// Another imp point here The onCreateViewHolder() method would be called enough times to fill the screen with items,
// plus a few extras to allow for smooth scrolling. So in the current example, onCreateViewHolder() might be called for, say, 7 or 8 items initially.
// This is to have ready-to-use views that can enter the screen as others leave, creating an efficient recycling flow.
// Once these initial ViewHolders are created, as you scroll through your list (even if there are hundreds or thousands of items) RecyclerView recycles
// the ViewHolders that have scrolled off-screen by calling onBindViewHolder() to update them with new data corresponding to the new position in the list.