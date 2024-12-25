package net.geidea.payment.tlv

interface IBerTlvLogger {
    val isDebugEnabled: Boolean
    fun debug(aFormat: String?, vararg args: Any?)
}