package net.geidea.payment.users.support

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.geidea.payment.DBHandler
import net.geidea.payment.R
import net.geidea.payment.databinding.ActivityUpdateTidBinding

class UpdateTID : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateTidBinding
    private lateinit var dbHandler: DBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUpdateTidBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHandler=DBHandler(this)
        binding.tvCurrentTID.text="Current TID:${dbHandler.getTID()}"
        binding.btnUpdate.setOnClickListener {
          if(binding.etTID.text.isNotEmpty()){
              if(binding.etTID.text.toString().length<8){
                  binding.etTID.setError(" TID must be length 8 !")
              }else{
                  dbHandler.updateTID(binding.etTID.text.toString())
                  Toast.makeText(this,"TID updated", Toast.LENGTH_SHORT).show()
                  startActivity(Intent(this,UpdateMerchantID::class.java))
              }

          }else binding.etTID.setError("Please Enter TID !")
        }
        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this,UpdateMerchantID::class.java))
        }


    }
}