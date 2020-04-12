package com.wyj.mvplayter.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wyj.mvplayter.model.HomeItemEntity
import com.wyj.mvplayter.widget.HomeItemView
import java.util.ArrayList

class HomeAdapter: RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    var mDataList = ArrayList<HomeItemEntity>()

    fun reloadData(list:List<HomeItemEntity>) {
        mDataList.clear()
        mDataList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(HomeItemView(parent.context))
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
    }

    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}