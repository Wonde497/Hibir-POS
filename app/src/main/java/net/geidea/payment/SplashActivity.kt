package net.geidea.payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.pos.sdk.security.POIHsmManage
import com.pos.sdk.security.PedKcvInfo
import com.pos.sdk.security.PedKeyInfo
import com.pos.sdk.utils.PosUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import net.geidea.payment.databinding.ActivitySplashBinding
import net.geidea.payment.kernelconfig.view.KernelConfigActivity
import net.geidea.payment.usbcomm.USBCommunicationActivity.Companion.TAG
import net.geidea.payment.utils.IS_KERNEL_CONFIG_COMPLETED
import net.geidea.payment.utils.KEY_ALG_3DES
import net.geidea.payment.utils.MASTER_KEY_INDEX
import net.geidea.payment.utils.PED_NO_PROTECT_KEY
import net.geidea.payment.utils.PED_NO_PROTECT_KEY_INDEX
import net.geidea.payment.utils.SESSION_DATA_KEY_INDEX
import net.geidea.payment.utils.SESSION_MAC_KEY_INDEX
import net.geidea.payment.utils.SESSION_PIN_KEY_INDEX
import net.geidea.payment.utils.TAK_3DES_DATA
import net.geidea.payment.utils.TDK_3DES_DATA
import net.geidea.payment.utils.TMK_3DES_DATA
import net.geidea.payment.utils.TPK_3DES_DATA
import net.geidea.payment.utils.pinBlockType
import net.geidea.utils.DataStoreUtils
import net.geidea.utils.extension.ioCoroutine


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var binding: ActivitySplashBinding
    private lateinit var dbHandler: DBHandler
    private val pedKcvInfo = PedKcvInfo(0, ByteArray(5))



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.splashImage.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.splash_animation
            )
        )

             dbHandler=DBHandler(this)
        val isAdminLoggedIn=dbHandler.checkAdmin()
        val isTIDRegistered=dbHandler.checkTID()
        val isMIDRegistered=dbHandler.checkMID()
        val isMerchantNameRegistered=dbHandler.checkMerchantName()
        val isMerchantAddressRegistered=dbHandler.checkMerchantAddress()
        sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()

        ioCoroutine {
            delay(2000)
            val isKernelConfigured = DataStoreUtils.getInstance(this@SplashActivity).getBoolean(
                IS_KERNEL_CONFIG_COMPLETED
            ) ?: false
            //pinBlockType = "mksk"
          //injectMKSKey()
         if (isKernelConfigured) {

                if(isAdminLoggedIn){
                        if(isTIDRegistered){
                            if(isMIDRegistered){
                                if(isMerchantNameRegistered){
                                    if(isMerchantAddressRegistered){
                                        if(sharedPreferences.getBoolean("MERCHANT MODE",false)){
                                            if(sharedPreferences.getString("Currency","")=="ETB"||sharedPreferences.getString("Currency","")=="USD"||sharedPreferences.getString("Currency","")=="EURO"){
                                                if(sharedPreferences.getBoolean("4G Data",false) || sharedPreferences.getBoolean("WiFi",false)){
                                                    val intent = Intent(this@SplashActivity, MainMenuActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                }else{
                                                    val intent = Intent(this@SplashActivity, RegisterCommunicationMode::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                }

                                            }else{
                                                val intent = Intent(this@SplashActivity, RegisterCurrency::class.java)
                                                startActivity(intent)
                                                finish()
                                            }

                                        }else{
                                            val intent = Intent(this@SplashActivity, RegisterTerminalMode::class.java)
                                            startActivity(intent)
                                            finish()
                                        }


                                    }else{
                                        val intent = Intent(this@SplashActivity, RegisterMerchantAddress::class.java)
                                        startActivity(intent)
                                        finish()
                                    }

                                }else{
                                    val intent = Intent(this@SplashActivity, RegisterMerchantName::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                                  }else{
                               val intent = Intent(this@SplashActivity, RegisterMID::class.java)
                               startActivity(intent)
                               finish()
                                    }

                                   }else{
                                       val intent = Intent(this@SplashActivity, RegisterTID::class.java)
                                        startActivity(intent)
                                        finish()
                                           }



                              }else{
                                  val intent = Intent(this@SplashActivity, RegisterAdmin::class.java)
                              startActivity(intent)
                              finish()

                }
//             val intent=Intent(this,MainMenuActivity::class.java)
//             startActivity(intent)
        } else {
                val intent=Intent(this,KernelConfigActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun injectMKSKey() {

        // TMK: the master key of the terminal, which will be used to decrypt the session-data,tpk(pin block) and mac(tak) key
        var result = updateKeyMKSK(
            PED_NO_PROTECT_KEY,
            PED_NO_PROTECT_KEY_INDEX,
            TMK_3DES_DATA,
            POIHsmManage.PED_TMK,
            MASTER_KEY_INDEX
        )
        if (result != 0) {
            Log.d(TAG, "injectMKSK: updateKeyMKSK PED_TMK failed")
            // return
        }else {
            Log.d(TAG, "injectMKSK: updateKeyMKSK PED_TMK succeed")
            Log.d(TAG, pedKcvInfo.toString())
        }
        // TDK: the session key which is used to encrypt data
        result = updateKeyMKSK(
            POIHsmManage.PED_TMK,
            MASTER_KEY_INDEX,
            TDK_3DES_DATA,
            POIHsmManage.PED_TDK,
            SESSION_DATA_KEY_INDEX
        )
        if (result != 0) {
            Log.d(
                TAG, "injectMKSK: updateKeyMKSK PED_TDK failed"
            )
            //return
        }else Log.d(TAG, "injectMKSK: updateKeyMKSK PED_TDK succeed")



        // TAK: the session key that is used to generate MAC for the message
        result = updateKeyMKSK(
            POIHsmManage.PED_TMK,
            MASTER_KEY_INDEX,
            TAK_3DES_DATA,
            POIHsmManage.PED_TAK,
            SESSION_MAC_KEY_INDEX
        )
        if (result != 0) {
            Log.d(
                TAG, "injectMKSK: updateKeyMKSK PED_TAK failed"
            )
            //return
        }else{
            Log.d(TAG, "injectMKSK: updateKeyMKSK PED_TAK succeed")
             }

    }
    private fun updateKeyMKSK(
        protectKeyType: Int, protectKeyIndex: Int, Keydata: String, keyType: Int, keyIndex: Int
    ): Int {
        Log.d("TAG","updateKeyMKSK clicked")
        Log.d("TAG", "kcv1..........$pedKcvInfo")
        val keyData = PosUtils.hexStringToBytes(Keydata)
        Log.d("TAG", "key data...$Keydata")
        Log.d("TAG", "keyType...$keyType")
        Log.d("TAG", "keyIndex...$keyIndex")
        val mkInfo = PedKeyInfo(
            protectKeyType, protectKeyIndex, keyType, keyIndex, KEY_ALG_3DES, keyData.size, keyData
        )
        return POIHsmManage.getDefault().PedWriteKey(
            mkInfo, pedKcvInfo
        )
    }
}