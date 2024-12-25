package net.geidea.payment.customviews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import net.geidea.payment.DBHandler
import net.geidea.payment.R
import net.geidea.payment.sign.SignDBHelper
import net.geidea.payment.transaction.model.TransData
import net.geidea.utils.CurrencyConverter
import net.geidea.utils.DateTimeFormat
import net.geidea.utils.convertDateTime
import net.geidea.utils.formatExpiryDate
import net.geidea.utils.maskPan


class ReceiptView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Regular paint for normal text
    private val paint = Paint().apply {
        color = Color.BLACK
        textSize = 24f
        isAntiAlias = true

    }

    // Bold paint for emphasizing certain text
    private val boldPaint = Paint().apply {
        color = Color.BLACK
        textSize = 24f
        isFakeBoldText = true  // Makes the text bold
        isAntiAlias = true
    }
    private val sharedPreferences=context.getSharedPreferences("SHARED_DATA",Context.MODE_PRIVATE)
    private lateinit var transData: TransData
    private lateinit var dbHandler: DBHandler
    private lateinit var signData: SignDBHelper

    // Initialize TransData and DBHandler through a function
    fun initializeData(transData: TransData, dbHandler: DBHandler, signData: SignDBHelper) {
        this.transData = transData
        this.dbHandler = dbHandler
        this.signData =  signData
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.LTGRAY)

        var yPos = 50
        // Draw Logo
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.nib_logo)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 400, 150, false)

        // Center the bitmap on the canvas
        val centerX = (width - scaledBitmap.width) / 2f
        val logoYPos = 50f // Set the Y position where you want to place the logo

        // Draw the bitmap at the centered position
        canvas.drawBitmap(scaledBitmap, centerX, logoYPos, null)
        yPos = (logoYPos + scaledBitmap.height + 20).toInt() // Update yPos based on the height of the logo

        // Draw Header Text
        canvas.drawText("NIB BANK POS", 100f, yPos.toFloat(), boldPaint)
        yPos += 40

        // Draw TID and MID from DBHandler
        val tid = dbHandler.getTID() ?: "N/A"  // Get TID or fallback to "N/A"
        val mid = dbHandler.getMID() ?: "N/A"  // Get MID or fallback to "N/A"
        canvas.drawText("TID: $tid   MID: $mid", 100f, yPos.toFloat(), paint)
        yPos += 40


        // Date and Time formatting using convertDateTime
        val formattedDate = convertDateTime(
            transData.transactionReqDateTime,
            DateTimeFormat.DATE_TIME_PATTERN_TRANS_ONE,
            DateTimeFormat.DATE_PATTERN_DISPLAY_ONE
        ) ?: "N/A"

        val formattedTime = convertDateTime(
            transData.transactionReqDateTime,
            DateTimeFormat.DATE_TIME_PATTERN_TRANS_ONE,
            DateTimeFormat.TIME_PATTERN_DISPLAY
        ) ?: "N/A"

        // Draw Date and Time
        canvas.drawText("Date: $formattedDate", 100f, yPos.toFloat(), paint)
        yPos += 40
        canvas.drawText("Time: $formattedTime", 100f, yPos.toFloat(), paint)
        yPos += 50


        // Draw Transaction Type in uppercase
        canvas.drawText(transData.transactionType.uppercase(), 100f, yPos.toFloat(), paint)
        yPos += 40

        // Draw Card Label and Entry Mode
        canvas.drawText("${transData.cardLabelNameEng}(${transData.entryMode})", 100f, yPos.toFloat(), paint)
        yPos += 40

        // Draw Masked PAN
        val maskedPan = maskPan(transData.pan.substringBefore("F"))
        canvas.drawText(maskedPan, 100f, yPos.toFloat(), paint)
        yPos += 40

        // Draw Expiry Date
        val formattedExpiryDate = formatExpiryDate(transData.cardExpiryDate)
        canvas.drawText("EXPIRY DATE : $formattedExpiryDate", 100f, yPos.toFloat(), paint)
        yPos += 40

        // Draw Amount with currency
        val currency = sharedPreferences.getString("Currency", "") ?: ""
        val amount = CurrencyConverter.convertWithoutSAR(transData.amount)
        canvas.drawText("AMOUNT: $currency $amount", 100f, yPos.toFloat(), boldPaint) // Draw amount in bold
        yPos += 40

        // Draw RRN (Field37) and Receipt No (STAN)
        canvas.drawText("RRN: ${TransData.ResponseFields.Field37 ?: "N/A"}", 100f, yPos.toFloat(), paint)
        yPos += 40
        canvas.drawText("RECEIPT No: ${transData.stan}", 100f, yPos.toFloat(), paint)
        yPos += 40

        // Draw Sign
        canvas.drawText("Signature", 100f, yPos.toFloat(), paint)
        val approvalCode = TransData.ResponseFields.Field38
        val signatureBitmap = signData.getSignatureByApprovalCode(approvalCode)

        if (signatureBitmap != null) {
            canvas.drawBitmap(signatureBitmap, 50f, yPos.toFloat(), null)
            yPos += signatureBitmap.height + 20  // Adjust yPos based on the height of the signature
        } else {
            Log.e("Receipt", "No signature found for approval code: $approvalCode")
        }


        // Generate and draw the QR code
        canvas.drawText("===============================================", 100f, yPos.toFloat(), paint)
        val qrCodeContent = "https://www.nibbanksc.com/"
        val qrCodeBitmap = generateQRCode(qrCodeContent, 150, 150)  // Size of the QR code
        qrCodeBitmap?.let {
            canvas.drawBitmap(it, 100f, yPos.toFloat(), null)  // Draw QR code below the receipt details
            yPos += it.height + 20  // Adjust yPos based on the height of the QR code
        }




        // Draw Footer
        canvas.drawText("===============================================", 100f, yPos.toFloat(), paint)
        canvas.drawText("Thank You!", 100f, yPos.toFloat(), paint)
        canvas.drawText("===============================================", 100f, yPos.toFloat(), paint)
        canvas.drawText("NIB Bank POS", 100f, yPos.toFloat(), paint)
        canvas.drawText("Powered By SSC.", 100f, yPos.toFloat(), paint)
    }

    fun setApprovalCode(approvalCode: Any) {
        // Retrieve the signature bitmap using the approval code
        var signatureBitmap = signData.getSignatureByApprovalCode(approvalCode)
        invalidate() // Redraw the view
    }


    // Method to generate a QR code bitmap
     fun generateQRCode(content: String, width: Int, height: Int): Bitmap? {
        return try {
            val qrCodeWriter = QRCodeWriter()
            val bitMatrix: BitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height)
            val qrCodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    qrCodeBitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            qrCodeBitmap
        } catch (e: Exception) {
            Log.e("ReceiptView", "Error generating QR Code: ${e.message}")
            null
        }
     }
}
