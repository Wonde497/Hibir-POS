import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import java.util.*

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        val lang = getLanguagePreference(newBase)
        val context = if (lang.isNotEmpty()) {
            setLocale(newBase, lang)
        } else {
            newBase
        }
        super.attachBaseContext(context)
    }

    private fun setLocale(context: Context?, lang: String): Context {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        return context?.createConfigurationContext(config) ?: context!!
    }

    private fun getLanguagePreference(context: Context?): String {
        val sharedPref = context?.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return sharedPref?.getString("language", "en") ?: "en" // Default to English
    }

    fun setLanguagePreference(language: String) {
        val sharedPref = getSharedPreferences("app_settings", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("language", language)
            apply()
        }
    }
}
