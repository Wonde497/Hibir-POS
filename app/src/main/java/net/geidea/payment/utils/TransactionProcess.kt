package net.geidea.payment.utils

import net.geidea.payment.transaction.model.EntryMode

sealed class TransactionProcess {
    class CardReadSuccess(val appLabelName: String, val entryMode: EntryMode) : TransactionProcess()
    class CardRetry(val message: String) : TransactionProcess()
    object MultipleCardFound : TransactionProcess()
    object PinRequestedDukpt : TransactionProcess()
    class EMVError(val message: String) : TransactionProcess()
    class MultipleApplication(val labelList: List<String>) : TransactionProcess()
    class TransactionStatus(var status: Boolean) : TransactionProcess()
    class TransactionErr(val message: String) : TransactionProcess()
    object DippedCardDetected: TransactionProcess()
    object CardDetected: TransactionProcess()
    class CardNotSupport(val message: String): TransactionProcess()
    object PinRequestedMkSk : TransactionProcess()
}
