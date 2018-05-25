package com.ftrd.flashlight.activity

import com.ftrd.flashlight.R
import com.ftrd.flashlight.nettys.buss.ExecuteBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author: Jeff <15899859876@qq.com>
 * @date:  2018-05-18 15:05
 * @description: 执行页面
 */
class ExecuteActivity :BaseActivity(){

    override fun info(){
        setContentView(R.layout.activity_execute)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(executeBus:ExecuteBus){}
}
