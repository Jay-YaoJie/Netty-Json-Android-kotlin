package com.ftrd.flashlight.nettys.buss

import com.ftrd.flashlight.FileKt.DefaultValue

/**
 * @author: Jeff <15899859876@qq.com>
 * @date:  2018-02-27 15:31
 * @description: 登录，eventbus使用此对象作为传输
 */
class LoginBus {
    //注册返回状态
    var registertResult: String? = "注册成功"
    //登录返回状态
    var loginResult: String? = "准备登录"
    //登录返回的昵称
    var nickName: String? = "陈静含"
    //本来是使用刷卡登录的
    //登录用户名
    var lgoinName: String? = DefaultValue.USER_PWD;
    //登录密码
    var lgoinPwd: String? =  DefaultValue.USER_PWD;

    override fun toString(): String {
        return "LoginBus(registertResult=$registertResult," +
                " loginResult=$loginResult, " +
                "nickName=$nickName," +
                " lgoinName=$lgoinName," +
                " lgoinPwd=$lgoinPwd)"
    }

}
