package net.geidea.payment.users.support

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.DBHandler
import net.geidea.payment.RegisterTerminalMode
import net.geidea.payment.databinding.ActivityUpdateMerchantIdBinding
class UpdateMerchantID : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateMerchantIdBinding
    private lateinit var dbHandler: DBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUpdateMerchantIdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHandler=DBHandler(this)
        binding.tvCurrentMID.text="Current MID:${dbHandler.getMID()}"
        binding.btnUpdate.setOnClickListener {
            if(binding.etMID.text.isNotEmpty()){
                if(binding.etMID.text.toString().length<15){
                    binding.etMID.error = "MID must be of lentgh 15"
                }else{
                    dbHandler.updateMID(binding.etMID.text.toString())
                    Toast.makeText(this,"MID updated", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,RegisterTerminalMode::class.java))
                    finish()
                }
            }else binding.etMID.error = "Please fill the MID !"
        }
        binding.btnSkip.setOnClickListener {
            startActivity(Intent(this,RegisterTerminalMode::class.java))
        }

    }
}