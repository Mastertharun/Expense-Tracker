package com.expensetracker.data.repository

import com.expensetracker.data.db.dao.UserDao
import com.expensetracker.data.db.entities.User
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    suspend fun register(name: String, email: String, password: String): Result<User> {
        val existing = userDao.getUserByEmail(email)
        if (existing != null) {
            return Result.failure(Exception("Email already registered"))
        }
        val hash = hashPassword(password)
        val user = User(name = name, email = email, passwordHash = hash)
        val id = userDao.insertUser(user)
        return Result.success(user.copy(id = id))
    }

    suspend fun login(email: String, password: String): User? {
        val hash = hashPassword(password)
        return userDao.login(email, hash)
    }

    suspend fun getUserById(id: Long): User? = userDao.getUserById(id)

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
