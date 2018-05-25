# Netty-Json-Android-kotlin

使用纯kotlin创建的Android项目，并使用在真实项目中的。连接服务器使用的是neetty，这只是一个框架，有很多注释 
在使用neetty和Json对象传输
连接服务器 NettyConnect
编码EncodeHandler  解码DecodeHandler MsgHandler 接收到的数据处理


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




/**
 * @author: Jeff <15899859876@qq.com>
 * @date:  2018-05-021 16:54
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





