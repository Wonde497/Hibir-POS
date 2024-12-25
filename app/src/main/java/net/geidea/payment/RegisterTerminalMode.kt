package net.geidea.payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityRegisterTerminalModeBinding

class RegisterTerminalMode : AppCompatActivity() {


    private lateinit var binding:ActivityRegisterTerminalModeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRegisterTerminalModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imageButton1.setOnClickListener {
            val intent= Intent(this,RegisterCurrency::class.java)
            startActivity(intent)
            finish()
        }
        binding.textView1.setOnClickListener {
            sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
            editor=sharedPreferences.edit()
            editor.putBoolean("MERCHANT MODE",true)
            editor.commit()
            val intent= Intent(this,RegisterCurrency::class.java)
            startActivity(intent)
            finish()
        }
        binding.cardMerchant.setOnClickListener {
            sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
            editor=sharedPreferences.edit()
            editor.putBoolean("MERCHANT MODE",true)
            editor.commit()
            val intent= Intent(this,RegisterCurrency::class.java)
            startActivity(intent)
            finish()

        }
        binding.imageButton1.setOnClickListener {
            sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
            editor=sharedPreferences.edit()
            editor.putBoolean("MERCHANT MODE",true)
            editor.commit()
            val intent= Intent(this,RegisterCurrency::class.java)
            startActivity(intent)
            finish()
        }
        binding.textView2.setOnClickListener {
            sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
            editor=sharedPreferences.edit()
            editor.putBoolean("MERCHANT MODE",false)
            editor.commit()
            val intent= Intent(this,RegisterCurrency::class.java)
            startActivity(intent)
            finish()
        }
        binding.cardBranch.setOnClickListener {
            sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
            editor=sharedPreferences.edit()
            editor.putBoolean("MERCHANT MODE",false)
            editor.commit()
            val intent= Intent(this,RegisterCurrency::class.java)
            startActivity(intent)
            finish()
        }
        binding.imageButton2.setOnClickListener{

            sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
            editor=sharedPreferences.edit()
            editor.putBoolean("MERCHANT MODE",false)
            editor.commit()
            val intent= Intent(this,RegisterCurrency::class.java)
            startActivity(intent)
            finish()
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,RegisterCurrency::class.java))
    }
}