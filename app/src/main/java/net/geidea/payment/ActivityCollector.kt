package net.geidea.payment

import android.app.Activity
import android.util.Log

class ActivityCollector {
     companion object ActivityCollector {
        private val activities = mutableListOf<Activity>()
//test commit
        fun addActivity(activity: Activity) {
            activities.add(activity)
        }

        fun removeActivity(activity: Activity) {
            activities.remove(activity)
        }

        fun finishAll() {
            for (activity in activities) {
                if (!activity.isFinishing) {
                    activity.finish()
                }
            }
            activities.clear()
        }

    }

}