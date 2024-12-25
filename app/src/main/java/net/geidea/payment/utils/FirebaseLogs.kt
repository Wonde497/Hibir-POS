package net.geidea.payment.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import java.io.IOException
import androidx.databinding.adapters.NumberPickerBindingAdapter.setValue
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.childEvents
import com.pos.sdk.accessory.POIGeneralAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import net.geidea.utils.DateTimeFormat
import net.geidea.utils.getCurrentDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by Rasool Mohamed on 26-08-2024.
 */

object FirebaseDatabaseSingleton {

    private var time = "0"

    // Singleton instance of FirebaseDatabase
    val instance: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance().apply {
            setPersistenceEnabled(true) // Optional: Enable offline persistence
        }
    }

    // Method to get a DatabaseReference for a specific path
    private fun getDatabaseReference(): DatabaseReference {
        return instance.getReference("DSN")
    }

    fun getCurrentTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formattedDateTime = currentDateTime.format(DateTimeFormatter.ofPattern("hh:mm a")) // Format it with milliseconds
        return formattedDateTime
    }

    fun setLog(logText: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val serialNo = POIGeneralAPI.getDefault().getVersion(0x07)
            val date = getCurrentDateTime(DateTimeFormat.DATE_PATTERN_DISPLAY)
            val time = getCurrentTime()
            val childText = "$serialNo/$date/$time"
            getDatabaseReference().child(childText).push().setValue(logText)
        }
    }

}