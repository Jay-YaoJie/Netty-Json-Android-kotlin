package com.ftrd.flashlight.activity.listadapter

import android.widget.TextView
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter
import com.ftrd.flashlight.R
import kotlinx.android.synthetic.main.activity_item_custom.view.*


/**
 * @author: Jeff <15899859876@qq.com>
 * @date:  2018-05-18 11:04
 * @description: 列表适配器
 */
class CustomAdapter(context: Context) : BaseAdapter() {
    var mInflater: LayoutInflater;
    var mContext: Context? = null;

    companion object {
        var data: List<Any>? = null;
        //设置选中项
        var selectItem: Int = 0;
    }

    init {
        mContext = context
        mInflater = LayoutInflater.from(mContext)

    }

    override fun getItem(int: Int): Any {
        return int
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data!!.size
    }

    override fun getView(position: Int, convertView: View?,
                         parent: android.view.ViewGroup): View {
        var convertView = convertView
        var viewHolder: ViewHolder
        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = mInflater.inflate(R.layout.activity_item_custom, null)
            viewHolder.tvItemMenu = convertView!!.fragmentItemCustomName;
            viewHolder.tvSpinner = convertView!!.fragmentItemCustomAge;

            convertView!!.setTag(viewHolder)
        } else {
            viewHolder = convertView!!.getTag() as ViewHolder
        }
        //设置值，
        viewHolder.tvItemMenu!!.text = data!![position] as String
        if (position == selectItem) {//设置当前选中值
            convertView.setBackgroundColor(Color.RED);
        } else {//没有选中的值
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView
    }

    class ViewHolder {
        var tvItemMenu: TextView? = null;
        var tvSpinner: TextView? = null;
    }
}