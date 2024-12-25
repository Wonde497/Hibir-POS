package net.geidea.payment

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import net.geidea.payment.usersData.User

// kaleb test commit
class DBHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object {

        private const val DATABASE_NAME = "UsersDatabase.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_ADMIN ="admintable"
        const val TABLE_SUPPORT="Support"
        const val TABLE_SUPER="super"
        const val TABLE_CASHIER="cashier"
        private const val TABLE_TID ="terminalID"
        private const val TABLE_MID ="merchantID"
        private const val TABLE_MERCHANT_NAME ="merchantName"
        private const val TABLE_MERCHANT_ADDRESS ="merchantAddress"
        private const val TABLE_TXN_DATA ="txn_data_table"
        private const val TABLE_COM_CONFIG ="comConfigTable"
        //private const val TABLE_ADMIN ="admintable"
        //private const val TABLE_ADMIN ="admintable"
        //private const val TABLE_ADMIN ="admintable"
        //private const val TABLE_ADMIN ="admintable"
         const val  TXN_TYPE ="txntype"
        private const val COLUMN_ID ="id"
        private const val COLUMN_USERNAME ="username"
        private const val COLUMN_PASSWORD ="password"
         private const val COLUMN_TID ="TID"
        private const val COLUMN_MID ="MID"
        private const val COLUMN_MERCHANT_NAME ="merchantName"
        private const val COLUMN_MERCHANT_ADDRESS ="merchantAddress"
        private const val COLUMN_MTI ="mti"
        private const val COLUMN_BITMAP ="bitmap"
        private const val COLUMN_PAN ="pan"
        public const val COLUMN_FIELD02 ="field02"
        private const val COLUMN_FIELD03 ="field03"
        const val COLUMN_FIELD04 ="field04"

        const val COLUMN_FIELD11 ="field11"
        const val COLUMN_FIELD12 ="field12"
        const val COLUMN_FIELD13 ="field13"
        private const val COLUMN_FIELD14 ="field14"
        private const val COLUMN_FIELD22 ="field22"
        private const val COLUMN_FIELD24 ="field24"
        private const val COLUMN_FIELD25 ="field25"
        private const val COLUMN_FIELD35 ="field35"
        const val COLUMN_FIELD37 ="field37"
        const val COLUMN_FIELD38 ="field38"
        const val COLUMN_FIELD39 ="field39"
        private const val COLUMN_FIELD41 ="field41"
        private const val COLUMN_FIELD42 ="field42"
        private const val COLUMN_FIELD49 ="field49"
        private const val COLUMN_FIELD52 ="field52"
        const val COLUMN_FIELD60 ="field60"
        private const val COLUMN_IP ="ip"
        private const val COLUMN_PORT_NO ="portNumber"
        //Queries to create table
        private const val CREATE_ADMIN =
            "CREATE TABLE $TABLE_ADMIN (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_USERNAME TEXT," +
                    "$COLUMN_PASSWORD TEXT)"

        private const val CREATE_SUPER =
            "CREATE TABLE $TABLE_SUPER (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_USERNAME TEXT," +
                    "$COLUMN_PASSWORD TEXT)"

        private const val CREATE_CASHIER =
            "CREATE TABLE $TABLE_CASHIER (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_USERNAME TEXT," +
                    "$COLUMN_PASSWORD TEXT)"

        private const val CREATE_TID =
            "CREATE TABLE $TABLE_TID (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_TID TEXT)"

        private const val CREATE_MID =
            "CREATE TABLE $TABLE_MID (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_MID TEXT)"

        private const val CREATE_MERCHANT_NAME =
            "CREATE TABLE $TABLE_MERCHANT_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_MERCHANT_NAME TEXT)"

        private const val CREATE_MERCHANT_ADDRESS =
            "CREATE TABLE $TABLE_MERCHANT_ADDRESS (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +

