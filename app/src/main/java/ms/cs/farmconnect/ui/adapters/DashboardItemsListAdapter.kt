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

    // Creating an instance of our own custom defined interface 'OnClickListener'
    private var m_custom_onClickListener: custom_OnClickListener? = null

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

            holder.itemView.setOnClickListener{
                if (m_custom_onClickListener != null) {
                    m_custom_onClickListener!!.custom_onClick(position, model)
                }
            }
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

    fun setOnClickListener(onClickListener: custom_OnClickListener) {
        this.m_custom_onClickListener = onClickListener
    }

    // By using an interface, the same adapter can be used in different contexts or activities, and each context
    // can provide its own implementation of the click behavior. This is particularly useful in large applications
    // where the same type of list might appear in multiple places but with slightly different behaviors on item click.
    // Handling click events Directly in onBindViewHolder() of the Dashboard fragment: Without an interface, if you want
    // to reuse the adapter in another place with different click behavior, you would need to modify the adapter or create
    // a new one, leading to code duplication.
    interface custom_OnClickListener {

        // Below function takes the position number and the Product that the user clicked on as arguments.
        fun custom_onClick(position: Int, product: Product)
    }

}