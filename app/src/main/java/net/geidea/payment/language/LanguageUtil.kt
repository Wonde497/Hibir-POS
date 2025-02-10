import android.content.Context
import android.content.SharedPreferences
import java.util.*

object LanguageUtil {

    private const val PREFS_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "language_key"

    // Save selected language
    fun saveLocale(context: Context, languageCode: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    // Retrieve saved language
    fun getLocale(context: Context): Locale {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val languageCode = prefs.getString(KEY_LANGUAGE, Locale.getDefault().language) ?: "en"
        return Locale(languageCode)
    }

    // Apply locale
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
