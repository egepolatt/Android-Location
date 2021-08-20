package com.egepolat.location

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import java.util.*

class FupsAdapter(items: ArrayList<BaseContract>): android.widget.BaseAdapter() {

    private var mItems: ArrayList<BaseContract> = items

    fun getItems(): ArrayList<BaseContract> {
        return this.mItems
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val contract = this.mItems[position]
        val layoutInflater = parent!!.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val bind: ViewDataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.spinner_item, parent, false)
        bind.setVariable(BR.contract, contract)
        bind.executePendingBindings()
        return bind.root
    }

    override fun getCount(): Int {
        return this.mItems.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any {
        return this.mItems[position]
    }

    fun insertItem(dto: BaseContract) {
        this.mItems.add(dto)
        super.notifyDataSetChanged()
    }

    fun insertItem(dto: BaseContract, position: Int) {
        this.mItems.add(position, dto)
        super.notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        this.mItems.remove(this.mItems[position])
        super.notifyDataSetChanged()
    }
}