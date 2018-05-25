package com.ftrd.flashlight.activity


import com.ftrd.flashlight.FlashLight.Companion.eBus
import com.ftrd.flashlight.R
import com.ftrd.flashlight.activity.listadapter.CustomAdapter
import com.ftrd.flashlight.nettys.buss.ListBus
import kotlinx.android.synthetic.main.activity_list.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * @author: Jeff <15899859876@qq.com>
 * @date:  2018-05-18 11:11
 * @description: 列表 AppCompatActivity
 * */
class ListActivity : BaseActivity() {

    var listAdapter: CustomAdapter? = null;
    override fun info() {
        setContentView(R.layout.activity_list);
        eBus.register(this);
        listAdapter = CustomAdapter(this);
        fragmentLv.setAdapter(listAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(listBus:ListBus){

    }


    override fun leftArrow() {//点击左边按钮
        //当前已经选中的item值
        val iCurrItem = fragmentLv.getSelectedItemPosition();
        if (iCurrItem >0) {
            CustomAdapter.selectItem = (iCurrItem - 1);
        } else {//如果等于0 则又从 总大小开始
            CustomAdapter.selectItem = listAdapter!!.count - 1;//getCount
        }
        fragmentLv.setItemChecked(CustomAdapter.selectItem,true);
        fragmentLv.setSelection(CustomAdapter.selectItem);
        //检测到listAdapter数据有变化时，便更新整个ListView
        listAdapter!!.notifyDataSetChanged();
    }

    override fun rightArrow() {//点击右边按钮
        //当前已经选中的item值
        val iCurrItem = fragmentLv.getSelectedItemPosition();
        if (iCurrItem <listAdapter!!.count) {
            CustomAdapter.selectItem = (iCurrItem + 1);
        } else {//如果等于总大小则又从0开始
            CustomAdapter.selectItem = 0;
        }
        fragmentLv.setItemChecked(CustomAdapter.selectItem,true);
        fragmentLv.setSelection(CustomAdapter.selectItem);
        //检测到listAdapter数据有变化时，便更新整个ListView
        listAdapter!!.notifyDataSetChanged();
    }

}

