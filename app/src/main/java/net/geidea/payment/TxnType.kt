package net.geidea.payment

     class TxnType {
         companion object {
             const val BALANCE_INQUIRY="balance inquiry"
             const val PURCHASE = "purchase"
             const val REVERSAL = "reversal"
             const val SETTLEMENT = "settlement"
             const val PRE_AUTH = "preauth"

             const val PRE_AUTH_COMPLETION = "preauthcomp"
             const val REFUND = "refund"
             const val PURCHASE_CASHBACK = "purchasecashback"
             const val M_PURCHASE="manualPurchase"
             const val M_REVERSAL="manualReversal"
             const val CASH_ADVANCE = "cashadvance"
             const val KEY_DOWNLOAD = "Key Download"
             const val M_BALANCE_INQUIRY="manual balance inquiry"
             const val M_REFUND="manual refund"


         }


     }