package cn.leo.netty

/**
 * @author : ling luo
 * @date : 2020-02-28
 */
interface ConnectStateListener {
    /**
     * 连接状态变化
     */
    fun onStateChange(state: ConnectState)
}