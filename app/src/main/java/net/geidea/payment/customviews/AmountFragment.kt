package net.geidea.payment.customviews

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.geidea.payment.databinding.FragmentAmountBinding
import net.geidea.payment.transaction.view.CardReadActivity
import net.geidea.payment.utils.FirebaseDatabaseSingleton
import net.geidea.payment.utils.FirebaseDatabaseSingleton.getCurrentTime
import net.geidea.utils.CurrencyConverter
import net.geidea.payment.Txntype
import net.geidea.payment.transaction.ManualCardEntry
import net.geidea.payment.transaction.model.TransData
//cfgjk
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
        txntype= sharedPreferences.getString("transaction","").toString()

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

                if(txntype==Txntype.purchase) {
                     //editor.putString("transaction","")
                     //editor.commit()
                    CardReadActivity.startTransaction(requireContext(), amount)
                    fragmentManager?.beginTransaction()?.remove(this)?.commit()


                }else if(txntype==Txntype.manualCardEntry){

                     val intent=Intent(requireContext(),ManualCardEntry::class.java)
                    val transData=TransData(requireContext())
                    intent.putExtra("amount",transData.fillGapSequence(amount.toString(),12))
                    startActivity(intent)
                    fragmentManager?.beginTransaction()?.remove(this)?.commit()

                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        binding.amount.text = "0.00"
        binding.keyboardLayout.resetValues()
    }
}

