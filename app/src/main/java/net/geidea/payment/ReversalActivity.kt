package net.geidea.payment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.geidea.payment.databinding.ActivityReversalBinding
import net.geidea.payment.transaction.model.TransData
import net.geidea.payment.transaction.view.CardReadActivity
import net.geidea.payment.users.supervisor.SupervisorLogin
import net.geidea.payment.utils.FirebaseDatabaseSingleton
import net.geidea.payment.utils.commonMethods
import net.geidea.utils.dialog.SweetAlertDialog

class ReversalActivity : AppCompatActivity() {
    private lateinit var binding:ActivityReversalBinding
    private lateinit var dbHandler: DBHandler
    private lateinit var sharedPreferences: SharedPreferences
   var commonMethods = commonMethods(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityReversalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences=getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        dbHandler=DBHandler(this)
        var txnDataList:List<Map<String,String>>

        binding.button.setOnClickListener {
            txnDataList=dbHandler.getTxnDataByApprovalCode(binding.editText.text.toString())
            if(txnDataList.isNotEmpty()){
                for (txnData in txnDataList) {
                    TransData.RequestFields.Field04=txnData[DBHandler.COLUMN_FIELD04].toString()

                    Log.d("Reversal Activity","amount:"+TransData.RequestFields.Field04)
                    TransData.RequestFields.Field11=txnData[DBHandler.COLUMN_FIELD11].toString()
                    TransData.RequestFields.Field12=txnData[DBHandler.COLUMN_FIELD12].toString()
                    TransData.RequestFields.Field13=txnData[DBHandler.COLUMN_FIELD13].toString()

                    TransData.RequestFields.Field37 = txnData[DBHandler.COLUMN_FIELD37].toString()
                    TransData.RequestFields.Field38 = txnData[DBHandler.COLUMN_FIELD38].toString()
                    Log.d("Reversal Activity","fld38:"+TransData.RequestFields.Field38)

                    TransData.RequestFields.Field39 = txnData[DBHandler.COLUMN_FIELD39].toString()
                    Log.d("Reversal Activity","fld39:"+TransData.RequestFields.Field39)

                    //Log.d("TAG", "filed39"+TransData.RequestFields.Field39)

                }
                Toast.makeText(this,"TXN Data found",Toast.LENGTH_SHORT).show()
                //val bundle=Bundle()
                //bundle.putString("field04",TransData.RequestFields.Field04)
               showConfirmDialog(TransData.RequestFields.Field04)


        }else{
                Toast.makeText(this,"NO TXN RECORDED with this approval code!",Toast.LENGTH_SHORT).show()
        }
        }

    }
    private fun showConfirmDialog(amount:String) {

        val dialog = SweetAlertDialog(this)
            .setTitleText("Confirmation")
            .setContentText("Are you sure you want to Reverse ${sharedPreferences.getString("Currency","")}${commonMethods.getReadableAmount(amount)}?")
            .setConfirmText("Confirm")
            .setCancelText("Cancel")
            .setConfirmClickListener {
                // Code to execute when "Confirm" is clicked
                var intent=Intent(this,SupervisorLogin::class.java)
                //intent.putExtras(bundle.getString("field04",""))
                startActivity(intent)
                finish()
            }
            .setCancelClickListener {
                    dialog ->
                // Code to execute when "Cancel" is clicked
                if (dialog != null) {
                    dialog.dismiss()
                }
            }
        dialog.show()
    }
}