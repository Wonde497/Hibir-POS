package net.geidea.payment.customviews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
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

import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream

class CustomReceiptView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    var receiptText: String = ""

    // Generate PDF receipt
    fun generateReceiptPdf(receiptText: String, filePath: String) {
        val document = PdfDocument()

        // Paint for drawing
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
        }

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val x = 50f
        var y = 50f

        val lines = receiptText.split("\n")
        for (line in lines) {
            canvas.drawText(line, x, y, paint)
            y += 20f
        }

        document.finishPage(page)
        val file = File(filePath)
        document.writeTo(FileOutputStream(file))
        document.close()
    }

    // Preview the PDF receipt as an Image
    fun previewReceiptPdf(filePath: String, imageView: ImageView) {
        val file = File(filePath)
        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)

        if (pdfRenderer.pageCount > 0) {
            val page = pdfRenderer.openPage(0) // Open the first page
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

            // Render page to bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // Set bitmap to ImageView
            imageView.setImageBitmap(bitmap)
            page.close()
        }

        pdfRenderer.close()
        fileDescriptor.close()
    }

    // Draw receipt preview (for view only)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
        }
        var y = 50f
        val lines = receiptText.split("\n")
        for (line in lines) {
            canvas.drawText(line, 50f, y, paint)
            y += 20f
        }
    }
}