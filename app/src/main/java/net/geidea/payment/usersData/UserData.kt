package net.geidea.payment.usersData

data class UserData(
        val id: Int = 0,  // Autoincremented by the database
        val username: String,
        val userType: String,
        val password: String
        )