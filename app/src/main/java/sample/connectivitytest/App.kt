package sample.connectivitytest

import Api.Sibur.Siauth.Health.HealthServiceGrpc
import android.app.Application
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.android.AndroidChannelBuilder
import io.grpc.internal.DnsNameResolverProvider
import okhttp3.OkHttpClient
import okhttp3.Request

class App : Application() {

    companion object {
        private val TAG = "App"
        private val GRPC_HOST = ""  //TODO: insert host
        private val GRPC_PORT = 1234    //TODO: insert port
    }

    val client = OkHttpClient()
    val request = Request.Builder()
        .get()
        .url("http://www.google.com")
        .build()

    lateinit var channel: ManagedChannel
    lateinit var stub: HealthServiceGrpc.HealthServiceBlockingStub

    override fun onCreate() {
        super.onCreate()

        channel = AndroidChannelBuilder.forAddress(GRPC_HOST, GRPC_PORT)
            .usePlaintext()
            .disableRetry()
            .maxInboundMessageSize(1024 * 1024 * 1024)
            .context(this)
            .build()

        stub = HealthServiceGrpc.newBlockingStub(channel)

    }
}