                    "$COLUMN_MERCHANT_ADDRESS TEXT)"
        private const val CREATE_TXN_DATA =
            "CREATE TABLE $TABLE_TXN_DATA (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$TXN_TYPE TEXT,"+
                    "$COLUMN_MTI TEXT,"+
                    "$COLUMN_BITMAP TEXT,"+
                    "$COLUMN_FIELD02 TEXT,"+
                    "$COLUMN_FIELD03 TEXT,"+
                    "$COLUMN_FIELD04 TEXT,"+

                    "$COLUMN_FIELD11 TEXT,"+
                    "$COLUMN_FIELD12 TEXT,"+
                    "$COLUMN_FIELD13 TEXT,"+
                    "$COLUMN_FIELD14 TEXT,"+
                    "$COLUMN_FIELD22 TEXT,"+
                    "$COLUMN_FIELD24 TEXT,"+
                    "$COLUMN_FIELD25 TEXT,"+
                    "$COLUMN_FIELD35 TEXT,"+
                    "$COLUMN_FIELD37 TEXT,"+
                    "$COLUMN_FIELD38 TEXT,"+
                    "$COLUMN_FIELD39 TEXT,"+
                    "$COLUMN_FIELD41 TEXT,"+
                    "$COLUMN_FIELD42 TEXT,"+
                    "$COLUMN_FIELD49 TEXT,"+
                    "$COLUMN_FIELD52 TEXT,"+
                    "$COLUMN_FIELD60 TEXT)"
        private const val CREATE_COM_CONFIG =
            "CREATE TABLE $TABLE_COM_CONFIG (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_IP TEXT," +
                    "$COLUMN_PORT_NO TEXT)"
        private const val CREATE_SUPPORT =
            "CREATE TABLE $TABLE_SUPPORT (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY," +
                    "$COLUMN_USERNAME TEXT," +
                    "$COLUMN_PASSWORD TEXT)"

