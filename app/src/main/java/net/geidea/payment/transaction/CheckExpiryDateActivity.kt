package net.geidea.payment.transaction

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import net.geidea.payment.databinding.ActivityCheckExpiryDateBinding

class CheckExpiryDateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckExpiryDateBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckExpiryDateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonExDateconfirm.setOnClickListener() {
            validateExDate()
            validateExMonth()
            validateExYear()



        }
    }
    fun validateExDate(){

    }
    fun validateExMonth(){

    }
    fun validateExYear(){

    }



}

