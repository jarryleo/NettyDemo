package cn.leo.netty

/**
 * @author : ling luo
 * @date : 2020-03-02
 * 消息发送者
 */
interface Sender {
    /**
     * 发送文本消息
     */
    fun send(msg: String)

    /**
     * 发送字节消息
     */
    fun send(data: ByteArray)
}