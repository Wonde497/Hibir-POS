package net.geidea.payment.print

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.pos.sdk.printer.POIPrinterManager
import com.pos.sdk.printer.POIPrinterManager.IPrinterListener
import com.pos.sdk.printer.models.BitmapPrintLine
import com.pos.sdk.printer.models.PrintLine
import com.pos.sdk.printer.models.TextPrintLine
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.geidea.payment.R

object Printer {
    private const val TAG = "Printer"
    var receiptCount = 0L
    var printStatus: MutableLiveData<PrintStatus> = MutableLiveData()
        private set

    fun printerTest(context: Context) {
        val printerManager = POIPrinterManager(context)
        printerManager.open()
        val state = printerManager.printerState
        Log.d(TAG, "printer state = $state")
        if (state == POIPrinterManager.STATUS_IDLE) {
            printerManager.setPrintGray(5)
            printerManager.setLineSpace(5)
            printerManager.addPrintLine(TextPrintLine(" ", 0, 40))
            printerManager.addPrintLine(TextPrintLine("Receipt:$receiptCount", PrintLine.CENTER, 20))
            var bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.geidea_emblem)
            printerManager.addPrintLine(BitmapPrintLine(bitmap, PrintLine.CENTER))
            val str2 =
                "CANARY CENTER, 7304 PRINCE ABDULAZIZ IBN MUSAID AS SULIMANIYAH DISTRICT 12243 OFF#112, 1/F, Riyadh Saudi Arabia"
            val p2: PrintLine = TextPrintLine(str2, PrintLine.LEFT, 20)
            printerManager.addPrintLine(p2)
            val list1 = printList("24 June 2025", "     Assistant 6", "815002", 18, false)
            printerManager.addPrintLine(list1)
            val list2 = printList("Item", "Quantity", "Price", 24, true)
            printerManager.addPrintLine(list2)
            val list3 = printList("Tomato", "1", "$2.08", 24, false)
            printerManager.addPrintLine(list3)
            val list4 = printList("Orange", "1", "$1.06", 24, false)
            printerManager.addPrintLine(list4)
            val p3: PrintLine = TextPrintLine("Total  $3.14", PrintLine.RIGHT)
            printerManager.addPrintLine(p3)
            printerManager.addPrintLine(TextPrintLine(""))
            // drawable/barcode.jpg  100*100
            bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.barcode)
            printerManager.addPrintLine(BitmapPrintLine(bitmap, PrintLine.CENTER))
            printerManager.addPrintLine(TextPrintLine(""))
            val str3 = "Did you know you could have earned Rewards points on this purchase?"
            val p4: PrintLine = TextPrintLine(str3, PrintLine.CENTER)
            printerManager.addPrintLine(p4)
            val p5: PrintLine =
                TextPrintLine("Simply sign up today for a Membership Card!", PrintLine.CENTER)
            printerManager.addPrintLine(p5)
            bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.barcode_12345)
            printerManager.addPrintLine(BitmapPrintLine(bitmap, PrintLine.CENTER))
            printerManager.addPrintLine(TextPrintLine(" ", 0, 100))
            val listener: IPrinterListener = object : IPrinterListener {
                override fun onStart() {}
                override fun onFinish() {
                    printerManager.close()
                    printStatus.postValue(PrintStatus.PrintCompleted)
                }

                override fun onError(code: Int, msg: String) {
                    Log.e("POIPrinterManager", "code: " + code + "msg: " + msg)
                    printerManager.close()
                    printStatus.postValue(PrintStatus.PrintError("Print Error:$code $msg"))
                }
            }
            if (state == 4) {
                printerManager.close()
                return
            }
            printerManager.beginPrint(listener)
        }
    }

     fun printList(
        leftStr: String,
        centerStr: String,
        rightStr: String,
        size: Int,
        bold: Boolean
    ): List<TextPrintLine> {
        val textPrintLine1 = TextPrintLine(leftStr, PrintLine.LEFT, size, bold)
        val textPrintLine2 = TextPrintLine(centerStr, PrintLine.CENTER, size, bold)
        val textPrintLine3 = TextPrintLine(rightStr, PrintLine.RIGHT, size, bold)
        val list: MutableList<TextPrintLine> = ArrayList()
        list.add(textPrintLine1)
        list.add(textPrintLine2)
        list.add(textPrintLine3)
        return list
    }
}


