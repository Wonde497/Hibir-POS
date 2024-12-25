package net.geidea.payment.sign

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log

class SignDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "signatureDB.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "Signatures"
        private const val COLUMN_ID = "id"
        private const val COLUMN_SIGNATURE = "signature"
        private const val COLUMN_APPROVAL_CODE = "approval_code"  // Column for approval code
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create table with an additional column for approval code
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SIGNATURE BLOB,
                $COLUMN_APPROVAL_CODE TEXT UNIQUE
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert signature with approval code
    fun insertSignature(signature: ByteArray, approvalCode: Any) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_SIGNATURE, signature)
            put(COLUMN_APPROVAL_CODE, approvalCode.toString())  // Convert to string for storage
        }
        db.insert(TABLE_NAME, null, contentValues)
        db.close()  // Close database connection
    }

    // Retrieve signature by approval code
    fun getSignatureByApprovalCode(approvalCode: Any): Bitmap? {
        val db = this.readableDatabase
        var signatureBitmap: Bitmap? = null
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_SIGNATURE),  // Ensure this column is included
            "$COLUMN_APPROVAL_CODE = ?",
            arrayOf(approvalCode.toString()),  // Convert to string for query
            null, null, null
        )

        // Check if cursor is not null and has at least one result
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN_SIGNATURE)

            // Check if columnIndex is valid
            if (columnIndex != -1) {
                val signatureBlob = cursor.getBlob(columnIndex)
                signatureBitmap = BitmapFactory.decodeByteArray(signatureBlob, 0, signatureBlob.size)
            } else {
                // Log or handle the error
                Log.e("SignDBHelper", "Column not found: $COLUMN_SIGNATURE")
            }
        } else {
            Log.e("SignDBHelper", "Cursor is null or empty")
        }

        cursor.close()
        db.close()
        return signatureBitmap
    }

}
