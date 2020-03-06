package cn.leo.netty

import android.util.Log
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import kotlinx.coroutines.delay

/**
 * @author : ling luo
 * @date : 2020-03-03
 * 自动重连
 */
@ChannelHandler.Sharable
internal class ConnectionWatchDog(
    private val host: String,
    private val port: Int,
    private val boostStrap: Bootstrap,
    private val reconnectWaitSecond: Long
) : ChannelInboundHandlerAdapter() {

    var stateListener: ConnectStateListener? = null

    private var channel: Channel? = null
    /**
     * 连接成功
     */
    override fun channelActive(ctx: ChannelHandlerContext?) {
        channel = ctx?.channel()
        super.channelActive(ctx)
        stateListener?.onStateChange(ConnectState.Connected)
    }

    /**
     * 连接断开
     */
    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        channel = null
        stateListener?.onStateChange(ConnectState.Offline)
    }

    fun connect() {
        stateListener?.onStateChange(ConnectState.Connecting)
        boostStrap.connect(host, port).addListener {
            if (it.isSuccess) {
                //连接成功
                Log.d("connect", "连接成功: $host : $port")
            } else {
                //连接失败，重新连接
                scheduleReconnect()
            }
        }
    }

    fun disconnect() {
        if (channel?.isOpen == true) {
            channel?.close()
        }
    }

    fun isOpen() = channel?.isOpen ?: false

    /**
     * 重连
     */
    private fun scheduleReconnect() {
        io {
            delay(reconnectWaitSecond * 1000L)
            connect()
        }
    }

}