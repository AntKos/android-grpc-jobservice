package sample.connectivitytest

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.RuntimeException

object RequestHelper {

    val TAG = "DEBUG"

    @SuppressLint("CheckResult")
    fun retryRequest(client: OkHttpClient, request: Request) : Completable {
        return Completable.fromAction {
            try {
                Log.d(TAG, "try execute HTTP request")
                val response = client.newCall(request).execute()
                Log.d(TAG, "HTTP response code = ${response.code()}")
            } catch (e: Exception) {
                Log.e(TAG, "error executing HTTP request $e")
                throw RuntimeException(e)
            }
        }
            .subscribeOn(Schedulers.io())
    }

    //FIXME: add
    @SuppressLint("CheckResult")
    fun retryGrpcRequest(stub: HealthServiceGrpc.HealthServiceBlockingStub) : Completable  {
        return Completable.fromAction {
            try {
                Log.d(TAG, "try execute GRPC request")
                val response = stub.echo(Health.HealthPacket.EchoRequest.getDefaultInstance())
                Log.d(TAG, "GRPC response = $response")
            } catch (e: Exception) {
                Log.e(TAG, "error executing request $e")
                throw RuntimeException(e)
            }
        }
            .subscribeOn(Schedulers.io())
    }
}