package com.ftrd.flashlight.nettys.buss

import com.ftrd.flashlight.FileKt.DefaultValue

/**
 * @author: Jeff <15899859876@qq.com>
 * @date:  2018-05-17 11:26
 * @description: 设备注册对象，在连接服务器时第一个通信的数据
 */
class RegisterBus{
    //注册返回状态
    var registertResult: String? = "注册设备"
    //登录返回状态
    var loginResult: String? = "手电筒"
    //登录返回的昵称
    var nickName: String? = "工人使用的"
    //本来是使用刷卡登录的
    //登录用户名
    var lgoinName: String? = DefaultValue.USER_PWD;
    //登录密码
    var lgoinPwd: String? =  DefaultValue.USER_PWD;

    override fun toString(): String {
        return "RegisterBus(registertResult=$registertResult, " +
                "loginResult=$loginResult," +
                " nickName=$nickName, " +
                "lgoinName=$lgoinName, " +
                "lgoinPwd=$lgoinPwd)"
    }


}