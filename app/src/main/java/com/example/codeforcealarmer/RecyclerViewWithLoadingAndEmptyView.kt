package com.example.codeforcealarmer

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewWithLoadingAndEmptyView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    var emptyView: View? = null
        set(value){
            field = value
            handleEmpty()
        }
    var emptyText: TextView? = null
        set(value){
            field = value
            handleEmpty()
        }

    var emptyIcon: ImageView? = null
        set(value){
            field = value
            handleEmpty()
        }

    var loadingView: View? = null
        set(value){
            field = value
            handleEmpty()
        }
    var loading: Boolean = false
        set(value) {
            field = value
            handleEmpty()
        }

    var isInternetConnection: Boolean = false
        set(value) {
            field = value
            handleEmpty()
        }

    private val observer = object : AdapterDataObserver(){
        override fun onChanged() {
            super.onChanged()
            handleEmpty()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            handleEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            handleEmpty()
        }
    }

    private fun handleEmpty() {
        if (loadingView != null && loading){
            loadingView?.visibility = View.VISIBLE
            emptyView?.visibility = View.GONE
            visibility = View.GONE
        }else {
            loadingView?.visibility = View.GONE
            if (emptyView != null && adapter?.itemCount == 0){
                emptyView?.visibility = View.VISIBLE
                if (isInternetConnection){
                    emptyText?.text = context.getString(R.string.empty_text)
                    emptyIcon?.setImageResource(android.R.drawable.ic_delete)
                }else{
                    emptyText?.text = context.getString(R.string.no_internet_text)
                    emptyIcon?.setImageResource(android.R.drawable.ic_menu_help)
                }

                visibility = View.GONE
            }else{
                emptyView?.visibility = View.GONE
                visibility = View.VISIBLE
            }
        }
    }

    override fun setAdapter(newAdapter: Adapter<*>?) {
        adapter?.unregisterAdapterDataObserver(observer)

        super.setAdapter(newAdapter)
        adapter?.registerAdapterDataObserver(observer)
    }
}