package net.geidea.payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityRegisterCurrencyBinding

class RegisterCurrency : AppCompatActivity() {
    private lateinit var binding:ActivityRegisterCurrencyBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
        //3 listeners for ETB
        binding.textView1.setOnClickListener {
            editor.putString("Currency","ETB")
            editor.commit()
            val intent= Intent(this,RegisterCommunicationMode::class.java)
            startActivity(intent)
            finish()
        }
        binding.imageButton1.setOnClickListener {
            editor.putString("Currency","ETB")
            editor.commit()
            val intent= Intent(this,RegisterCommunicationMode::class.java)
            startActivity(intent)
            finish()
        }
        binding.cardETB.setOnClickListener {
            editor.putString("Currency","ETB")
            editor.commit()
            val intent= Intent(this,RegisterCommunicationMode::class.java)
            startActivity(intent)
            finish()
        }

        //3 listeners for USD
        binding.textView2.setOnClickListener {
            editor.putString("Currency","USD")
            editor.commit()
            val intent= Intent(this,RegisterCommunicationMode::class.java)
            startActivity(intent)
            finish()
        }
        binding.imageButton2.setOnClickListener {
            editor.putString("Currency","USD")
            editor.commit()
            val intent= Intent(this,RegisterCommunicationMode::class.java)
            startActivity(intent)
            finish()
        }
        binding.cardUSD.setOnClickListener {
            editor.putString("Currency","USD")
            editor.commit()
            val intent= Intent(this,RegisterCommunicationMode::class.java)
            startActivity(intent)
            finish()
        }
        // 3 listeners for EURO
        binding.textView3.setOnClickListener {
            editor.putString("Currency","EURO")
            editor.commit()
            val intent= Intent(this,RegisterCommunicationMode::class.java)
            startActivity(intent)
            finish()
        }
        binding.imageButton3.setOnClickListener {
            editor.putString("Currency","EURO")
            editor.commit()
            val intent= Intent(this,RegisterCommunicationMode::class.java)
            startActivity(intent)
            finish()
        }
        binding.cardEuro.setOnClickListener {
            editor.putString("Currency","EURO")
            editor.commit()
            val intent= Intent(this,RegisterCommunicationMode::class.java)
            startActivity(intent)
            finish()
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,RegisterCommunicationMode::class.java))
    }
}