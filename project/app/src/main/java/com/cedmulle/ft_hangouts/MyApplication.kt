package com.cedmulle.ft_hangouts

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyApplication : Application(), Application.ActivityLifecycleCallbacks {

    private var activityReferences = 0
    private var isActivityChangingConfigurations = false
    private var backgroundTime: Long = 0L

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityStarted(activity: Activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            if (backgroundTime != 0L) {
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val timeString = sdf.format(Date(backgroundTime))
                val message = getString(R.string.toast_background_time, timeString)
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
                backgroundTime = 0L
            }
        }
    }

    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            backgroundTime = System.currentTimeMillis()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
