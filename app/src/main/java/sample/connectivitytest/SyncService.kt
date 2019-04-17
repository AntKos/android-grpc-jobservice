package sample.connectivitytest

import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import java.util.logging.Logger

@SuppressLint("CheckResult")
class SyncService : JobService() {

    companion object {

        fun schedule(context: Context) {
            logMessage("schedule")
            val serviceComponent = ComponentName(context, SyncService::class.java)
            val jobInfoBuilder = JobInfo.Builder(0, serviceComponent).apply {
                setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                setBackoffCriteria((10 * 1000).toLong(), JobInfo.BACKOFF_POLICY_LINEAR)
            }
            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(jobInfoBuilder.build())
        }

        private fun logMessage(message: String, throwable: Throwable? = null) =
            Log.d("DEBUG - SyncService", message, throwable)

    }


    private val disposables = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()
        logMessage("onCreate")
    }

    override fun startService(service: Intent?): ComponentName? {
        logMessage("startService")
        return super.startService(service)
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        logMessage("onStartJob, thread = ${Thread.currentThread()}")

        with(applicationContext as App) {
            disposables.add(
                Observable.mergeDelayError(
                    RequestHelper.retryRequest(client, request).toObservable<Int>(),
                    RequestHelper.retryGrpcRequest(stub).toObservable<Int>()
                )
                    .doOnComplete { jobFinished(params, false) }
                    .doOnError { jobFinished(params, true) }
                    .subscribe({}, {})
            )

            //can use following lines to test gRPC request only
            /*
             disposables.add(
                RequestHelper.retryGrpcRequest(stub)
                    .doOnComplete { jobFinished(params, false) }
                    .doOnError { jobFinished(params, true) }
                    .subscribe({}, {})
            )
            */
        }

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        logMessage("onStopJob")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        logMessage("onDestroy")
    }
}