package ru.netology.data

data class Messages(
    val id: Int,
    val chatId: Int,
    val senderId: Int,
    val receiverId: Int,
    val text: String,
    internal var read: Boolean = false
)