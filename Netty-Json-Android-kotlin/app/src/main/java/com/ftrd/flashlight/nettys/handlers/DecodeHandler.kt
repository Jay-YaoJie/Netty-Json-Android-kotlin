package com.ftrd.flashlight.nettys.handlers

import com.ftrd.flashlight.FileKt.LogUtils
import com.ftrd.flashlight.FlashLight.Companion.gson
import com.google.gson.Gson
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder


/**
 * @author: Jeff <15899859876@qq.com>
 * @date:  2018-05-16 15:58
 * @description: 接收器，解码
 *
 * DecodeHandler 继承自 Netty 中的 MessageToMessageDecoder 类，
 * 并重写抽象方法 decode(ctx: ChannelHandlerContext?, msg: ByteBuf?, out: MutableList<Any>?)
 * 首先从数据报msg（数据类型取决于继承MessageToMessageDecoder时填写的泛型类型）
 *它负责将Object类型的json 对象编码为 POJO 对象
 */
class DecodeHandler : MessageToMessageDecoder<Any?>() {
    override fun decode(ctx: ChannelHandlerContext?, msg: Any?, out: MutableList<Any>?) {
        LogUtils.d("DecodeHandler", "msg=" + msg.toString())
        var str: String = msg.toString()
        str = str.substring(0, str.lastIndexOf("}") + 1);
        LogUtils.d("DecodeHandler", "str=" + str)
        val jsonToAny = gson.fromJson(str, Any::class.java)
//        // 解码并添加到解码列表out中
        out!!.add(jsonToAny);
        LogUtils.d("DecodeHandler", "jsonToAny=" + jsonToAny.toString())

    }

}
