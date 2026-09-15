package com.example.evfinder.data

import com.example.evfinder.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {

    private val registeredUsers = mutableMapOf(
        "taxista@ecogo.app" to Pair("Carlos Taxista", "123456"),
        "usuario@ecogo.app" to Pair("Laura Gómez", "123456")
    )

    private val _currentUser = MutableStateFlow<User>(User.guest())
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    fun login(email: String, pass: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val userRecord = registeredUsers[cleanEmail]
        return if (userRecord != null && userRecord.second == pass) {
            val user = User(
                id = cleanEmail,
                name = userRecord.first,
                email = cleanEmail,
                isGuest = false
            )
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Credenciales inválidas. Verifica tu correo y contraseña."))
        }
    }

    fun register(name: String, email: String, pass: String): Result<User> {
        val cleanName = name.trim()
        val cleanEmail = email.trim().lowercase()
        
        if (cleanName.isBlank() || cleanEmail.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Por favor completa todos los campos requeridos."))
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return Result.failure(Exception("Formato de correo electrónico no válido."))
        }
        if (pass.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres."))
        }
        if (registeredUsers.containsKey(cleanEmail)) {
            return Result.failure(Exception("El correo electrónico ya se encuentra registrado."))
        }

        registeredUsers[cleanEmail] = Pair(cleanName, pass)
        val user = User(
            id = cleanEmail,
            name = cleanName,
            email = cleanEmail,
            isGuest = false
        )
        _currentUser.value = user
        return Result.success(user)
    }

    fun loginAsGuest() {
        _currentUser.value = User.guest()
    }

    fun logout() {
        _currentUser.value = User.guest()
    }
}
