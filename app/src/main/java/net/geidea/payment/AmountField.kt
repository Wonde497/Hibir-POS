package net.geidea.payment

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.geidea.payment.databinding.ActivityAmountBinding
import net.geidea.payment.databinding.ActivityAmountFieldBinding
import net.geidea.payment.users.supervisor.SupervisorLogin

class AmountField : AppCompatActivity() {
    private lateinit var binding:ActivityAmountFieldBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAmountFieldBinding.inflate(layoutInflater)
        setContentView(binding.root)
       /* binding.button.setOnClickListener {
            startActivity(Intent(this,SupervisorLogin::class.java))
        }*/
    }
}