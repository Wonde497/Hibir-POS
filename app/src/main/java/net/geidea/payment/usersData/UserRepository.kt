package net.geidea.payment.usersData

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class UserRepository(context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context)

    // Add a new user (Admin, Support, Cashier)
    fun addUser(user: UserData): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USERNAME, user.username)
            put(DatabaseHelper.COLUMN_PASSWORD, user.password)
            put(DatabaseHelper.COLUMN_USERTYPE, user.userType)
        }
        return db.insert(DatabaseHelper.TABLE_USERS, null, values)
    }

    // Retrieve a user by username
    fun getUserByUsername(username: String): UserData? {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COLUMN_USERNAME} = ?",
            arrayOf(username),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
            val userType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERTYPE))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD))
            cursor.close()
            UserData(id, username, password, userType)
        } else {
            cursor.close()
            null
        }
    }

    // Retrieve all users of a specific type (e.g., "admin", "support", or "cashier")
    fun getUsersByType(userType: String): List<UserData> {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COLUMN_USERTYPE} = ?",
            arrayOf(userType),
            null,
            null,
            null
        )
        val usersList = mutableListOf<UserData>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD))
                usersList.add(UserData(id, username, password, userType))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return usersList
    }
}
