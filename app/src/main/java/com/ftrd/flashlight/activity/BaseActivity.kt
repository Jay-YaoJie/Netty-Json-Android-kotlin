package com.ftrd.flashlight.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.ftrd.flashlight.FileKt.LogUtils
import com.ftrd.flashlight.FlashLight

/**
 * @author: Jeff <15899859876@qq.com>
 * @date:  2018-05-18 15:08
 * @description: 全局Avtivity页面 主要写事件（按键）处理
 */
open class BaseActivity : AppCompatActivity() {
    var tag: String? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        info();
        // 添加到 AppManager 应用管理
        ActivityManager.addActivity(this)
        tag = this.localClassName;
    }

    //主要是初始化页面
    protected open fun info() {}

    /**
     * 重写 onDestroy() 方法，移除 Activity 管理以及 MVP 生命周期管理
     */
    override fun onDestroy() {
        if (FlashLight.eBus.isRegistered(this)) {
            //如果有注册eventbus则在结束当前页面时关闭
            FlashLight.eBus.unregister(this);
        }
        // 从应用管理移除当前 Activity 对象
        ActivityManager.removeActivity(this);
        super.onDestroy()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        LogUtils.d("com.ftrd.flashlight.activity.BaseActivity", "点击按钮=" + event!!.keyCode);
        //重写dispatchKeyEvent方法 按返回键back 执行两次的解决方法
        if (event!!.action != KeyEvent.ACTION_UP) { //不响应按键抬起时的动作
            ////在 when 中，else 同 switch 的 default。如果其他分支都不满足条件将会求值 else 分支。
            when (event!!.keyCode) {
            //  //KEYCODE_DPAD_LEFT 左键按钮 21
                21 -> {
                    leftArrow();
                    return true;
                };
            //KEYCODE_DPAD_RIGHT 右键按钮 22
                22 -> {
                    rightArrow();
                    return true;
                };
            //KEYCODE_ENTER  /确认按钮 66
                66 -> {
                    keypadEnter();
                    return true;
                };
            //KEYCODE_MENU  /返回按钮（Menu）82
                82 -> {
                    backspace();
                    return true;
                };
                else -> { // else 同 switch 的 default
                    LogUtils.e("com.ftrd.flashlight.activity.BaseActivity", "点击了其他按钮=" + event!!.keyCode);
                    //dispatchKeyEvent是做分发的工作，如果你想要onKeyDown还可以接收到应该实现返回
                    return super.dispatchKeyEvent(event);
                }
            }
        }
        //dispatchKeyEvent是做分发的工作，如果你想要onKeyDown还可以接收到应该实现返回
        return super.dispatchKeyEvent(event);
    }

    //  Home  Home键
    //KeypadEnter 小键盘“Enter” 确定
    protected open fun keypadEnter() {}
    // Backspace     退格键 返回
    protected open fun backspace() {}
//    UpArrow  方向键上
//    DownArrow 方向键下
//    RightArrow 方向键右
    protected open fun rightArrow() {}
    //    LeftArrow 方向键左
    protected open fun leftArrow() {}
}
