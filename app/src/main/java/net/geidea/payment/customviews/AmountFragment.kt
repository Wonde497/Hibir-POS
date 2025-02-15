package net.geidea.payment.customviews

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import net.geidea.payment.databinding.FragmentAmountBinding
import net.geidea.payment.transaction.view.CardReadActivity
import net.geidea.payment.utils.FirebaseDatabaseSingleton
import java.text.DecimalFormat
import net.geidea.utils.CurrencyConverter
import net.geidea.payment.TxnType
import net.geidea.payment.transaction.ManualCardEntry
import net.geidea.payment.transaction.model.TransData
import net.geidea.payment.users.supervisor.ManualRefundActivity
import net.geidea.payment.users.supervisor.SupervisorLogin

class AmountFragment : Fragment() {
    private lateinit var binding: FragmentAmountBinding
      lateinit var sharedPreferences: SharedPreferences
      lateinit var editor:SharedPreferences.Editor
      var txntype=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAmountBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
        editor=sharedPreferences.edit()
        txntype= sharedPreferences.getString("TXN_TYPE","").toString()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.currencyLabel.text = sharedPreferences.getString("Currency", "")


        attachKeyboardListener()
    }

    private fun attachKeyboardListener() {
        binding.keyboardLayout.setOnInteractionListener(onKeyValue = {
            binding.amount.text = CurrencyConverter.convertWithoutSAR(it.toLong())

        }, onConfirm = { amount, _ ->
            if (amount > 0) {
                val logAmt = binding.amount.text
                FirebaseDatabaseSingleton.setLog("New Transaction Amount - $logAmt")
                Log.d("MyTag", "New Transaction Amount - $logAmt")

                Log.d("Fragment", "transaction $txntype")

                if((txntype==TxnType.PURCHASE)||(txntype==TxnType.CASH_ADVANCE)||(txntype==TxnType.PRE_AUTH)) {
                    CardReadActivity.startTransaction(requireContext(), amount)
                    fragmentManager?.beginTransaction()?.remove(this)?.commit()


                }else if(txntype==TxnType.M_PURCHASE){

                     val intent=Intent(requireContext(),ManualCardEntry::class.java)
                    val transData=TransData(requireContext())
                    intent.putExtra("amount",transData.fillGapSequence(amount.toString(),12))
                    startActivity(intent)
                    fragmentManager?.beginTransaction()?.remove(this)?.commit()

                }else if(txntype==TxnType.REFUND||txntype==TxnType.PRE_AUTH_COMPLETION){
                    val transData=TransData(requireContext())
                    val readableOldAmount=getReadableAmount(TransData.RequestFields.Field04)
                    val amt=transData.fillGapSequence(amount.toString(),12)
                    val amount1=getReadableAmount(amt)
                    Log.d("MyTag", "amount1$amount1")
                    Log.d("MyTag", "readableOldAmount$readableOldAmount")

                    if(amount1.toDouble() < readableOldAmount.toDouble()){
                        val intent =Intent(requireContext(), SupervisorLogin::class.java)

                        intent.putExtra("amount",amount)

                         TransData.RequestFields.Field04=transData.fillGapSequence(amount.toString(),12)

                            startActivity(intent)

                    }else{
                        Toast.makeText(requireContext(),"Refund amount must be less than $readableOldAmount ",Toast.LENGTH_SHORT).show()
                    }

                }else if(txntype==TxnType.M_REFUND){

                    val transData=TransData(requireContext())
                    val readableOldAmount=getReadableAmount(TransData.RequestFields.Field04)
                    val amt=transData.fillGapSequence(amount.toString(),12)
                    val amount1=getReadableAmount(amt)
                    Log.d("MyTag", "amount1$amount1")
                    Log.d("MyTag", "readableOldAmount$readableOldAmount")

                    if(amount1.toDouble() < readableOldAmount.toDouble()){
                        val intent =Intent(requireContext(), ManualRefundActivity::class.java)

                        //intent.putExtra("amount",amount)

                        TransData.RequestFields.Field04=transData.fillGapSequence(amount.toString(),12)
                        startActivity(intent)
                        fragmentManager?.beginTransaction()?.remove(this)?.commit()


                    }else{
                        Toast.makeText(requireContext(),"Refund amount must be less than $readableOldAmount ",Toast.LENGTH_SHORT).show()
                    }

                    }
            }
        })
    }
    private fun getReadableAmount(amount: String?): String {
        return if (amount.isNullOrEmpty()) {
            "0.00"
        } else {
            val decimalFormat=DecimalFormat("0.00")
            decimalFormat.format(amount.toDouble() / 100.0)
        }
    }

    override fun onStop() {
        super.onStop()
        binding.amount.text = "0.00"
        binding.keyboardLayout.resetValues()
    }
}

