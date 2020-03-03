package cn.leo.netty

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * @author : ling luo
 * @date : 2020-02-28
 */
class PushService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}