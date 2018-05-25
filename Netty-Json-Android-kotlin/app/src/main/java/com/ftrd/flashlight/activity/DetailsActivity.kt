package com.ftrd.flashlight.activity


import com.ftrd.flashlight.R
import com.ftrd.flashlight.nettys.buss.DetailsBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author: Jeff <15899859876@qq.com>
 * @date:  2018-05-18 15:00
 * @description: 列表详情页面
 */
class DetailsActivity : BaseActivity() {

    override fun info(){
        setContentView(R.layout.activity_etails)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(detailsBus:DetailsBus){

    }
}
