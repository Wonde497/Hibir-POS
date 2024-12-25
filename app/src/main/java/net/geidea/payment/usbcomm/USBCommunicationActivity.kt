package net.geidea.payment.usbcomm

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import net.geidea.payment.R
import net.geidea.payment.databinding.ActivityUsbCommunicationBinding
import java.util.Locale

@AndroidEntryPoint
class USBCommunicationActivity : AppCompatActivity(), PortManager.PortManagerListener,
    View.OnClickListener {
    companion object {
        val TAG: String = "serialportsample"
        val deviceNameUSB: String = "/dev/ttyGS0"
        val MSG_GS_TO_S: Int = 1
    }

    private lateinit var mPortManager: PortManager
    var var1: ByteArray = byteArrayOf(0x17, 0x18, 0x19, 0x20, 0x21, 0x22)
    var var2: Int = 2048
    private var mThreadFlag = true
    private val mHandler = Handler(Looper.getMainLooper())
    private lateinit var mReadWriteHandler: ReadWriteHandler
    private lateinit var mHandlerThread: HandlerThread


    private lateinit var binding: ActivityUsbCommunicationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_usb_communication)
        binding.idPortOpen.setOnClickListener(this)
        binding.idPortClose.setOnClickListener(this)
        binding.idPortSend.setOnClickListener(this)
        binding.idPortRecv.setOnClickListener(this)
        binding.idPortReset.setOnClickListener(this)
        binding.idPortCancelrecv.setOnClickListener(this)
        binding.idPortNonblockrecv.setOnClickListener(this)
        binding.idPortConnect.setOnClickListener(this)
        binding.idPortDisconnect.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        mHandlerThread = HandlerThread(TAG)
        mHandlerThread.start()
        mReadWriteHandler = ReadWriteHandler(mHandlerThread.looper)
        mPortManager = PortManager(this, this)
        mPortManager.onPortStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        mThreadFlag = false
        onPortClose()
        onPortDisconnect()
        mPortManager.onPrinterStop()
    }

    private fun onPortOpen() {
        Log.d("FFFFFFFFF98764400000", "onPortOpen")
        mPortManager.open()
    }

    private fun onPortConnect(port: String) {
        Log.d("FFFFFFFFF98764400000", "onPortConnect")
        mPortManager.connect(port)
    }


    private fun onPortClose() {
        Log.d("FFFFFFFFF98764400000", "onPortClose")
        mPortManager.close()
    }

    private fun onPortDisconnect() {
        if (mPortManager.connectStatus !== 0) {
            Log.d("FFFFFFFFF98764400000", "onPortDisconnect")
            mPortManager.disconnect()
        }
    }

    private fun onPortSend(`var`: ByteArray) {
        mPortManager.send(`var`)
    }


    private fun onPortRecv(`var`: Int) {
        var recvData: ByteArray? = null
        recvData = mPortManager.recv(`var`)
        val toast: Toast =
            Toast.makeText(
                this@USBCommunicationActivity,
                bytesToHexString(recvData),
                Toast.LENGTH_SHORT
            )
        toast.show()
        mPortManager.send("Reply".toByteArray())
    }

    private fun onPortReset() {
        mPortManager.reset()
    }

    private fun onPortCancelRecv() {
        mPortManager.cancelRecv()
    }

    private fun onPortNonBlockRecv() {
        var recvNonBlockingData: ByteArray? = null
        recvNonBlockingData = mPortManager.recvNonBlocking()
        val toast: Toast = Toast.makeText(
            this@USBCommunicationActivity,
            bytesToHexString(recvNonBlockingData),
            Toast.LENGTH_SHORT
        )
        toast.show()
    }

    fun bytesToHexString(bArr: ByteArray): String {
        val sb = StringBuffer(bArr.size)
        var sTmp: String

        for (i in bArr.indices) {
            sTmp = Integer.toHexString(0xFF and bArr[i].toInt())
            if (sTmp.length < 2) sb.append(0)
            sb.append(sTmp.uppercase(Locale.getDefault()))
        }

        return sb.toString()
    }

    override fun onServiceConnected() {
    }

    inner class ReadWriteHandler(looper: Looper?) : Handler(looper!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_GS_TO_S -> Thread {
                    var recvData: ByteArray? = null
                    while (mThreadFlag) {
                        recvData = mPortManager.recv(var2)
                        if (recvData != null) {
                            Log.d(TAG, "recvData = " + bytesToHexString(recvData))
                            val recvStr: String = bytesToHexString(recvData)

                            mHandler.post(Runnable { binding.tvPortmanagerRecvdata.text = recvStr })
                        } else {
                            Log.d(TAG, "recvData is null ")
                            mHandler.post(Runnable { binding.tvPortmanagerRecvdata.text = "recvData is null " })
                        }
                    }
                }.start()

                else -> {}
            }
        }
    }

    override fun onClick(p0: View) {
        val id: Int = p0.id
        when (id) {
            R.id.id_port_open -> onPortOpen()
            R.id.id_port_close -> {
                mThreadFlag = false
                onPortDisconnect()
                onPortClose()
            }

            R.id.id_port_send -> onPortSend(var1)

            R.id.id_port_recv -> {}
            R.id.id_port_reset -> {}
            R.id.id_port_cancelrecv -> {}
            R.id.id_port_nonblockrecv -> {}
            R.id.id_port_connect -> {
                onPortDisconnect()
                onPortConnect(deviceNameUSB)
                mThreadFlag = true
                mReadWriteHandler.sendEmptyMessage(MSG_GS_TO_S)
            }

            R.id.id_port_disconnect -> {
                mThreadFlag = false
                onPortDisconnect()
            }
        }
    }
}