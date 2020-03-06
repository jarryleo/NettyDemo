package cn.leo.netty

import io.netty.util.internal.logging.InternalLoggerFactory
import io.netty.util.internal.logging.JdkLoggerFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * @author : ling luo
 * @date : 2020-03-02
 */
object NettyManager {
    private val clientCache = ConcurrentHashMap<String, NettyTcpClient>()

    init {
        InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE)
    }

    /**
     * 获取tcp连接
     */
    fun getClient(
        host: String,
        port: Int,
        config: TcpConfigEntity.() -> Unit = {}
    ): NettyTcpClient {
        val key = "$host:$port"
        var client = clientCache[key]
        if (client != null) {
            return client
        }
        val tcpConfigEntity = TcpConfigEntity()
        config(tcpConfigEntity)
        client = NettyTcpClientImpl(host, port, tcpConfigEntity)
        clientCache[key] = client
        return client
    }
}