package com.ftrd.flashlight.nettys

import com.ftrd.flashlight.FileKt.DelegatesExt
import com.ftrd.flashlight.FileKt.FinalValue.COMMAND_IP
import com.ftrd.flashlight.FileKt.FinalValue.COMMAND_PORT
import com.ftrd.flashlight.FileKt.LogUtils;
import com.ftrd.flashlight.nettys.handlers.DecodeHandler
import com.ftrd.flashlight.nettys.handlers.MsgHandler
import com.goav.netty.Handler.EncodeHandler
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import io.netty.util.CharsetUtil
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.LengthFieldBasedFrameDecoder


/**
 * @author: Jeff <15899859876@qq.com>
 * @date:  2018-05-16 16:54
 * @description: netty连接类
 * 1.Netty Client启动的时候需要重连
 * 2.在程序运行中连接断掉需要重连。
 * 使用object关键字替代class关键字就可以声明一个单例对象
 */
object NettyConnect {
    //companion静态声类声名对象，相当于static关键
    //companion object { // 自定义委托实现单例,只能修改这个值一次.
    //如果要发送数据直接调用NettyConnect.channel?.writeAndFlush(""); 或着 导包直接使用channel.writeAndFlush("")
    var channel: Channel? by DelegatesExt.notNullSingleValue<Channel?>();
    /*    NioEventLoopGroup可以理解为一个线程池，内部维护了一组线程，
       每个线程负责处理多个Channel上的事件，而一个Channel只对应于一个线程，这样可以回避多线程下的数据同步问题。*/
    private var eventLoopGroup: EventLoopGroup? = null;//=NioEventLoopGroup()
    /* Bootstrap是开发netty客户端的基础,通过Bootstrap的connect方法来连接服务器端。该方法返回的也是ChannelFuture，
        通过这个我们可以判断客户端是否连接成功,*/
    private var bootstrap: Bootstrap? = null;
    //使用Thread线程进行异步连接
    private var mThread: Thread? = null;
    //ChannelFuture的作用是用来保存Channel异步操作的结果。
    private var future: ChannelFuture? = null;
    //保存连接成功或着失败
    private var onDestrYN: Boolean = false;
    //   }

