package net.geidea.payment.utils

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.pos.sdk.printer.POIPrinterManager
import com.pos.sdk.printer.models.PrintLine
import com.pos.sdk.printer.models.TextPrintLine
import com.pos.sdk.security.POIHsmManage
import com.pos.sdk.security.PedKcvInfo
import com.pos.sdk.security.PedKeyInfo
import com.pos.sdk.utils.PosUtils
import net.geidea.payment.BuildConfig
import net.geidea.payment.R
import net.geidea.payment.customviews.PasswordDialog
import net.geidea.payment.listener.PinPadListener
import net.geidea.payment.print.PrintStatus
import net.geidea.payment.print.Printer.printList
import net.geidea.payment.transaction.viewmodel.CardReadViewModel
import net.geidea.utils.dialog.SweetAlertDialog
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class commonMethods(val activity: Context) {

   private val pedKcvInfo = PedKcvInfo(0, ByteArray(5))
   private lateinit var showProgressDialog: SweetAlertDialog


   fun getReadableAmount(amount: String?): String {
      return if (amount.isNullOrEmpty()) {
         "0.00"
      } else {
         val decimalFormat = DecimalFormat("0.00")
         decimalFormat.format(amount.toDouble() / 100.0)
      }
   }
   fun twodecimalplace(amount: String?): String{
      return if (amount.isNullOrEmpty()) {
         "0.00"
      } else {
         val decimalFormat = DecimalFormat("0.00")
         decimalFormat.format(amount.toDouble())
      }
   }

   fun updateKeyMKSK(
      protectKeyType: Int, protectKeyIndex: Int, Keydata: String, keyType: Int, keyIndex: Int
   ): Int {
      Log.d(TAG,"updateKeyMKSN clicked")
      Log.d(TAG,"kcv1.........."+pedKcvInfo)
      val keyData = PosUtils.hexStringToBytes(Keydata)
      Log.d(TAG,"key data..."+Keydata)
      Log.d(TAG,"keyType..."+keyType)
      Log.d(TAG,"keyIndex..."+keyIndex)
      val mkInfo = PedKeyInfo(
         protectKeyType, protectKeyIndex, keyType, keyIndex, KEY_ALG_3DES, keyData.size, keyData
      )
      return POIHsmManage.getDefault().PedWriteKey(
         mkInfo, pedKcvInfo
      )
   }
     fun showloading(){
        showProgressDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        showProgressDialog.setTitleText("Loading Please wait...")
        showProgressDialog.setCancelable(false)
        showProgressDialog.show()
      }
   fun showAlert(message:String){
      showProgressDialog = SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
      showProgressDialog.setTitleText(message)
      showProgressDialog.setCancelable(false)
      showProgressDialog.show()
   }
     fun dismissdialog(){
        showProgressDialog.dismissWithAnimation()
     }
   fun getdate():String{
      val current_Date = SimpleDateFormat("yy/MM/dd", Locale.getDefault()).format(Date())
      return current_Date
   }
   fun getTime():String{
     val current_Time = SimpleDateFormat("HH:mm:ss ", Locale.getDefault()).format(Date())
      return current_Time
   }
   }