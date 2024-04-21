package ms.cs.farmconnect.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ms.cs.farmconnect.R
import ms.cs.farmconnect.models.Product
import ms.cs.farmconnect.utils.FCTextView
import ms.cs.farmconnect.utils.FCTextViewBold
import ms.cs.farmconnect.utils.GlideLoader

open class DashboardItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Variable model would get assigned an object of the Product class filled with values for one product.
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.iv_dashboard_item_image
            )
            holder.tv_dashboard_item_title.text = model.title
            holder.tv_dashboard_item_price.text = "$${model.price}"
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var iv_dashboard_item_image : ImageView = view.findViewById(R.id.iv_dashboard_item_image)
        var tv_dashboard_item_title : FCTextViewBold = view.findViewById(R.id.tv_dashboard_item_title)
        var tv_dashboard_item_price : FCTextView = view.findViewById(R.id.tv_dashboard_item_price)
    }
}