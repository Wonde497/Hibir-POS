package net.geidea.payment.users.support

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.geidea.payment.DBHandler
import net.geidea.payment.R
import net.geidea.payment.databinding.ActivityCommunicationConfigBinding

class CommunicationConfig : AppCompatActivity() {
    private lateinit var binding: ActivityCommunicationConfigBinding
    private lateinit var dbHandler: DBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCommunicationConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHandler=DBHandler(this)



        binding.btnRegister.setOnClickListener {
       dbHandler.registerIPAndPortNumber(binding.etIP.text.toString(),binding.etPortNo.text.toString())

            Toast.makeText(this,"Clicked",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,UpdateTID::class.java))
        }
        binding.btnSkip.setOnClickListener {
            Toast.makeText(this,"skipped",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,UpdateTID::class.java))

        }







    }

    override fun onResume() {
        super.onResume()
        binding.tvCurrentIP.text="Current IP:${dbHandler.getIPAndPortNumber()?.first}"
        binding.tvCurrentPort.text="Current Port:${dbHandler.getIPAndPortNumber()?.second}"

        Log.d("onresume",binding.tvCurrentIP.text.toString())
        Log.d("onresume",binding.tvCurrentPort.text.toString())
    }
}