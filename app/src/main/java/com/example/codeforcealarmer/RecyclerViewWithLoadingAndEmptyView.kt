package com.example.codeforcealarmer

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewWithLoadingAndEmptyView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    var emptyView: View? = null
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