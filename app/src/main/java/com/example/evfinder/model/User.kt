package com.example.evfinder.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val isGuest: Boolean = false
) {
    companion object {
        fun guest(): User = User(
            id = "guest_user",
            name = "Invitado",
            email = "invitado@ecogo.app",
            isGuest = true
        )
    }
}
