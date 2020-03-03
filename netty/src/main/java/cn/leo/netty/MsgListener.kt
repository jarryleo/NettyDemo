package cn.leo.netty

/**
 * @author : ling luo
 * @date : 2020-03-02
 * 消息接收回调
 */
interface MsgListener {

    /**
     * 收到服务端发送来的消息
     */
    fun onMsgRecive(msg: String, host: String, port: Int)
}