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

class CustomReceiptView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.BLACK
        textSize = 24f
        isAntiAlias = true
        textAlign = Paint.Align.LEFT
    }

    private val boldPaint = Paint().apply {
        color = Color.BLACK
        textSize = 24f
        isFakeBoldText = true
        isAntiAlias = true
        textAlign = Paint.Align.LEFT
    }

    private val centerPaint = Paint().apply {
        color = Color.BLACK
        textSize = 24f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private val sharedPreferences = context.getSharedPreferences("SHARED_DATA", Context.MODE_PRIVATE)
    private lateinit var transData: TransData
    private lateinit var dbHandler: DBHandler
    private lateinit var signData: SignDBHelper

    fun initializeData(transData: TransData, dbHandler: DBHandler, signData: SignDBHelper) {
        this.transData = transData
        this.dbHandler = dbHandler
        this.signData = signData
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE) // White background for a clean look

        var yPos = 50f // Start drawing from the top

        // Draw Logo (centered)
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.hb_logo1)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 400, 150, false)
        val centerX = width / 2f
        canvas.drawBitmap(scaledBitmap, centerX - scaledBitmap.width / 2, yPos, null)
        yPos += scaledBitmap.height + 20

        // Draw Header Text (centered)
        canvas.drawText("HIBRET BANK POS", centerX, yPos, centerPaint)
        yPos += 40

        // Draw TID and MID (left-aligned)
        val tid = dbHandler.getTID() ?: "N/A"
        val mid = dbHandler.getMID() ?: "N/A"
        canvas.drawText("TID: $tid   MID: $mid", 50f, yPos, paint)
        yPos += 40

        // Draw Date and Time (left-aligned)
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

        canvas.drawText("Date: $formattedDate", 50f, yPos, paint)
        yPos += 40
        canvas.drawText("Time: $formattedTime", 50f, yPos, paint)
        yPos += 50

        // Draw Transaction Type (centered)
        canvas.drawText(transData.transactionType.uppercase(), centerX, yPos, centerPaint)
        yPos += 40

        // Draw Card Label and Entry Mode (left-aligned)
        canvas.drawText("${transData.cardLabelNameEng} (${transData.entryMode})", 50f, yPos, paint)
        yPos += 40

        // Draw Masked PAN (left-aligned)
        val maskedPan = maskPan(transData.pan.substringBefore("F"))
        canvas.drawText(maskedPan, 50f, yPos, paint)
        yPos += 40

        // Draw Expiry Date (left-aligned)
        val formattedExpiryDate = formatExpiryDate(transData.cardExpiryDate)
        canvas.drawText("EXPIRY DATE: $formattedExpiryDate", 50f, yPos, paint)
        yPos += 40

        // Draw Amount with Currency (left-aligned, bold)
        val currency = sharedPreferences.getString("Currency", "") ?: ""
        val amount = CurrencyConverter.convertWithoutSAR(transData.amount)
        canvas.drawText("AMOUNT: $currency $amount", 50f, yPos, boldPaint)
        yPos += 40

        // Draw RRN and Receipt No (left-aligned)
        canvas.drawText("RRN: ${TransData.ResponseFields.Field37 ?: "N/A"}", 50f, yPos, paint)
        yPos += 40
        canvas.drawText("RECEIPT No: ${transData.stan}", 50f, yPos, paint)
        yPos += 40

        // Draw Signature Label (left-aligned)
        canvas.drawText("Signature", 50f, yPos, paint)
        val approvalCode = TransData.ResponseFields.Field38
        val signatureBitmap = signData.getSignatureByApprovalCode(approvalCode)

        if (signatureBitmap != null) {
            canvas.drawBitmap(signatureBitmap, 50f, yPos + 20, null)
            yPos += signatureBitmap.height + 40
        } else {
            Log.e("Receipt", "No signature found for approval code: $approvalCode")
            yPos += 40
        }

        // Draw Separator Line (centered)
        canvas.drawText("===============================================", centerX, yPos, centerPaint)
        yPos += 40

        // Draw QR Code (centered)
        val qrCodeContent = "https://www.hibret.com/"
        val qrCodeBitmap = generateQRCode(qrCodeContent, 150, 150)

        qrCodeBitmap?.let {
            canvas.drawBitmap(it, centerX - it.width / 2, yPos, null)
            yPos += it.height + 20
        }

        // Draw Footer (centered)
        canvas.drawText("===============================================", centerX, yPos, centerPaint)
        yPos += 40
        canvas.drawText("Thank You!", centerX, yPos, centerPaint)
        yPos += 40
        canvas.drawText("===============================================", centerX, yPos, centerPaint)
        yPos += 40
        canvas.drawText("NIB Bank POS", centerX, yPos, centerPaint)
        yPos += 40
        canvas.drawText("Powered By SSC.", centerX, yPos, centerPaint)
    }

    fun setApprovalCode(approvalCode: Any) {
        signData.getSignatureByApprovalCode(approvalCode)
        invalidate()
    }

    private fun generateQRCode(content: String, width: Int, height: Int): Bitmap? {
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