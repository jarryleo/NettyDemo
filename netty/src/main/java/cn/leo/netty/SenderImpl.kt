package cn.leo.netty

/**
 * @author : ling luo
 * @date : 2020-03-02
 */

internal class SenderImpl(private val handler: NettyClientHandler) : Sender {

    override fun send(msg: String) {
        handler.sendMsg(msg)
    }

    override fun send(data: ByteArray) {
        handler.sendData(data)
    }
}