    //创建连接方法,如果要调用连接，直接调用此方法即可( NettyConnect.reConnect() )
    fun nettyConnect() {

        mThread = object : Thread("NettyConnect.reConnect") {
            override fun run() {
                try {
                    // super.run();
                    eventLoopGroup = NioEventLoopGroup(2);
                    bootstrap = Bootstrap()
                    bootstrap!!
                            .group(eventLoopGroup)
                            .channel(NioSocketChannel::class.java)
                            //使用TCP进行连接
                            .option(ChannelOption.TCP_NODELAY, true)
                            //// 设置TCP连接超时时间 10秒
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000 * 10)//设置连接超时时间
                            .option(ChannelOption.SO_KEEPALIVE, true)
                            //.remoteAddress(host, port)
                            .handler(object : ChannelInitializer<SocketChannel>() {
                                @kotlin.jvm.Throws(java.lang.Exception::class)
                                override fun initChannel(ch: SocketChannel) {
                                    var pipeline: ChannelPipeline = ch.pipeline();
                                    /* 使用pipeline.addLast()添加，Decoder、Encode和Handler对象
                                      Decoder接收数据，Encode发送数据，Handler是编码和解码工具*/
                                    //解析编码为UTF-8
                                    pipeline.addLast(StringDecoder(CharsetUtil.UTF_8));
                                    //打包编码为UTF-8
                                    pipeline.addLast(StringEncoder(CharsetUtil.UTF_8));

                                    // 添加长度字段解码器
                                    // 在MessagePack解码器之前增加LengthFieldBasedFrameDecoder，用于处理半包消息
                                    // 它会解析消息头部的长度字段信息，这样后面的MsgpackDecoder接收到的永远是整包消息
                                    ch.pipeline().addLast("frameDecoder",
                                            LengthFieldBasedFrameDecoder(16384, 0,
                                                    2, 0, 2))
                                    // 添加MesspagePack解码器
                                    ch.pipeline().addLast("msgpack decoder", DecodeHandler());//接收服务器返回的数据包解析
                                    // 添加长度字段编码器
                                    // 在MessagePack编码器之前增加LengthFieldPrepender，它将在ByteBuf之前增加2个字节的消息长度字段
                                    ch.pipeline().addLast("frameEncoder", LengthFieldPrepender(2))
                                    // 添加MessagePack编码器
                                    ch.pipeline().addLast("msgpack encoder", EncodeHandler());//发送前打包数据并编码
                                    // 添加业务处理handler
                                    ch.pipeline().addLast(MsgHandler());//接收返回数据

                                }
                            })
                    LogUtils.d("NettyConnect",
                            "正在连接服务器 ipStr=${COMMAND_IP},portInt=${COMMAND_PORT}");
                    //ChannelFuture的作用是用来保存Channel异步操作的结果。
                    //不使用监听
                    // val future: ChannelFuture = bootstrap!!.connect(FinalValue.COMMAND_IP,FinalValue.COMMAND_PORT).sync();
                    //使用监听，监听是否连接或是断开
                    // val future: ChannelFuture = bootstrap!!.connect(FinalValue.COMMAND_IP,FinalValue.COMMAND_PORT);
                    //{addListener(GenericFutureListener)}的方式来获得通知，而非await()。使用sync异步执行
                    future = bootstrap!!.connect(COMMAND_IP, COMMAND_PORT)!!.sync();//// 发起异步连接操作
                    //如果连接成功则保存ChannelFuture到Channel
//                        channel = future!!.awaitUninterruptibly().channel();
                    if (future!!.isSuccess) {
                        //如果连接成功则保存ChannelFuture到Channel
                        channel = future!!.awaitUninterruptibly().channel();
                        channel!!.closeFuture().sync();// 等待客户端链路关闭
                        // 优雅退出，释放NIO线程组
                        eventLoopGroup!!.shutdownGracefully();
//                            //如果连接成功则保存ChannelFuture到Channel
//                            channel = future!!.channel() as Channel
                        LogUtils.d("NettyConnect", "服务器连接成功ipStr=${COMMAND_IP},portInt=${COMMAND_PORT}")
                        onDestrYN = true;//连接成功
                    } else {
                        onDestrYN = false;//连接失败
                        while (onDestrYN) {//连接失败一直进行连接，不管是什么原因都进行连接
                            LogUtils.d("NettyConnect", "连接失败再次连接")
                            //断开连接，重新进行连接
                            channel!!.disconnect();
                            channel!!.close();
                            future!!.channel().eventLoop().schedule(
                                    {
                                        //重新开新的线程进行连接
                                        if (future!!.isSuccess) {
                                            //如果连接成功则保存ChannelFuture到Channel
                                            channel = future!!.channel() as Channel;
                                            //channel = future!!.awaitUninterruptibly().channel()
                                            onDestrYN = true;//连接成功
                                        };
                                    }, 30,//2秒重新连接
                                    TimeUnit.SECONDS);
                        };
                    };
                } catch (ex: Exception) {
                    LogUtils.d("NettyConnect", "连接出现异常重新连接${ex.toString()}");
                    executor.execute {
                        try {
                            TimeUnit.SECONDS.sleep(20);
                            nettyDestroy();
                            nettyConnect();// 发起重连操作
                        } catch (e: InterruptedException) {
                            e.printStackTrace();
                        };
                    };
                };
            };
        };

        mThread!!.start();
    }

    private val executor = Executors.newScheduledThreadPool(1)
    //? 表示当前对象是否可以为空
    //！！ 表示当前对象不为空的情况下执行
    fun nettyDestroy() {
        // channel = null;
        //  Bootstrap
        bootstrap = null;
        //结束线程池
        if (eventLoopGroup != null) {
            // 优雅退出，释放NIO线程组
            eventLoopGroup!!.shutdownGracefully();
        }
        eventLoopGroup = null;
        //结束线程
        mThread!!.interrupt();
        mThread!!.join();
        mThread = null;
        //结束连接
        if (future != null && future!!.isSuccess) {
            /* 当对应的channel关闭的时候，就会返回对应的channel。
                       Returns the ChannelFuture which will be notified when this channel is closed.
                       This method always returns the same future instance.*/
            try {
                channel!!.closeFuture().sync();
                channel!!.flush();
            } catch (e: Exception) {
                e.printStackTrace()
            }
            channel!!.close();
        }

        //ChannelFuture,结束监听
        //future?.removeListener { }
        future = null;
    }
}
