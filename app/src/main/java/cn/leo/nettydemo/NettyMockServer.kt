package cn.leo.nettydemo

import android.util.Log
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LineBasedFrameDecoder
import io.netty.handler.codec.string.LineEncoder
import io.netty.handler.codec.string.LineSeparator
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import io.netty.util.CharsetUtil

/**
 * @author : ling luo
 * @date : 2020-03-06
 */
class NettyMockServer {
    private val port = 12345

    fun startServer() {
        try {
            val bossGroup = NioEventLoopGroup()
            val workerGroup = NioEventLoopGroup()
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        ch?.pipeline()?.apply {
                            //Decoders 解码
                            addLast(LineBasedFrameDecoder(1024 * 1024 * 1024))
                            addLast(StringDecoder(CharsetUtil.UTF_8))
                            //Encoders 编码
                            addLast(StringEncoder(CharsetUtil.UTF_8))
                            addLast(LineEncoder(LineSeparator.UNIX, CharsetUtil.UTF_8))
                            //消息处理
                            addLast(ServerHandler())
                        }
                    }

                })
            b.bind(port).sync()
            Log.e("NettyMockServer", "TCP 服务器启动成功，port = $port")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    class ServerHandler : SimpleChannelInboundHandler<String>() {
        override fun channelRead0(ctx: ChannelHandlerContext?, msg: String?) {
            msg?.let {
                ctx?.channel()?.writeAndFlush(msg)
            }
        }
    }
}