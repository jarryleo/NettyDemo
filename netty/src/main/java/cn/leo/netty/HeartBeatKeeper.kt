package cn.leo.netty

import android.util.Log
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.timeout.IdleState

import io.netty.handler.timeout.IdleStateEvent


/**
 * @author : ling luo
 * @date : 2020-03-03
 * 心跳保持
 */

internal class HeartBeatKeeper(private val heartBeat: HeartBeatInterface?) :
    ChannelInboundHandlerAdapter() {

    override fun userEventTriggered(ctx: ChannelHandlerContext?, evt: Any?) {
        if (evt is IdleStateEvent) {
            val state = evt.state()
            if (state == IdleState.WRITER_IDLE) {
                Log.d("HeartBeatKeeper", "client send heart beat")
                ctx?.channel()
                    ?.writeAndFlush(
                        heartBeat?.let { heartBeat.toString() } ?: "heart beat")
            }
        } else {
            super.userEventTriggered(ctx, evt)
        }
    }
}