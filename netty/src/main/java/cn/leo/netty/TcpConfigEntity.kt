package cn.leo.netty

/**
 * @author : ling luo
 * @date : 2020-03-02
 * 连接配置类
 *
 */
data class TcpConfigEntity(
    /**
     * 心跳间隔，单位秒
     */
    var heartbeatIntervalSecond: Long = 7,
    /**
     * 心跳数据对象
     */
    var heartBeatData: HeartBeatInterface? = null,
    /**
     * 重连间隔,单位秒
     */
    var reconnectWaitSecond: Long = 5,
    /**
     * 连接超时时间
     */
    var connectTimeoutMillis: Int = 5000


)