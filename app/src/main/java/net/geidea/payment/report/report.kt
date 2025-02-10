package net.geidea.payment.report

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pos.sdk.printer.POIPrinterManager
import com.pos.sdk.printer.models.PrintLine
import com.pos.sdk.printer.models.TextPrintLine
import net.geidea.payment.DBHandler
import net.geidea.payment.TxnType
import net.geidea.payment.print.Printer.printList
import net.geidea.payment.utils.FirebaseDatabaseSingleton
import net.geidea.payment.utils.commonMethods

  class Report(val context: Context,val activity: AppCompatActivity) {
  var  dbHandler= DBHandler(context)
    companion object{
        var purchase = 0.0; var reversal=0.0; var refund=0.0; var preauth=0.0; var preauthcomp=0.0; var purchasecashback=0.0; var cashadvance=0.0;
        var purcount=0; var revcount=0;var refucount=0;var precount=0;var precompcount=0;var purcashcount=0;var cashadvcount=0;
         var total=0.0;
    }
    var sharedPreferences= context.getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)

    var commonMethods= commonMethods(activity)
    private val printerManager: POIPrinterManager by lazy {
        POIPrinterManager(activity)
    }
    var printtype = "";
    var trannum=0
    lateinit var detailtxndata :List<Map<String,String>>

      fun getReport(){
           purchase = 0.0;  reversal=0.0;  refund=0.0;  preauth=0.0;  preauthcomp=0.0; purchasecashback=0.0;  cashadvance=0.0;
           purcount=0;  revcount=0; refucount=0; precount=0; precompcount=0; purcashcount=0; cashadvcount=0;
           total=0.0;

         var txnDataList:List<Map<String,String>> = dbHandler.getTxnData()

         if(txnDataList.isNotEmpty()){
             for (txnData in txnDataList) {
                 var txntype =txnData[DBHandler.TXN_TYPE].toString()
                 var amount =txnData[DBHandler.COLUMN_FIELD04].toString()

                    amount = commonMethods.getReadableAmount(amount)
                 Log.d("","ammmmmmmm2222222 "+amount)

                 when (txntype) {
                     TxnType.PURCHASE -> {

                       var  amtem = amount.toFloat()
                         Log.d("","ammmmmmmm111 "+amtem)
                         purchase = purchase+amtem
                         Log.d("","purrrrrrrrrrr "+purchase)

                         purcount = purcount+1
                     }
                     TxnType.REVERSAL -> {

                         var  amtem = amount.toFloat()
                         Log.d("","ammmmmmmm111 "+amtem)
                         reversal = reversal+amtem
                         revcount = revcount+1

                     }TxnType.REFUND -> {
                     var  amtem = amount.toFloat()
                     refund = refund+amtem
                     refucount = refucount
                     }
                     TxnType.PRE_AUTH -> {
                         var  amtem = amount.toFloat()
                         preauth = preauth+amtem
                         precount = precount+1
                     }TxnType.PRE_AUTH_COMPLETION -> {
                     var  amtem = amount.toFloat()
                     preauthcomp = preauthcomp+amtem
                     precompcount = precompcount+1
                     }TxnType.PURCHASE_CASHBACK -> {
                     var  amtem = amount.toFloat()
                     purchasecashback = purchasecashback+amtem
                     purcashcount = purcashcount+1
                     }TxnType.CASH_ADVANCE -> {
                      var  amtem = amount.toFloat()
                     cashadvance = cashadvance+amtem
                     cashadvcount = cashadvcount+1
                     }
                     else -> {
                         // Code to execute if expression doesn't match any of the above values
                     }
                 }
                }
                  if(sharedPreferences.getBoolean("MERCHANT MODE",true)) {
                      total = purchase - reversal - refund + preauthcomp + purchasecashback
                  }else {
                      total = cashadvance - reversal + preauthcomp + purchasecashback
                  }
              if(printtype!="") {

                  print()

                }

              }else{
             Toast.makeText(context,"NO TXN  FOUND", Toast.LENGTH_SHORT).show()
               }

              }

        fun printsummaryReport(){
            printtype ="summary"
            getReport()
        }
        fun printdetailedReport(){
                trannum=0
          detailtxndata = dbHandler.getTxnData()

            if(detailtxndata.isNotEmpty()){
                printtype ="detail"
                print()
            }else{
                Toast.makeText(context,"NO TXN  FOUND", Toast.LENGTH_SHORT).show()
            }



        }

         private  fun print() {
        FirebaseDatabaseSingleton.setLog("printReceipt")
        printerManager.open()
        printerManager.cleanCache()
        val state = printerManager.printerState
        if (state == POIPrinterManager.STATUS_IDLE) {
                     if(printtype =="summary") {
                         setPrintItems()
                     }else
                     if(printtype =="detail") {
                         setPrintItemdetail()
                     }

            val listener: POIPrinterManager.IPrinterListener =
                object : POIPrinterManager.IPrinterListener {
                    override fun onStart() {
                        //Print started
                    }

                    override fun onFinish() {
                        printerManager.cleanCache();
                        printerManager.close()
                        if(printtype =="detail"&&trannum<detailtxndata.size){
                            print()
                        }else{
                            printtype = ""
                        }
                    }

                    override fun onError(code: Int, msg: String) {
                        Log.e("POIPrinterManager", "code: " + code + "msg: " + msg)
                        printerManager.close()
                    }
                }
             if (state == 4) {
                printerManager.close()
                return
              }
            printerManager.beginPrint(listener)
        }

    }


    private fun setPrintItemdetail() {
        var mername = dbHandler.getMerName()
        var termid = dbHandler.getTID()
        var currency = sharedPreferences.getString("currency","")
        var amount = detailtxndata.get(trannum)[DBHandler.COLUMN_FIELD04]
        var appcode = detailtxndata.get(trannum)[DBHandler.COLUMN_FIELD38]
        var trntype = detailtxndata.get(trannum)[DBHandler.TXN_TYPE]
        var cardnum = detailtxndata.get(trannum)[DBHandler.COLUMN_FIELD02]
        var currdate = commonMethods.getdate()
        var currtime = commonMethods.getTime()
        trannum = trannum + 1
        printerManager.addBlankView(1)
        printerManager.addPrintLine(
            TextPrintLine(
                "Detailed Report", PrintLine.CENTER, 20, false
            )
        )
        printerManager.addBlankView(1)

        printerManager.addPrintLine(
            printList(
                "Merchant Name: ",
                "",
                mername,
                20,
                false
            )
        )
        printerManager.addPrintLine(
            printList(
                "Terminal Id: ",
                "",
                termid,
                20,
                false
            )
        )
        printerManager.addPrintLine(
            printList(
                "Transaction NO: ",
                "",
                (trannum).toString(),
                20,
                false
            )
        )

        printerManager.addPrintLine(

            printList(
                currdate,
                "",
                currtime,
                20,
                false
            )

        )
        printerManager.addPrintLine(
            TextPrintLine(
                cardnum, PrintLine.CENTER, 20, true
            )
        )
        printerManager.addPrintLine(

            printList(
                "Transaction type: ",
                "",
                trntype.toString(),
                20,
                false
            )

        )

        printerManager.addPrintLine(
            printList(
                "Approval Code: ",
                "",
                appcode.toString(),
                20,
                false
            )
        )
        printerManager.addPrintLine(
                TextPrintLine(
                    currency+" "+commonMethods.getReadableAmount(amount),
                     PrintLine.CENTER, 20, true
                )
                )
        printerManager.addBlankView(1)


    }



    private fun setPrintItems() {
        printerManager.addBlankView(1)
        printerManager.addPrintLine(
            TextPrintLine(
                "Summary Report", PrintLine.CENTER, 20, false
            )
        )
        printerManager.addBlankView(1)

        printerManager.addPrintLine(
            printList(
                "Transaction type: ",
                "",
                "Amount ",
                20,
                false
            )
        )
        printerManager.addBlankView(1)

        printerManager.addPrintLine(
            printList(
                "Purchase: ",
                "",
                commonMethods.twodecimalplace(purchase.toString()),
                20,
                false
            )
        )
        printerManager.addPrintLine(
            printList(
                "Reversal: ",
                "",
                commonMethods.twodecimalplace(reversal.toString()),
                20,
                false
            )
        )
        printerManager.addPrintLine(
            printList(
                "Refund: ",
                "",
                commonMethods.twodecimalplace(refund.toString()),
                20,
                false
            )
        )
        printerManager.addPrintLine(
            printList(
                "Preauth: ",
                "",
                commonMethods.twodecimalplace(preauth.toString()),
                20,
                false
            )
        )
        printerManager.addPrintLine(
            printList(
                "Preauth Completion: ",
                "",
                commonMethods.twodecimalplace( preauthcomp.toString()),
                20,
                false
            )
        )
        printerManager.addPrintLine(
            printList(
                "Purchase Cash back: ",
                "",
                commonMethods.twodecimalplace(purchasecashback.toString()),
                20,
                false
            )
        )
        printerManager.addPrintLine(
            printList(
                "Cash advance: ",
                "",
                commonMethods.twodecimalplace(cashadvance.toString()),
                20,
                false
            )
        )
        printerManager.addBlankView(1)
        printerManager.addPrintLine(
            printList(
                "Total: ",
                "",
                commonMethods.twodecimalplace(total.toString()),
                20,
                false
            )
        )
       /* printerManager.addPrintLine(
            TextPrintLine(
                "THANK YOU", PrintLine.CENTER, 20, true
            )
        )


        printerManager.addPrintLine(
            TextPrintLine(
                "<<Merchant Copy>>", PrintLine.CENTER, 20, true
            )
        )*/

        printerManager.addBlankView(5)

    }
   }