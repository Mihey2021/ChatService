package ru.netology.data

data class Chat(
    val id: Int,
    val memberUsers: ChatPair
) {
    override fun toString(): String {
        return "✉  Чат между ${memberUsers.user1.name} и ${memberUsers.user2.name} (chatId: $id)"
    }
}

data class ChatPair(val user1: Users, val user2: Users)