package cn.leo.netty

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.ConcurrentSkipListSet

/**
 * @author : ling luo
 * @date : 2020-02-28
 */
internal class NettyTcpClientImpl(
    private val host: String,
    private val port: Int,
    configEntity: TcpConfigEntity
) : NettyTcpClient, ConnectStateListener {
    private val msgListenerCache = ConcurrentSkipListSet<MsgListener>()
    private val stateListenerCache = ConcurrentSkipListSet<ConnectStateListener>()

    private val nettyClientHandler = NettyClientHandler()

    private val core: NettyTcpClientCore by lazy {
        NettyTcpClientCore(host, port, configEntity, nettyClientHandler)
    }

    private val msgSender: Sender by lazy {
        SenderImpl(nettyClientHandler)
    }

    init {
        core.connectionWatchDog.stateListener = this
        nettyClientHandler.msgCallback = { msg ->
            msgListenerCache.forEach { it.onMsgRecive(msg, host, port) }
        }
        core.connect()
    }

    /**
     * 订阅接收指定消息,带生命周期绑定
     */
    override fun subscribe(
        lifecycleOwner: LifecycleOwner,
        msgListener: MsgListener
    ) {
        subscribe(msgListener)
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                unsubscribe(msgListener)
            }
        })
    }

    /**
     * 订阅接收指定消息
     */
    override fun subscribe(msgListener: MsgListener) {
        msgListenerCache.add(msgListener)
        if (!core.isOpen()) {
            core.connect()
        }
    }

    /**
     * 取消订阅消息
     */
    override fun unsubscribe(msgListener: MsgListener) {
        msgListenerCache.remove(msgListener)
        if (msgListenerCache.isEmpty()) {
            close()
        }
    }

    /**
     * 订阅连接状态
     */
    override fun subscribeState(connectStateListener: ConnectStateListener) {
        stateListenerCache.add(connectStateListener)
    }

    /**
     * 取消订阅连接状态
     */
    override fun unsubscribeState(connectStateListener: ConnectStateListener) {
        stateListenerCache.remove(connectStateListener)
    }


    /**
     * 获取发送者
     */
    override fun getSender(): Sender {
        return msgSender
    }

    override fun close() {
        core.close()
    }

    /**
     * 连接状态变化
     */
    override fun onStateChange(state: ConnectState) {
        stateListenerCache.forEach {
            it.onStateChange(state)
        }
    }
}