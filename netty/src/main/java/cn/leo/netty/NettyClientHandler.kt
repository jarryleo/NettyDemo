package cn.leo.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * @author : ling luo
 * @date : 2020-02-28
 * 连接处理句柄
 */
internal class NettyClientHandler : SimpleChannelInboundHandler<String>() {

    //频道上下文
    private var channelHandlerContext: ChannelHandlerContext? = null


    var msgCallback: (msg: String) -> Unit = {}

    /**
     * 连接成功后收到的回调
     */
    override fun channelActive(ctx: ChannelHandlerContext?) {
        super.channelActive(ctx)
        channelHandlerContext = ctx
    }

    /**
     * 断开连接收到回调
     */
    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        channelHandlerContext = null
    }

    /**
     * 发送消息
     */
    fun sendMsg(msg: String) {
        channelHandlerContext?.channel()?.writeAndFlush(msg)
    }

    /**
     * 发送字节消息
     */
    fun sendData(data: ByteArray) {
        channelHandlerContext?.channel()?.writeAndFlush(data)
    }

    /**
     * 接收消息
     */
    override fun channelRead0(ctx: ChannelHandlerContext?, msg: String?) {
        msg?.let {
            msgCallback(msg)
        }
    }
}