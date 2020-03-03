package cn.leo.netty

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LineBasedFrameDecoder
import io.netty.handler.codec.string.LineEncoder
import io.netty.handler.codec.string.LineSeparator
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.handler.timeout.IdleStateHandler
import io.netty.util.CharsetUtil
import java.util.concurrent.TimeUnit

/**
 * @author : ling luo
 * @date : 2020-02-28
 * netty 连接核心类
 */

internal class NettyTcpClientCore(
    private val host: String,
    private val port: Int,
    configEntity: TcpConfigEntity,
    nettyClientHandler: NettyClientHandler
) {
    private val group by lazy {
        NioEventLoopGroup()
    }

    private val boostStrap by lazy {
        Bootstrap()
    }

    val connectionWatchDog by lazy {
        ConnectionWatchDog(
            host,
            port,
            boostStrap,
            configEntity.reconnectWaitSecond
        )
    }

    init {
        boostStrap
            .group(group)
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, configEntity.connectTimeoutMillis)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel?) {
                    ch?.pipeline()?.apply {
                        //自动重连
                        addLast(connectionWatchDog)
                        //设置 读/写/所有 空闲超时时间
                        addLast(
                            IdleStateHandler(
                                configEntity.reconnectWaitSecond,
                                configEntity.reconnectWaitSecond,
                                configEntity.reconnectWaitSecond,
                                TimeUnit.SECONDS
                            )
                        )
                        //心跳处理
                        addLast(HeartBeatKeeper(configEntity.heartBeatData))
                        //Decoders 解码
                        addLast(LineBasedFrameDecoder(1024 * 1024 * 1024))
                        addLast(StringDecoder(CharsetUtil.UTF_8))
                        //Encoders 编码
                        addLast(StringEncoder(CharsetUtil.UTF_8))
                        addLast(LineEncoder(LineSeparator.UNIX, CharsetUtil.UTF_8))
                        //消息处理
                        addLast(nettyClientHandler)
                    }
                }
            })
    }

    /**
     * 连接服务器
     */
    fun connect() {
        connectionWatchDog.connect()
    }

    /**
     * 断开连接
     */
    fun close() {
        connectionWatchDog.disconnect()
    }

    /**
     * 是否连接
     */
    fun isOpen() = connectionWatchDog.isOpen()

}