package cn.leo.netty

import androidx.lifecycle.LifecycleOwner

/**
 * @author : ling luo
 * @date : 2020-02-28
 */
interface NettyTcpClient {
    /**
     * 订阅接收指定消息,带生命周期绑定
     */
    fun subscribe(
        lifecycleOwner: LifecycleOwner,
        msgListener: MsgListener
    )

    /**
     * 订阅接收指定消息
     */
    fun subscribe(msgListener: MsgListener)

    /**
     * 取消订阅消息
     */
    fun unsubscribe(msgListener: MsgListener)

    /**
     * 订阅连接状态
     */
    fun subscribeState(connectStateListener: ConnectStateListener)

    /**
     * 取消订阅连接状态
     */
    fun unsubscribeState(connectStateListener: ConnectStateListener)

    /**
     * 获取发送者
     */
    fun getSender(): Sender

    /**
     * 关闭连接
     */
    fun close()
}