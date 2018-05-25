package com.goav.netty.Handler

import com.ftrd.flashlight.FileKt.LogUtils
import com.ftrd.flashlight.FlashLight.Companion.gson
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder


/**
 *
 *@author: Jeff <15899859876@qq.com>
 * @time: 2018-05-16 15:10
 * @description: 发送器，编码
 * EncodeHandler 继承自 Netty 中的 MessageToMessageEncoder 类，
 * 并重写抽象方法 encode(ctx: ChannelHandlerContext?, msg: Any?, out: MutableList<Any>?)
 * 它负责将Object类型的POJO对象编码为json对象
 *
 */

internal class EncodeHandler : MessageToMessageEncoder<Any>() {
    override fun encode(ctx: ChannelHandlerContext?, msg: Any?, out: MutableList<Any>?) {
        LogUtils.d("EncodeHandler", "msg="+msg.toString());
        //序列化为json字符串
       val objectToJson:String=gson.toJson(msg);
        out!!.add(objectToJson);
        LogUtils.d("EncodeHandler", "objectToJson="+objectToJson);


    }


}
