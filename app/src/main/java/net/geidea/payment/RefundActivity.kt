package net.geidea.payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.geidea.payment.databinding.ActivityRefundBinding
import net.geidea.payment.transaction.model.TransData
import net.geidea.payment.utils.commonMethods
import net.geidea.utils.dialog.SweetAlertDialog

class RefundActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRefundBinding
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var showProgressDialog:SweetAlertDialog
    private lateinit var common:commonMethods
    lateinit var txntype: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     binding=ActivityRefundBinding.inflate(layoutInflater)
     setContentView(binding.root)
        showProgressDialog=SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE)
        showProgressDialog.setTitleText("Please wait...")
        showProgressDialog.setCancelable(false)
        sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        txntype=sharedPreferences.getString("TXN_TYPE","").toString()


        val transData= TransData(this)
        common= commonMethods(this)
        val dbHandler=DBHandler(this)
        var txnDataList:List<Map<String,String>>

        binding.button.setOnClickListener {
            Log.d("Refund", "search button clicked")
            txnDataList=dbHandler.getTxnData2ByApprovalCode(binding.editText.text.toString())
            if(txnDataList.isNotEmpty()){
                val tranTypeInDb= txnDataList[0][DBHandler.TXN_TYPE]
                Log.d("Refund", "transaction in Db:$tranTypeInDb")
                Log.d("Refund", "transaction:$txntype")

                if((txntype==TxnType.REFUND)&&(tranTypeInDb==TxnType.PURCHASE)) {
                    for (txnData in txnDataList) {
                        TransData.RequestFields.Field02 = txnData[DBHandler.COLUMN_FIELD02].toString()

                        TransData.RequestFields.Field04 = txnData[DBHandler.COLUMN_FIELD04].toString()

                        Log.d("Reversal Activity", "amount:" + TransData.RequestFields.Field04)
                        TransData.RequestFields.Field11 = txnData[DBHandler.COLUMN_FIELD11].toString()
                        TransData.RequestFields.Field12 = txnData[DBHandler.COLUMN_FIELD12].toString()
                        TransData.RequestFields.Field13 = txnData[DBHandler.COLUMN_FIELD13].toString()
                        TransData.RequestFields.Field14 = txnData[DBHandler.COLUMN_FIELD14].toString()

                        TransData.RequestFields.Field37 =
                            txnData[DBHandler.COLUMN_FIELD37].toString()
                        TransData.RequestFields.Field38 =
                            txnData[DBHandler.COLUMN_FIELD38].toString()
                        Log.d("Refund Activity", "fld37:" + TransData.RequestFields.Field37)

                        TransData.RequestFields.Field39 = txnData[DBHandler.COLUMN_FIELD39].toString()
                        Log.d("Reversal Activity", "fld39:" + TransData.RequestFields.Field39)

                        //Log.d("TAG", "filed39"+TransData.RequestFields.Field39)

                    }
                    Toast.makeText(this, "TXN Data found", Toast.LENGTH_SHORT).show()
                    //val bundle=Bundle()
                    //bundle.putString("field04",TransData.RequestFields.Field04)
                    showConfirmDialog(TransData.RequestFields.Field04)


                } else if((txntype==TxnType.PRE_AUTH_COMPLETION)&&(tranTypeInDb==TxnType.PRE_AUTH)){
                    for (txnData in txnDataList) {
                        TransData.RequestFields.Field02 = txnData[DBHandler.COLUMN_FIELD02].toString()

                        TransData.RequestFields.Field04 = txnData[DBHandler.COLUMN_FIELD04].toString()

                        Log.d("Reversal Activity", "amount:" + TransData.RequestFields.Field04)
                        TransData.RequestFields.Field11 = txnData[DBHandler.COLUMN_FIELD11].toString()
                        TransData.RequestFields.Field12 = txnData[DBHandler.COLUMN_FIELD12].toString()
                        TransData.RequestFields.Field13 = txnData[DBHandler.COLUMN_FIELD13].toString()
                        TransData.RequestFields.Field14 = txnData[DBHandler.COLUMN_FIELD14].toString()

                        TransData.RequestFields.Field37 = txnData[DBHandler.COLUMN_FIELD37].toString()
                        TransData.RequestFields.Field38 = txnData[DBHandler.COLUMN_FIELD38].toString()
                        Log.d("Refund Activity", "fld37:" + TransData.RequestFields.Field37)

                        TransData.RequestFields.Field39 = txnData[DBHandler.COLUMN_FIELD39].toString()
                        Log.d("Reversal Activity", "fld39:" + TransData.RequestFields.Field39)

                        //Log.d("TAG", "filed39"+TransData.RequestFields.Field39)

                    }

            }else if((txntype==TxnType.M_REFUND)&&(tranTypeInDb==TxnType.M_PURCHASE)) {

                    Log.d("Refund", "transaction:$txntype")
                    Log.d("Refund", "transaction in Db:$tranTypeInDb")
                    // clear all field values
                    transData.clearVariables()

                    TransData.RequestFields.Header = "30606020153535"
                    TransData.RequestFields.primaryBitmap="703C05800EC00004"
                    TransData.RequestFields.MTI="0200"
                    TransData.RequestFields.Field03="200000"
                    TransData.RequestFields.Field22="0010"
                    TransData.RequestFields.Field24="0001"
                    TransData.RequestFields.Field25="00"
                    TransData.RequestFields.Field41=dbHandler.getTID()
                    TransData.RequestFields.Field42=dbHandler.getMID()
                    TransData.RequestFields.Field62 = "0006"

                    for (txnData in txnDataList) {

                        TransData.RequestFields.Field02 = txnData[DBHandler.COLUMN_FIELD02].toString()

                        TransData.RequestFields.Field04 = txnData[DBHandler.COLUMN_FIELD04].toString()

                        Log.d("Reversal Activity", "amount:" + TransData.RequestFields.Field04)
                        TransData.RequestFields.Field11 = txnData[DBHandler.COLUMN_FIELD11].toString()
                        TransData.RequestFields.Field12 = txnData[DBHandler.COLUMN_FIELD12].toString()
                        TransData.RequestFields.Field13 = txnData[DBHandler.COLUMN_FIELD13].toString()
                        TransData.RequestFields.Field14 = txnData[DBHandler.COLUMN_FIELD14].toString()

                        TransData.RequestFields.Field37 =
                            txnData[DBHandler.COLUMN_FIELD37].toString()
                        TransData.RequestFields.Field38 =
                            txnData[DBHandler.COLUMN_FIELD38].toString()
                        Log.d("Refund Activity", "fld37:" + TransData.RequestFields.Field37)

                        TransData.RequestFields.Field39 = txnData[DBHandler.COLUMN_FIELD39].toString()
                        Log.d("Reversal Activity", "fld39:" + TransData.RequestFields.Field39)

                        //Log.d("TAG", "filed39"+TransData.RequestFields.Field39)

                    }
                    Toast.makeText(this, "TXN Data found", Toast.LENGTH_SHORT).show()
                    //val bundle=Bundle()
                    //bundle.putString("field04",TransData.RequestFields.Field04)
                    showConfirmDialog(TransData.RequestFields.Field04)


                }
            }else{
                Toast.makeText(this,"NO TXN RECORDED with this approval code !", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun showConfirmDialog(amount: String) {
        var txn:String=""
        if(txntype==TxnType.REFUND||txntype==TxnType.M_REFUND){
             txn=TxnType.REFUND
        }else if(txntype==TxnType.PRE_AUTH_COMPLETION){
            txn="Complete"
        }
        val dialog = SweetAlertDialog(this)
            .setTitleText("Confirmation")
            .setContentText("Are you sure you want to $txn ${sharedPreferences.getString("Currency", "")}${common.getReadableAmount(amount)}?")
            .setConfirmText("Confirm")
            .setCancelText("Cancel")
            .setConfirmClickListener {dialog ->
                if (txntype == TxnType.REFUND||txntype==TxnType.PRE_AUTH_COMPLETION||txntype==TxnType.M_REFUND) {
                    val intent = Intent(this, AmountField::class.java)
                    //intent.putExtras(bundle.getString("field04",""))
                    startActivity(intent)
                    finish()
                } else {
                    /* Code to execute when "Cancel" is clicked
                    dialog?.dismiss()
                    showProgressDialog.show()
                    val handler = Handler()
                    handler.postDelayed({
                        Thread(doManualReversal).start()
                    }, 2000)
*/
                }
            }
            .setCancelClickListener { dialog ->
                // Code to execute when "Cancel" is clicked
                dialog?.dismiss()
            }
        dialog.show()
    }

}