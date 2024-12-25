package net.geidea.payment.com

import android.util.Log
import net.geidea.payment.tlv.HexUtil
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.net.SocketException

class Comm {
    private val TAG = "EMVDemo-Comm"
    private var socket: Socket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private var status = 0
    var ip: String = ""
    var port: Int = 0

    constructor() {
        status = 0
        ip = ""
        port = 0
        outputStream = null
        inputStream = null
    }

    constructor(ip: String, port: Int) {
        status = 0
        this.ip = ip
        this.port = port
        outputStream = null
        inputStream = null
    }

    fun connect(ip: String, port: Int): Boolean {
        this.ip = ip
        this.port = port
        return connect()
    }

    fun connect(): Boolean {
        Log.d(TAG, "SERVER CONNECTED ")
        Log.e(TAG, "COM host address:172.16.17.27   Port:6022")

        if (status > 0) {
            if (this.ip == ip && this.port == port) {
                return true
            } else {
                disconnect()
            }
        }
        return try {
            socket = Socket(ip, port)
            if (socket == null) {
                return false
            }
            this.ip = ip
            this.port = port
            status = 1
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun send(data: ByteArray): Int {
        if (status <= 0) {
            return 0
        }

        Log.d(TAG, "PACKET SENT SUCCESSFULLY ")
        Log.d(TAG, "SENT DATA(HEX) " + HexUtil.toHexString(data))
        Log.d(TAG, "SENT String " + String(data))

        return try {
            outputStream = socket?.getOutputStream()
            if (outputStream == null) {
                return 0
            }

            outputStream!!.write(data)
            outputStream!!.flush()
            data.size
        } catch (e: IOException) {
            e.printStackTrace()
            0
        }
    }
    fun send11(data: String): Int {
        if (status <= 0) {
            return 0
        }

        Log.d(TAG, "SEND: ")
        Log.d(TAG, data)

        return try {
            outputStream = socket?.getOutputStream()
            if (outputStream == null) {
                return 0
            }

            outputStream!!.write(data.toByteArray())
            outputStream!!.flush()
            data.toByteArray().size
        } catch (e: IOException) {
            e.printStackTrace()
            0
        }
    }

    fun receive(wantLength: Int, timeoutSecond: Int): ByteArray? {
        Log.d(TAG, "RECEIVE....:")
        if (status <= 0) {
            return null
        }
        return try {
            socket?.soTimeout = timeoutSecond * 1000
            inputStream = socket?.getInputStream()
            if (inputStream == null) {
                return null
            }
            val tmp = ByteArray(wantLength)
            val recvLen = inputStream!!.read(tmp)
            if (recvLen > 0) {
                val ret = ByteArray(recvLen)
                System.arraycopy(tmp, 0, ret, 0, recvLen)
                ret
            } else {
                null
            }
        } catch (e: SocketException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun disconnect() {
        status = 0
        try {
            inputStream?.close()
            inputStream = null
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            outputStream?.close()
            outputStream = null
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            socket?.close()
            socket = null
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
