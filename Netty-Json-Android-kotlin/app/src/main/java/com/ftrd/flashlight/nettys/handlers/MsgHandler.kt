package com.ftrd.flashlight.nettys.handlers


import com.ftrd.flashlight.FileKt.FinalValue.MSG_HEAD
import com.ftrd.flashlight.FileKt.LogUtils
import com.ftrd.flashlight.FlashLight.Companion.eBus
import com.ftrd.flashlight.nettys.NettyConnect.nettyConnect
import com.ftrd.flashlight.nettys.NettyConnect.nettyDestroy
import com.ftrd.flashlight.nettys.buss.RegisterBus
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.TimeUnit


/**
 * @author: Jeff <15899859876@qq.com>
 * @date:  2018-05-16 16:56
 * @description: 接收到的数据处理
 *
 * ChannelDuplexHandler 则同时实现了 ChannelInboundHandler 和 ChannelOutboundHandler 接口。
 * 如果一个所需的ChannelHandler既要处理入站事件又要处理出站事件，推荐继承此类。
 *
 * 当然这里使用的是netty4
 * 如果使用netty5使用 ChannelHandlerAdapter 也同样实现 ChannelInboundHandler 和 ChannelOutboundHandler 接口
 */
class MsgHandler : ChannelHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext?) {
        super.channelActive(ctx)
//        if (ctx!!.channel()!!.isActive) {
//            ctx!!.close()
//
//        } else {
        //注册设备,服务器连接成功，注册通道，认证
        LogUtils.d("MsgHandler", "channelActive")
        //在这里只做发送注册的信息

        //在这里发送心跳
//        //0029,000004,,C3,170413 181858,10#心跳包设置
//        //  val time = Integer.parseInt(arr[5].replace("#", ""))
        ctx?.executor()?.scheduleAtFixedRate(
                {
                    ctx!!.writeAndFlush(RegisterBus());
                    LogUtils.d("MsgHandler", "channelActive里发送心跳${MSG_HEAD}---${TimeUnit.SECONDS}")
                },
                0,
                30,
                TimeUnit.SECONDS)

        //   }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        super.exceptionCaught(ctx, cause);
        LogUtils.d("MsgHandler", cause?.message.toString());
        if (ctx!!.channel().isOpen() && ctx!!.channel()!!.isActive) {
            nettyDestroy();//关闭服务器连接
            //ctx!!.close();
              nettyConnect();//连接服务器
        }

    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        LogUtils.d("MsgHandler", "channelRead=${msg!!.toString()}");


        // val value:String = String(msg as ByteArray);
        //MessagePack.unpack(MessagePack.pack(msg),UserInfo.class);
        return;
        val listMsg: List<Any> = (msg!! as List<Any>)

        LogUtils.d("MsgHandler", "channelRead=${listMsg!!.toString()}");
            when (listMsg.get(0) ) {
                "C0" -> {
                    // C0:[0048,000004,,C0,170413 181858,V1,170413 181618,0,1]设备注册成功返回值

                }
                "C120" -> {
                    //C120:[0054,000003,,C120,170414 110212,V120,170414 110212,1,0,ok] 登录返回值

                }
//            "C3"->{
//                //[0029,000004,,C3,170413 181858,10]心跳包设置
//                //  val time = Integer.parseInt(arr[5].replace("#", ""))
//                ctx?.executor()?.scheduleAtFixedRate(
//                        {
//                            ctx.writeAndFlush(MSG_HEAD)
//                            LogUtils.d("MsgHandler", "channelActive里发送心跳${MSG_HEAD}---${TimeUnit.SECONDS}")
//                        },
//                        0,
//                        10,
//                        TimeUnit.SECONDS)
//            }



        }
        super.channelRead(ctx, msg)
    }


}

