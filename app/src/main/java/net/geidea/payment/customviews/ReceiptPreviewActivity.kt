package net.geidea.payment.customviews

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextPaint
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import net.geidea.payment.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.pdf.PdfRenderer
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

class ReceiptPreviewActivity : AppCompatActivity() {

    private lateinit var receiptContainer: LinearLayout
    private lateinit var pdfFile: File

    // Sample receipt data
    private val receiptData = ReceiptData(
        header = "Hibret Bank POS\\nAddis, Ababa 123 Main Street\\nPhone: 555-1234",
        items = listOf(
            Item("Item 1", "10.00"),
            Item("Item 2", "15.00"),
            Item("Item 3", "10.00"),
            Item("Item 4", "15.00"),
            Item("Item 5", "10.00"),
            Item("Item 6", "15.00"),
            Item("Item 7", "10.00"),
            Item("Item 8", "15.00"),
            Item("Item 9", "10.00"),
            Item("Item 10", "15.00"),
            Item("Item 11", "10.00"),
            Item("Item 12", "15.00"),
            Item("Item 13", "10.00"),
            Item("Item 14", "15.00"),
            Item("Item 15", "10.00"),
            Item("Item 16", "15.00"),
            Item("Item 17", "10.00"),
            Item("Item 18", "15.00"),
            Item("Item 19", "10.00"),
            Item("Item 20", "15.00"),
        ),
        total = "300.00"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_receipt)

        receiptContainer = findViewById(R.id.receiptContainer)
        setupReceiptPreview()

        setupPdfButton()
    }

    private fun setupReceiptPreview() {
        // Clear existing items
        val startIndex = 2 // After header and separator
        val endIndex = receiptContainer.childCount - 3 // Before total, separator, and button
        receiptContainer.removeViews(startIndex, endIndex - startIndex)

        // Add items
        receiptData.items.forEach { item ->
            addReceiptItemView(item)

        }

        // Update total
        findViewById<TextView>(R.id.tvTotal).text =
            "TOTAL: $${receiptData.total}"



    }

    private fun addReceiptItemView(item: Item) {
        TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 4.dpToPx(), 0, 4.dpToPx())
            }
            text = "${item.name.padEnd(20)}$${item.price.padStart(6)}"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            typeface = android.graphics.Typeface.MONOSPACE
        }.also { receiptContainer.addView(it, receiptContainer.childCount - 3) }
    }

    private fun setupPdfButton() {
        findViewById<TextView>(R.id.btnPrintReceipt).setOnClickListener {
            generatePdf()
            openPdf()
        }
    }

    private fun generatePdf() {
        val document = PdfDocument()
        val pageInfo = createPageInfo()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = createPdfPaint()

        var yPos = drawHeader(canvas, paint)
        yPos = drawItems(canvas, paint, yPos)
        drawTotal(canvas, paint, yPos)

        // QR Code content
        val qrCodeContent = "https://www.nibbanksc.com/"
        val qrCodeBitmap = generateQRCode(qrCodeContent, 150, 150)  // Size of the QR code

        // Log QR code generation to ensure it's working
        if (qrCodeBitmap != null) {
            Log.d("ReceiptPreviewActivity", "QR Code generated successfully")
        } else {
            Log.e("ReceiptPreviewActivity", "Error generating QR Code")
        }

        qrCodeBitmap?.let {
            // Draw QR code just below the total
            canvas.drawBitmap(it, 50f, yPos.toFloat(), null)
            yPos += it.height + 20  // Adjust yPos after QR code

            Log.d("ReceiptPreviewActivity", "QR Code drawn at yPos: $yPos")
        }

        document.finishPage(page)
        saveDocument(document)
    }

    private fun createPageInfo(): PdfDocument.PageInfo {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return PdfDocument.PageInfo.Builder(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            1
        ).create()
    }

    private fun createPdfPaint(): TextPaint {
        return TextPaint().apply {
            color = android.graphics.Color.BLACK
            textSize = 14f * resources.displayMetrics.density
            typeface = android.graphics.Typeface.MONOSPACE
        }
    }


    private fun drawHeader(canvas: android.graphics.Canvas, paint: TextPaint): Float {
        var yPos = 30f
        receiptData.header.split("\n").forEach { line ->
            canvas.drawText(line, 30f, yPos, paint)
            yPos += paint.textSize * 1.2f
        }
        return yPos + 20f
    }

    private fun drawItems(canvas: android.graphics.Canvas, paint: TextPaint, yPos: Float): Float {
        var currentY = yPos
        receiptData.items.forEach { item ->
            val text = "${item.name.padEnd(20)}$${item.price.padStart(6)}"
            canvas.drawText(text, 30f, currentY, paint)
            currentY += paint.textSize * 1.2f
        }
        return currentY + 20f
    }

    private fun drawTotal(canvas: android.graphics.Canvas, paint: TextPaint, yPos: Float) {
        paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
        canvas.drawText("TOTAL: $${receiptData.total}", 30f, yPos, paint)

        // QR Code content
        val qrCodeContent = "https://www.nibbanksc.com/"
        val qrCodeBitmap = generateQRCode(qrCodeContent, 150, 150)  // Size of the QR code

        // Log QR code generation to ensure it's working
        if (qrCodeBitmap != null) {
            Log.d("ReceiptPreviewActivity", "QR Code generated successfully")
        } else {
            Log.e("ReceiptPreviewActivity", "Error generating QR Code")
        }

        qrCodeBitmap?.let {
            // Draw QR code just below the total
            canvas.drawBitmap(it, 30f, yPos.toFloat(), null)
            //yPos += it.height + 20  // Adjust yPos after QR code

            Log.d("ReceiptPreviewActivity", "QR Code drawn at yPos: $yPos")
        }

    }

    private fun saveDocument(document: PdfDocument) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Receipt_$timestamp.pdf"

        pdfFile = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        document.writeTo(FileOutputStream(pdfFile))
        document.close()
    }

    private fun openPdf() {
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            showToast("Printing..............")
        }
    }

    private fun Int.dpToPx(): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        resources.displayMetrics
    ).toInt()

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    data class ReceiptData(
        val header: String,
        val items: List<Item>,
        val total: String
    )

    data class Item(
        val name: String,
        val price: String
    )

    // Method to generate a QR code bitmap
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

            // Log QR Code creation details
            Log.d("ReceiptPreviewActivity", "QR Code created with dimensions: ${qrCodeBitmap.width}x${qrCodeBitmap.height}")

            qrCodeBitmap
        } catch (e: Exception) {
            Log.e("ReceiptPreviewActivity", "Error generating QR Code: ${e.message}")
            null
        }
    }



}