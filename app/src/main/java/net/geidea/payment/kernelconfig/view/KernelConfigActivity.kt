package net.geidea.payment.kernelconfig.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import net.geidea.payment.MainActivity
import net.geidea.payment.R
import net.geidea.payment.RegisterAdmin
import net.geidea.payment.databinding.ActivityKernelConfigBinding
import net.geidea.payment.kernelconfig.viewmodel.KernelConfigViewModel

@AndroidEntryPoint
class KernelConfigActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKernelConfigBinding
    private val viewModel by viewModels<KernelConfigViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_kernel_config)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        observerKernelConfigStatus()
    }


    private fun observerKernelConfigStatus(){
        viewModel.onKernelConfig().observe(this)
        {
            if (it)
            {
                val intent = Intent(this@KernelConfigActivity,RegisterAdmin::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}