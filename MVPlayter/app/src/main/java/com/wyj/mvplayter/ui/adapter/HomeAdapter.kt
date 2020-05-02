package com.wyj.mvplayter.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wyj.mvplayter.model.HomeItemEntity
import com.wyj.mvplayter.widget.HomeItemView
import java.util.ArrayList

class HomeAdapter: RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private var mDataList = ArrayList<HomeItemEntity>()

    fun reloadData(list:List<HomeItemEntity>) {
        mDataList.clear()
        mDataList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(HomeItemView(parent.context))
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        var itemEntity = mDataList.get(position)
        itemEntity.let {
            val itemView = holder.itemView as HomeItemView
            itemView.bindData(itemEntity)
        }
    }

    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}