package com.ftrd.flashlight.activity

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.ftrd.flashlight.FileKt.LogUtils
import java.util.*

/**
 * @author: Jeff <15899859876@qq.com>
 * @date:  2018-05-18 15:56
 * @description: 应用程序 Activity 管理类：用于 Activity 管理和应用程序退出
 */
object ActivityManager {

    /** 保存 Activity 对象的堆栈 */
    private val activityStack: Stack<AppCompatActivity> = Stack()

    /**
     * 添加 Activity 到堆栈
     *
     * @param activity Activity 对象
     */
    fun addActivity(activity: AppCompatActivity) {
        activityStack.add(activity)
        LogUtils.w("AppManager---->>", "add---->>$activity size---->>${activityStack.size}")
    }

    /**
     * 将 Activity 从堆栈移除
     *
     * @param activity Activity 对象
     */
    fun removeActivity(activity: AppCompatActivity) {
        if (activityStack.contains(activity)) {
            activityStack.remove(activity)
            LogUtils.w("AppManager---->>", "remove---->>$activity size---->>${activityStack.size}")
        }
    }

    /**
     * 结束指定 Activity
     *
     * @param clazz Activity 类对象
     */
    fun finishActivity(clazz: Class<out AppCompatActivity>) {
        val del: AppCompatActivity? = activityStack.lastOrNull { it.javaClass == clazz }
        del?.finish()
    }

    /**
     * 获取栈顶的 Activity
     *
     * @return 栈顶的 Activity 对象
     */
    fun peekActivity(): AppCompatActivity {
        return activityStack.peek()
    }

    /**
     * 根据类，获取 Activity 对象
     *
     * @param clazz  Activity 类
     * @param A      Activity 类型
     *
     * @return       Activity对象
     */
    fun <A : AppCompatActivity> getActivity(clazz: Class<out AppCompatActivity>): A? {
        var target: A? = null
        activityStack
                .filter { it.javaClass == clazz }
                .forEach {
                    @Suppress("UNCHECKED_CAST")
                    target = it as A
                }
        return target
    }

    /**
     * 结束所有 Activity
     */
    private fun finishAllActivity() {
        for (activity in activityStack) {
            activity.finish()
        }
        activityStack.clear()
        LogUtils.w("AppManager---->>", "Finish All Activity!")
    }


    /**
     * 退出应用程序
     */
    @SuppressLint("MissingPermission")
    fun appExit() {
        try {
            finishAllActivity()
            val activityMgr = peekActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityMgr.killBackgroundProcesses(peekActivity().packageName)
            System.exit(0)
        } catch (e: Exception) {
            LogUtils.e("AppManager---->>", "Application Exit!")
        }
    }
}