        //Queries to delete table
        private const val DELETE_ADMIN = "DROP TABLE IF EXISTS $TABLE_ADMIN"
        private const val DELETE_TID = "DROP TABLE IF EXISTS $TABLE_TID"
        private const val DELETE_MID = "DROP TABLE IF EXISTS $TABLE_MID"
        private const val DELETE_MERCHANT_NAME = "DROP TABLE IF EXISTS $TABLE_MERCHANT_NAME"
        private const val DELETE_MERCHANT_ADDRESS = "DROP TABLE IF EXISTS $TABLE_MERCHANT_ADDRESS"
        private const val DELETE_TXN_DATA = "DROP TABLE IF EXISTS $TABLE_TXN_DATA"
        private const val DELETE_COM_CONFIG = "DROP TABLE IF EXISTS $TABLE_COM_CONFIG"
        private const val DELETE_SUPPORT = "DROP TABLE IF EXISTS $TABLE_SUPPORT"
        private const val DELETE_SUPER = "DROP TABLE IF EXISTS $TABLE_SUPER"
        private const val DELETE_CASHIER = "DROP TABLE IF EXISTS $TABLE_CASHIER"

    }
    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL(CREATE_ADMIN)
            db.execSQL(CREATE_SUPER)
            db.execSQL(CREATE_CASHIER)
            db.execSQL(CREATE_TID)
            db.execSQL(CREATE_MID)
            db.execSQL(CREATE_MERCHANT_NAME)
            db.execSQL(CREATE_MERCHANT_ADDRESS)
            db.execSQL(CREATE_TXN_DATA)
            db.execSQL(CREATE_COM_CONFIG)
            db.execSQL(CREATE_SUPPORT)

        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        if (db != null) {
            db.execSQL(DELETE_ADMIN)
            db.execSQL(DELETE_SUPER)
            db.execSQL(DELETE_CASHIER)
            db.execSQL(DELETE_TID)
            db.execSQL(DELETE_MID)
            db.execSQL(DELETE_MERCHANT_NAME)
            db.execSQL(DELETE_MERCHANT_ADDRESS)
            db.execSQL(DELETE_TXN_DATA)
            db.execSQL(DELETE_COM_CONFIG)
            db.execSQL(DELETE_SUPPORT)


        }
        onCreate(db)
    }
    fun registerAdmin(userName:String,password:String){
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, userName)
            put(COLUMN_PASSWORD, password)
        }

        db.insert(TABLE_ADMIN, null, values)
        db.close()
    }
    fun registerSupport(supportName:String,passwrd:String){
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, supportName)
            put(COLUMN_PASSWORD, passwrd)
        }

        db.insert(TABLE_SUPPORT,null, values)
        db.close()
    }
    // Check if a username already exists in the Support table
    fun isSupportUsernameExists(username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_SUPPORT WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))

        if (cursor != null) {
            cursor.moveToFirst()
            val count = cursor.getInt(0)
            cursor.close()
            return count > 0
        }
        return false
    }

    fun registerSuper(userName:String,password:String){
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, userName)
            put(COLUMN_PASSWORD, password)
        }

        db.insert(TABLE_SUPER, null, values)
        db.close()
    }

    // Check if a username already exists in the Support table
    fun isSupervisorUsernameExists(username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_SUPER WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))

        if (cursor != null) {
            cursor.moveToFirst()
            val count = cursor.getInt(0)
            cursor.close()
            return count > 0
        }
        return false
    }

    fun registerCashier(supportName:String,passwrd:String){
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, supportName)
            put(COLUMN_PASSWORD, passwrd)
        }

        db.insert(TABLE_CASHIER,null, values)
        db.close()
    }
    // Check if a username already exists in the Support table
    fun isCashierUsernameExists(username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_CASHIER WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))

        if (cursor != null) {
            cursor.moveToFirst()
            val count = cursor.getInt(0)
            cursor.close()
            return count > 0
        }
        return false
    }

    fun viewUser(tableName: String, userType: String): List<User> {
        val userList = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $tableName ORDER BY $COLUMN_USERNAME ASC", null)

        cursor?.use {
            val usernameIndex = it.getColumnIndex(COLUMN_USERNAME)
            val passwordIndex = it.getColumnIndex(COLUMN_PASSWORD)

            if (usernameIndex != -1 && passwordIndex != -1) {
                while (it.moveToNext()) {
                    val username = it.getString(usernameIndex)
                    val password = it.getString(passwordIndex)
                    userList.add(User(username, userType, password))
                }
            } else {

            }
        }

        db.close()
        return userList
    }

    // Method to delete a user by username
    fun deleteUser(tableName: String, username: String): Boolean {
        val db = this.writableDatabase
        val result: Int

        // Prepare the where clause for deletion
        val whereClause = "$COLUMN_USERNAME = ?"
        val whereArgs = arrayOf(username)

        // Execute the delete operation
        result = db.delete(tableName, whereClause, whereArgs)

        db.close()
        // Return true if a user was deleted, false otherwise
        return result > 0
    }



    fun registerTID(terminalID:String){
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TID, terminalID)
        }
        db.insert(TABLE_TID, null, values)
        db.close()
    }
    fun updateTID(terminalID: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TID, terminalID)
        }
        // Define the selection criteria
        val selection = "$COLUMN_TID = ?"
        val selectionArgs = arrayOf(terminalID)

        // Update the database
        val count = db.update(
            TABLE_TID,
            values,
            selection,
            selectionArgs
        )

        // If no rows were updated, insert the new value
        if (count == 0) {
            db.insert(TABLE_TID, null, values)
        }

        db.close()
    }
    fun registerMID(merchantID:String){
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MID, merchantID)
        }
        db.insert(TABLE_MID, null, values)
        db.close()
    }
    fun updateMID(merchantID: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MID, merchantID)
        }
        // Define the selection criteria
        val selection = "$COLUMN_MID = ?"
        val selectionArgs = arrayOf(merchantID)

        // Update the database
        val count = db.update(
            TABLE_MID,
            values,
            selection,
            selectionArgs
        )

        // If no rows were updated, insert the new value
        if (count == 0) {
            db.insert(TABLE_MID, null, values)
        }

        db.close()
    }
    fun registerMerchantName(merchantName:String){
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MERCHANT_NAME, merchantName)
        }
        db.insert(TABLE_MERCHANT_NAME, null, values)
        db.close()
    }
    fun registerMerchantAddress(merchantAddress:String){
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MERCHANT_ADDRESS, merchantAddress)
        }
        db.insert(TABLE_MERCHANT_ADDRESS, null, values)
        db.close()
    }
    fun registerTxnData(txntype:String,mti:String,bitmap:String,field02:String,
                        field03:String,field04:String,
                        field11:String, field12:String,field13:String,field14:String,
                        field22:String,field24:String,field25:String,
                        field35:String,field37:String,field38:String,
                        field39:String, field41:String,field42:String,
                        field49:String,field52:String,field55:String){
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(TXN_TYPE, txntype)
            put(COLUMN_MTI, mti)
            put(COLUMN_BITMAP,bitmap)
            put(COLUMN_FIELD02, field02)
            put(COLUMN_FIELD03, field03)
            put(COLUMN_FIELD04, field04)

            put(COLUMN_FIELD11, field11)
            put(COLUMN_FIELD12, field12)
            put(COLUMN_FIELD13, field13)
            put(COLUMN_FIELD14, field14)
            put(COLUMN_FIELD22, field22)
            put(COLUMN_FIELD24, field24)
            put(COLUMN_FIELD25, field25)
            put(COLUMN_FIELD35, field35)
            put(COLUMN_FIELD37, field37)
            put(COLUMN_FIELD38, field38)
            put(COLUMN_FIELD39, field39)
            put(COLUMN_FIELD41, field41)
            put(COLUMN_FIELD42, field42)
            put(COLUMN_FIELD49, field49)
            put(COLUMN_FIELD52, field52)
            put(COLUMN_FIELD60, field55)
        }
        db.insert(TABLE_TXN_DATA, null, values)
        db.close()

    }
    fun deleteAllTxnData(){
        val db = this.writableDatabase
        db.delete(TABLE_TXN_DATA,null,null)
        db.close()



    }
    @SuppressLint("Range")
    fun getTxnData(): List<Map<String, String>> {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TXN_DATA,
            arrayOf(
                TXN_TYPE,COLUMN_MTI, COLUMN_BITMAP, COLUMN_FIELD02, COLUMN_FIELD03,
                COLUMN_FIELD04,  COLUMN_FIELD11, COLUMN_FIELD12,COLUMN_FIELD13,
                COLUMN_FIELD14, COLUMN_FIELD22, COLUMN_FIELD24, COLUMN_FIELD25,
                COLUMN_FIELD35, COLUMN_FIELD37, COLUMN_FIELD38, COLUMN_FIELD39,
                COLUMN_FIELD41, COLUMN_FIELD42, COLUMN_FIELD49, COLUMN_FIELD52,
                COLUMN_FIELD60
            ),
            null, null, null, null, null
        )

        val txnDataList = mutableListOf<Map<String, String>>()
        while (cursor.moveToNext()) {
            val txnData = mutableMapOf<String, String>()
            txnData[TXN_TYPE] = cursor.getString(cursor.getColumnIndex(TXN_TYPE))
            txnData[COLUMN_MTI] = cursor.getString(cursor.getColumnIndex(COLUMN_MTI))
            txnData[COLUMN_BITMAP] = cursor.getString(cursor.getColumnIndex(COLUMN_BITMAP))
            txnData[COLUMN_FIELD02] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD02))
            txnData[COLUMN_FIELD03] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD03))
            txnData[COLUMN_FIELD04] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD04))
            txnData[COLUMN_FIELD11] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD11))
            txnData[COLUMN_FIELD12] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD12))
            txnData[COLUMN_FIELD13] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD13))
            txnData[COLUMN_FIELD14] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD14))
            txnData[COLUMN_FIELD22] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD22))
            txnData[COLUMN_FIELD24] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD24))
            txnData[COLUMN_FIELD25] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD25))
            txnData[COLUMN_FIELD35] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD35))
            txnData[COLUMN_FIELD37] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD37))
            txnData[COLUMN_FIELD38] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD38))
            txnData[COLUMN_FIELD39] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD39))
            txnData[COLUMN_FIELD41] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD41))
            txnData[COLUMN_FIELD42] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD42))
            txnData[COLUMN_FIELD49] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD49))
            txnData[COLUMN_FIELD52] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD52))
            txnData[COLUMN_FIELD60] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD60))
            txnDataList.add(txnData)
        }
        cursor.close()
        db.close()
        return txnDataList
    }
    @SuppressLint("Range")
    fun getTxnDataByApprovalCode(field38Value: String): List<Map<String, String>> {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TXN_DATA,
            arrayOf(
                TXN_TYPE, COLUMN_MTI, COLUMN_BITMAP, COLUMN_FIELD02, COLUMN_FIELD03,
                COLUMN_FIELD04, COLUMN_FIELD11, COLUMN_FIELD12, COLUMN_FIELD13,
                COLUMN_FIELD14, COLUMN_FIELD22, COLUMN_FIELD24, COLUMN_FIELD25,
                COLUMN_FIELD35, COLUMN_FIELD37, COLUMN_FIELD38, COLUMN_FIELD39,
                COLUMN_FIELD41, COLUMN_FIELD42, COLUMN_FIELD49, COLUMN_FIELD52,
                COLUMN_FIELD60
            ),
            "$COLUMN_FIELD38 = ?",
            arrayOf(field38Value),
            null, null, null
        )

        val txnDataList = mutableListOf<Map<String, String>>()
        while (cursor.moveToNext()) {
            val txnData = mutableMapOf<String, String>()
            txnData[COLUMN_MTI] = cursor.getString(cursor.getColumnIndex(COLUMN_MTI))
            txnData[COLUMN_BITMAP] = cursor.getString(cursor.getColumnIndex(COLUMN_BITMAP))
            txnData[COLUMN_FIELD02] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD02))
            txnData[COLUMN_FIELD03] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD03))
            txnData[COLUMN_FIELD04] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD04))

            txnData[COLUMN_FIELD11] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD11))
            txnData[COLUMN_FIELD12] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD12))
            txnData[COLUMN_FIELD13] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD13))
            txnData[COLUMN_FIELD14] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD14))
            txnData[COLUMN_FIELD22] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD22))
            txnData[COLUMN_FIELD24] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD24))
            txnData[COLUMN_FIELD25] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD25))
            txnData[COLUMN_FIELD35] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD35))
            txnData[COLUMN_FIELD37] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD37))
            txnData[COLUMN_FIELD38] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD38))
            txnData[COLUMN_FIELD39] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD39))
            txnData[COLUMN_FIELD41] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD41))
            txnData[COLUMN_FIELD42] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD42))
            txnData[COLUMN_FIELD49] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD49))
            txnData[COLUMN_FIELD52] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD52))
            txnData[COLUMN_FIELD60] = cursor.getString(cursor.getColumnIndex(COLUMN_FIELD60))
            txnDataList.add(txnData)
        }
        cursor.close()
        db.close()
        return txnDataList
    }

    fun registerIPAndPortNumber(ip:String,portNumber:String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IP, ip)
            put(COLUMN_PORT_NO, portNumber)
        }
        db.insert(TABLE_COM_CONFIG, null, values)
        db.close()
    }
    fun getIPAndPortNumber(): Pair<String, String>? {
        val db = this.readableDatabase
        val projection = arrayOf(COLUMN_IP, COLUMN_PORT_NO)
        val cursor = db.query(
            TABLE_COM_CONFIG,   // The table to query
            projection,         // The columns to return
            null,               // The columns for the WHERE clause
            null,               // The values for the WHERE clause
            null,               // Don't group the rows
            null,               // Don't filter by row groups
            null                // The sort order
        )

        var result: Pair<String, String>? = null
        with(cursor) {
            if (moveToFirst()) {
                val ip = getString(getColumnIndexOrThrow(COLUMN_IP))
                val portNumber = getString(getColumnIndexOrThrow(COLUMN_PORT_NO))
                result = Pair(ip, portNumber)
            }
            close()
        }
        db.close()
        return result
    }

    fun checkAdmin():Boolean{
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${TABLE_ADMIN} ", null)

        val isRegistered = cursor.count > 0
        cursor.close()
        db.close()

        return isRegistered

    }
    fun checkTID():Boolean{
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${TABLE_TID} ", null)

        val isRegistered = cursor.count > 0
        cursor.close()
        db.close()

        return isRegistered

    }
    fun checkMID():Boolean{
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${TABLE_MID} ", null)

        val isRegistered = cursor.count > 0
        cursor.close()
        db.close()

        return isRegistered

    }
    fun checkMerchantName():Boolean{
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${TABLE_MERCHANT_NAME} ", null)

        val isRegistered = cursor.count > 0
        cursor.close()
        db.close()

        return isRegistered

    }
    fun checkMerchantAddress():Boolean{
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${TABLE_MERCHANT_ADDRESS} ", null)

        val isRegistered = cursor.count > 0
        cursor.close()
        db.close()

        return isRegistered

    }
    fun getTID(): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_TID FROM $TABLE_TID", null)
        var tid = ""

        if (cursor.moveToFirst()) {
            do{
                tid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TID))

            }while(cursor.moveToNext())
        }
        cursor.close()

        return tid
    }
    fun getMerName():String{

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_MERCHANT_NAME FROM $TABLE_MERCHANT_NAME", null)
        var mid=""
        if(cursor.moveToFirst()){
            do {
                mid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MERCHANT_NAME))

            }while (cursor.moveToNext())
        }
        cursor.close()

        return mid
    }

    fun getMID():String{

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_MID FROM $TABLE_MID", null)
        var mid=""
        if(cursor.moveToFirst()){
            do {
                mid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MID))

            }while (cursor.moveToNext())
        }
        cursor.close()

        return mid
    }

    fun getMerchantAddress():String{

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_MERCHANT_ADDRESS FROM $TABLE_MERCHANT_ADDRESS", null)
        var merchantAddress=""
        if(cursor.moveToFirst()){
            do {
                merchantAddress = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MERCHANT_ADDRESS))

            }while (cursor.moveToNext())
        }
        cursor.close()

        return merchantAddress
    }
    fun isAdminExists(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_ADMIN, arrayOf(COLUMN_ID), "$COLUMN_USERNAME=? AND $COLUMN_PASSWORD=?", arrayOf(username, password), null, null, null)
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }
    fun isSupportExists(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_SUPPORT, arrayOf(COLUMN_ID), "$COLUMN_USERNAME=? AND $COLUMN_PASSWORD=?", arrayOf(username, password), null, null, null)
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

    fun isSuperExists(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_SUPER, arrayOf(COLUMN_ID), "$COLUMN_USERNAME=? AND $COLUMN_PASSWORD=?", arrayOf(username, password), null, null, null)
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }
    fun isCashierExists(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_CASHIER, arrayOf(COLUMN_ID), "$COLUMN_USERNAME=? AND $COLUMN_PASSWORD=?", arrayOf(username, password), null, null, null)
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

}
