package net.geidea.payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.geidea.payment.databinding.ActivityRegisterCommunicationModeBinding

class RegisterCommunicationMode : AppCompatActivity() {
    private lateinit var binding:ActivityRegisterCommunicationModeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterCommunicationModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
        //3 listeners for 4G network
        binding.textView1.setOnClickListener {
            editor.putBoolean("4G Data",true)
            editor.commit()
            val intent= Intent(this@RegisterCommunicationMode,MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.imageButton1.setOnClickListener {
            editor.putBoolean("4G Data",true)
            editor.commit()
            val intent= Intent(this@RegisterCommunicationMode,MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.card4G.setOnClickListener {
            editor.putBoolean("4G Data",true)
            editor.commit()
            val intent= Intent(this@RegisterCommunicationMode,MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.textView2.setOnClickListener {
            editor.putBoolean("WiFi",true)
            editor.commit()
            val intent= Intent(this@RegisterCommunicationMode,MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.imageButton2.setOnClickListener {
            editor.putBoolean("WiFi",true)
            editor.commit()
            val intent= Intent(this@RegisterCommunicationMode,MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.cardWiFi.setOnClickListener {
            editor.putBoolean("WiFi",true)
            editor.commit()
            val intent= Intent(this@RegisterCommunicationMode,MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,MainMenuActivity::class.java))
    }
}