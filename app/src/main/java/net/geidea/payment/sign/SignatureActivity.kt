package net.geidea.payment.sign

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap.CompressFormat
import android.widget.TextView
import net.geidea.payment.R
import net.geidea.payment.customviews.PrintTest2
import net.geidea.payment.transaction.model.TransData
import net.geidea.utils.CurrencyConverter

class SignatureActivity : AppCompatActivity() {

    private lateinit var signatureView: SignatureView
    private lateinit var confirmButton: Button
    private lateinit var clearButton: Button
    private lateinit var amountText: TextView
    private lateinit var signDBHelper: SignDBHelper
    private lateinit var transData: TransData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esign)

        signatureView = findViewById(R.id.signature_canvas)
        confirmButton = findViewById(R.id.confirm_signature_button)
        clearButton = findViewById(R.id.clear_signature_button)
        amountText = findViewById(R.id.amount_display)
        signDBHelper = SignDBHelper(this)
        transData = TransData(this)

        val amount = CurrencyConverter.convertWithoutSAR(transData.amount)
        amountText.setText("ETB "+amount)

        confirmButton.setOnClickListener {
            if (signatureView.isSigned()) {
                val signatureBitmap = signatureView.getSignatureBitmap()
                val approvalCode = TransData.ResponseFields.Field38  // Retrieve the approval code
                saveSignatureToDatabase(signatureBitmap, approvalCode)
                Toast.makeText(this, "Signed", Toast.LENGTH_SHORT).show()

                // Start the PrintTest2 activity
                startActivity(Intent(this, PrintTest2::class.java))
            } else {
                Toast.makeText(this, "Please provide your signature", Toast.LENGTH_SHORT).show()
            }
        }

        clearButton.setOnClickListener {
            if (signatureView.isSigned()) {
                signatureView.clear()
            } else {
                Toast.makeText(this, "Please provide your signature", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveSignatureToDatabase(bitmap: Bitmap, approvalCode: Any) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream)
        val signatureByteArray = byteArrayOutputStream.toByteArray()

        signDBHelper.insertSignature(signatureByteArray, approvalCode)  // Pass the approval code
    }
}
