package net.geidea.payment.customviews

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import net.geidea.payment.DBHandler
import net.geidea.payment.MainActivity
import net.geidea.payment.R
import net.geidea.payment.customviews.ReceiptView
import net.geidea.payment.sign.SignDBHelper
import net.geidea.payment.transaction.model.TransData
import net.geidea.payment.transaction.viewmodel.CardReadViewModel

class PrintTest2 : AppCompatActivity() {

    private lateinit var transactionStatusImage: AppCompatImageView
    private lateinit var transactionStatus: TextView
    private lateinit var approvalCode: TextView
    private lateinit var receiptView: ReceiptView
    private lateinit var startNewTransactionButton: Button
    private lateinit var printReceiptButton: Button
    private lateinit var  transData: TransData
    private lateinit var dbHandler: DBHandler
    private lateinit var signData: SignDBHelper
    private lateinit var cardReadViewModel: CardReadViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_transaction_status_2)  // Replace with the correct XML layout name if needed

        transData  = TransData(this)
        dbHandler = DBHandler(this)
        signData =  SignDBHelper(this)
        cardReadViewModel = CardReadViewModel(this)

        // Initialize UI elements
        transactionStatusImage = findViewById(R.id.transaction_status_image)
        transactionStatus = findViewById(R.id.transaction_status)
        approvalCode = findViewById(R.id.tvApprovalCode)
        receiptView = findViewById(R.id.receiptView) as ReceiptView
        startNewTransactionButton = findViewById(R.id.start_new_transaction)
        printReceiptButton = findViewById(R.id.print_receipt)

        // Set initial data for UI components
       // setupInitialData()

       receiptView.initializeData(transData, dbHandler, signData)

        // Retrieve the approval code
        val approvalCode = TransData.ResponseFields.Field38

        // Set the approval code to retrieve and draw the signature
        receiptView.setApprovalCode(approvalCode)
      // receiptView.setDBHandler(dbHandler)
        // Set button listeners
        startNewTransactionButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))

        }

        printReceiptButton.setOnClickListener {
           cardReadViewModel.printReceipt()
        }
    }
}
