package net.geidea.payment.print

sealed class PrintStatus {
    object PrintStarted : PrintStatus()
    object PrintCompleted : PrintStatus()
    class PrintError(val message: String) : PrintStatus()
}