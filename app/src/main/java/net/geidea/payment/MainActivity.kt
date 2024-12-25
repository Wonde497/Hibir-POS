package net.geidea.payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.pos.sdk.printer.PosPrinter
import com.pos.sdk.printer.PosPrinterInfo
import com.pos.sdk.security.POIHsmManage
import com.pos.sdk.security.PedKcvInfo
import com.pos.sdk.security.PedKeyInfo
import com.pos.sdk.utils.PosByteArray
import com.pos.sdk.utils.PosUtils
import dagger.hilt.android.AndroidEntryPoint
import net.geidea.payment.databinding.ActivityMainBinding
import net.geidea.payment.print.Printer
import net.geidea.payment.tlv.HexUtil
import net.geidea.payment.usbcomm.USBCommunicationActivity
import net.geidea.payment.utils.DUKPT_INDEX
import net.geidea.payment.utils.DUKPT_IPEK
import net.geidea.payment.utils.DUKPT_KSN
import net.geidea.payment.utils.KEY_ALG_3DES
import net.geidea.payment.utils.MASTER_KEY_INDEX
import net.geidea.payment.utils.PED_NO_PROTECT_KEY
import net.geidea.payment.utils.PED_NO_PROTECT_KEY_INDEX
import net.geidea.payment.utils.SESSION_DATA_KEY_INDEX
import net.geidea.payment.utils.SESSION_MAC_KEY_INDEX
import net.geidea.payment.utils.SESSION_PIN_KEY_INDEX
import net.geidea.payment.utils.TAK_3DES_DATA
import net.geidea.payment.utils.TDK_3DES_DATA
import net.geidea.payment.utils.TLK_3DES_KEY
import net.geidea.payment.utils.TL_KEY_INDEX
import net.geidea.payment.utils.TMK_3DES_DATA
import net.geidea.payment.utils.TPK_3DES_DATA
import net.geidea.payment.utils.pinBlockType
import net.geidea.utils.extension.ioCoroutine
import net.geidea.utils.extension.withMainContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private val pedKcvInfo = PedKcvInfo(0, ByteArray(5))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        binding.toolbar.navIcon.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        navItemCLickListener()

     }

    private fun openPrintNtimesScreen() {
        val intent = Intent(this@MainActivity, PrintNTimesActivity::class.java)
        startActivity(intent)
    }

    private fun navItemCLickListener() {
        binding.navigationView.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            val menuId = it.itemId
            when (menuId) {
                R.id.action_print -> {
                    doSamplePrint()

                }
                R.id.action_print_n_times -> {
                    openPrintNtimesScreen()
                }

                R.id.action_settings -> {
                    openSettingsScreen()
                }

                R.id.action_usb_communication -> {
                    openUSBCommunication()
                }

                R.id.action_erase_all_key -> {
                    ioCoroutine {
                        val result = POIHsmManage.getDefault().PedErase()
                        withMainContext {
                            if (result == 0) {
                                Toast.makeText(this@MainActivity, "succeed", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(this@MainActivity, "failure", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    }

                }

                R.id.action_inject_dukpt -> {
                    pinBlockType = "dukpt"
                    ioCoroutine {
                        val result = injectDukptKey(DUKPT_INDEX, DUKPT_IPEK, DUKPT_KSN)
                        Log.d(TAG,"kcvksn.........."+pedKcvInfo)
                        withMainContext {
                            if (result == 0) {
                                Toast.makeText(this@MainActivity, "succeed", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(this@MainActivity, "failure", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                    }

                }

                R.id.action_fetch_current_ksn -> {
                    val currentKSN = getCurrentKSN() ?: ""
                    Toast.makeText(this@MainActivity, currentKSN, Toast.LENGTH_SHORT).show()

                }

                R.id.action_increase_ksn -> {
                    val newKSN = increaseKSN() ?: ""
                    Toast.makeText(this@MainActivity, newKSN, Toast.LENGTH_SHORT).show()
                }

                R.id.action_master_session_key -> {
                   // pinBlockType = "mksk"
                    ioCoroutine {
                        //injectTLK()
                        injectMKSKey()
                    }
                }
            }
            true
        }
    }


    private fun injectTLK() {
        // TLK: the zmk/tlk(system key) to decrypt master key
        val result = updateKeyMKSK(
            PED_NO_PROTECT_KEY,
            PED_NO_PROTECT_KEY_INDEX,
            TLK_3DES_KEY,
            POIHsmManage.PED_TLK,
            TL_KEY_INDEX
        )

        if (result != 0) {
            Log.d(
                TAG, "injectMKSK: updateKeyMKSK PED_TLK failed"
            )
            return
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

        // TPK: the session key which is used to encrypt the PIN
        result = updateKeyMKSK(
            POIHsmManage.PED_TMK,
            MASTER_KEY_INDEX,
            TPK_3DES_DATA,
            POIHsmManage.PED_TPK,
            SESSION_PIN_KEY_INDEX
        )
        if (result != 0) {
            Log.d(
                TAG, "injectMKSK: updateKeyMKSK PED_TPK failed"
            )
           // return
        }
        else Log.d(TAG, "injectMKSK: updateKeyMKSK PED_TPK succeed")


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


    private fun doSamplePrint() {
        if (hasPrinterPaper()) {
            Printer.printerTest(this@MainActivity)
        }
    }

    private fun openSettingsScreen() {
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun openUSBCommunication() {
        val intent = Intent(this@MainActivity, USBCommunicationActivity::class.java)
        startActivity(intent)
    }


    private fun hasPrinterPaper(): Boolean {
        return try {
            val info = PosPrinterInfo()
            PosPrinter.getPrinterInfo(0, info)
            info.mHavePaper == 1
        } catch (e: Exception) {
            false
        }
    }

    private fun injectDukptKey(keyIndex: Int, keyData: String, ksnData: String): Int {
        val kcvInfo = PedKcvInfo(0, ByteArray(5))

        return POIHsmManage.getDefault().PedWriteTIK(
            keyIndex, 0, 16, HexUtil.parseHex(keyData), HexUtil.parseHex(ksnData), kcvInfo
        )
    }

    private fun getCurrentKSN(): String? {
        val rspBuf = PosByteArray()
        val result = POIHsmManage.getDefault().PedGetDukptKsn(1, rspBuf)
        return if (result == 0) {
            HexUtil.toHexString(rspBuf.buffer)
        } else null
    }

    private fun increaseKSN(): String? {
        val rspBuf = PosByteArray()
        POIHsmManage.getDefault().PedDukptIncreaseKsn(1)
        val result = POIHsmManage.getDefault().PedGetDukptKsn(1, rspBuf)
        return if (result == 0) {
            HexUtil.toHexString(rspBuf.buffer)
        } else null
    }

    private fun updateKeyMKSK(
        protectKeyType: Int, protectKeyIndex: Int, Keydata: String, keyType: Int, keyIndex: Int
    ): Int {
     Log.d(TAG,"updateKeyMKSN clicked")
        Log.d(TAG,"kcv1.........."+pedKcvInfo)
        val keyData = PosUtils.hexStringToBytes(Keydata)
        Log.d(TAG,"key data..."+Keydata)
        Log.d(TAG,"keyType..."+keyType)
        Log.d(TAG,"keyIndex..."+keyIndex)
        val mkInfo = PedKeyInfo(
            protectKeyType, protectKeyIndex, keyType, keyIndex, KEY_ALG_3DES, keyData.size, keyData
        )
        return POIHsmManage.getDefault().PedWriteKey(
            mkInfo, pedKcvInfo
        )
    }
}
