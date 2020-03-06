package cn.leo.nettydemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.leo.netty.ConnectState
import cn.leo.netty.ConnectStateListener
import cn.leo.netty.MsgListener
import cn.leo.netty.NettyManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val server by lazy {
        NettyMockServer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //客服端
        val client = NettyManager.getClient("127.0.0.1", 12345)
        //启动模拟服务器
        server.startServer()
        //订阅消息接收
        client.subscribe(this, object : MsgListener {
            override fun onMsgRecive(msg: String, host: String, port: Int) {
                tvMsg.text = msg
                println(msg)
            }
        })
        //订阅连接状态
        client.subscribeState(object : ConnectStateListener {
            override fun onStateChange(state: ConnectState) {
                when (state) {
                    ConnectState.Connecting -> {
                        Log.e("onStateChange", "正在连接")
                        btnSend.text = "正在连接"
                        btnSend.isEnabled = false
                    }
                    ConnectState.Connected -> {
                        Log.e("onStateChange", "连接成功")
                        btnSend.text = "发送"
                        btnSend.isEnabled = true
                    }
                    ConnectState.Offline -> {
                        Log.e("onStateChange", "断开连接")
                        btnSend.text = "断开连接"
                        btnSend.isEnabled = false
                    }
                }
            }
        })
        //发送消息
        val sender = client.getSender()
        btnSend.setOnClickListener {
            sender.send(etInput.text.toString())
        }
    }
}
