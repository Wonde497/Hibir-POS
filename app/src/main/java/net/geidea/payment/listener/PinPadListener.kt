package net.geidea.payment.listener


interface PinPadListener {
    fun pinPadDisplayed()
    fun onPinTimeout()
    fun onPinQuit()
    fun pinType(pinType: Int)
    fun offlinePinVerified()
    fun on117Or196PinBlockSuccess(pinBlock: String)//117 or 196 case
    fun pinRetry(pendingCount: Int = 0, retryCount: Int = 0, pinType: Int)
}