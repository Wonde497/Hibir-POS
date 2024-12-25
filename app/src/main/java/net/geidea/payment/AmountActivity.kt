package net.geidea.payment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityAmountBinding
class AmountActivity : AppCompatActivity() {
private lateinit var binding:ActivityAmountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAmountBinding.inflate(layoutInflater)
        setContentView(binding.root)
         }
}