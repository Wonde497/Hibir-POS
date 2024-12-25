package net.geidea.payment

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.pos.sdk.printer.PosPrinter
import com.pos.sdk.printer.PosPrinterInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.geidea.payment.databinding.ActivityPrintNTimesBinding
import net.geidea.payment.print.PrintStatus
import net.geidea.payment.print.Printer
import net.geidea.utils.extension.inVisible

@AndroidEntryPoint
class PrintNTimesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrintNTimesBinding
    private var printNTimes:Long = 0
    private var counter = 1L
    private var stopPrinting = false
    private var isPrintFailed = false
    private var breakLoop = false
    private var isPrintObserveCalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_print_n_times)
        printNTimes = binding.printTimesCount.text.toString().toLong()

        binding.keyboardLayoutNTimesPrint.setOnInteractionListener(onKeyValue = {
            binding.printTimesCount.text = it
        }, onConfirm = { nCount, _ ->
            if (nCount in 1..100) {
                if(!hasPrinterPaper()) {
                    Toast.makeText(
                        this@PrintNTimesActivity,
                        "No Paper",
                        Toast.LENGTH_LONG
                    ).show()
                    resetValues()
                    return@setOnInteractionListener
                }
                isPrintFailed = false
                stopPrinting = false
                breakLoop = false
                binding.keyboardLayoutNTimesPrint.hideKeyBoard()
                printNTimes = nCount
                counter = 1
                if(!isPrintObserveCalled) {
                    isPrintObserveCalled = true
                    observePrintStatus()
                }
                CoroutineScope(Dispatchers.IO).launch {
                    updatePrintingMsg(1)
                    Printer.receiptCount = counter
                    doSamplePrint()
                }
            } else {
                Toast.makeText(
                    this@PrintNTimesActivity,
                    "Wrong Input, Value between 1 to 100",
                    Toast.LENGTH_LONG
                ).show()
                resetValues()
            }
        })

        binding.stopPrint.setOnClickListener(){
            stopPrinting = true
            if(isPrintFailed){
                breakLoop = true
                isPrintFailed = false
                resetValues()
                binding.keyboardLayoutNTimesPrint.showKeyBoard()
            }
        }
    }

    private fun resetValues() {
            binding.printTimesCount.text = "0"
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

    private fun doSamplePrint() {
        if (hasPrinterPaper()) {
            Printer.printerTest(this)
        }
    }

    private suspend fun updatePrintingMsg(msgType: Int) {
        var msg = ""
        when (msgType) {
            1 -> {
                delay(200)
                msg = "Printing...\n"
            }
            2 -> msg = "Printing Done\n"
            3 -> msg = "Printing Stop!!\n"
        }
        val msgFinal = "$msg$counter Out Of $printNTimes"
        binding.printCounters.text = msgFinal
    }

    private suspend fun proceedPrintComplete() {
        Log.d("observePrintStatus", "proceedPrintComplete counter:$counter")

        if (counter == printNTimes) {
            updatePrintingMsg(2)
            resetValues()
            binding.keyboardLayoutNTimesPrint.showKeyBoard()

        } else if (counter != printNTimes) {
            if (stopPrinting) {
                updatePrintingMsg(3)
                resetValues()
                binding.keyboardLayoutNTimesPrint.showKeyBoard()
            } else {
                delay(1000)
                counter++
                Printer.receiptCount = counter
                updatePrintingMsg(1)
                Log.d("observePrintStatus", "printing counter:$counter")
                doSamplePrint()
            }
        }

    }
    private fun observePrintStatus() {
        Printer.printStatus.observe(this) {
            CoroutineScope(Dispatchers.IO).launch {
                when (it) {
                    is PrintStatus.PrintStarted -> {
                        //do nothing
                    }

                    is PrintStatus.PrintCompleted -> {
                        proceedPrintComplete()
                    }

                    is PrintStatus.PrintError -> {
                        Log.d("observePrintStatus", "PrintError")
                        isPrintFailed = true
                        lifecycleScope.launch {
                            Toast.makeText(
                                this@PrintNTimesActivity,
                                it.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        while (!breakLoop){
                            if(hasPrinterPaper()) {
                                isPrintFailed = false
                                delay(3000)//wait 3 sec to load  paper properly
                                doSamplePrint()
                                break
                            }
                            delay(200)
                            Log.d("no paper", "waiting for paper load")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        Log.d("PrintNTimesActivity", "onDestroy called")
        super.onDestroy()
        breakLoop = true
    }

}