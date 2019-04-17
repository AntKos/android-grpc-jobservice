package sample.connectivitytest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = "MainActivity"
    }

    val handler = Handler()

    private inner class NetworkReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val conn = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = conn.activeNetworkInfo
            val isConnected = networkInfo != null && networkInfo.isConnected
            Log.d(TAG, "Internet connection active ? $isConnected")
            if (!isConnected) {
                SyncService.schedule(context)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //channel state monitoring
        handler.post(object : Runnable {
            override fun run() {
                val channel = (applicationContext as App).channel
                val state = channel.getState(false)
                Log.d("CHANNEL_STATE", state.name)
                handler.postDelayed(this, 3000)
            }
        })

        registerReceiver(NetworkReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

}
