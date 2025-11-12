package edu.temple.countdownactivity

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.*

class CountdownService : Service() {
    companion object{
        const val TAG = "Countdown"
        const val EXTRA_SECONDS = "extra_seconds"
    }
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)
    private var currentCountdown : Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val seconds = intent?.getIntExtra(EXTRA_SECONDS, 0) ?: 0
        Log.i( "Countdown", "Start")
        if (seconds <= 0){
            Log.i("Countdown", "Stopped")
            stopSelf(startId)
            return START_NOT_STICKY


        }
        currentCountdown?.cancel()
        currentCountdown = serviceScope.launch {
            try {
                for (t in seconds downTo 0) {
                    Log.i(TAG, "Countdown")
                    delay(1000L)

                }
                Log.i("Countdown", "Countdown Complete")
            }catch (_: CancellationException){}
            finally {
                stopSelf(startId)
            }
        }
        return START_NOT_STICKY
    }



    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy(){
        currentCountdown?.cancel()
        serviceJob.cancel()
        Log.i("Countdown", "Service Destroyed")
        super.onDestroy()
    }

}