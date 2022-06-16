package ru.netology.services

import ru.netology.data.Chat
import ru.netology.data.ChatPair
import ru.netology.data.Messages
import ru.netology.data.Users
import ru.netology.exceptions.ChatNotFound
import ru.netology.exceptions.MessageNotFound

infix fun Users.vs(that: Users) = ChatPair(this, that)

object ChatService {
    private val chatsList = mutableListOf<Chat>()
    private val messagesList = mutableListOf<Messages>()

    fun sendMessage(senderUser: Users, receiverUser: Users, text: String): Boolean {

        val filterChatsList = getChatList(senderUser, receiverUser)
            .ifEmpty { listOf(createChat(senderUser, receiverUser)) }

        val currentChat = filterChatsList[0]
        val msgId = if (messagesList.isEmpty()) 0 else messagesList.last().id + 1
        //Добавим сообщение в чат
        val msgSend = messagesList.add(Messages(msgId, currentChat.id, senderUser.id, receiverUser.id, text))

        //Отметим в этом чате непрочитанные входящие сообщения как прочитанные (если отвечаем, значит прочитали входящие)
        markMessagesAsRead(currentChat.id, senderUser.id)

        return msgSend
    }

    fun getChatList(senderUser: Users, receiverUser: Users) =
        chatsList.filter { it.memberUsers == senderUser vs receiverUser || it.memberUsers == receiverUser vs senderUser }

    fun getAllChatsForUser(user: Users) =
        chatsList.filter { it.memberUsers.user1 == user || it.memberUsers.user2 == user }

    fun createChat(senderUser: Users, receiverUser: Users): Chat {
        val chatId = if (chatsList.isEmpty()) 0 else chatsList.last().id + 1
        val newChat = Chat(chatId, senderUser vs receiverUser)
        chatsList.add(newChat)
        return newChat
    }

    fun markMessagesAsRead(chatId: Int, receiverId: Int) {
        messagesList.filter { it.chatId == chatId && !it.read && it.receiverId == receiverId }
            .forEach { it.read = true }
    }

    @Throws(ChatNotFound::class)
    fun deleteChat(chatId: Int): Boolean {
        chatsList
            .filter { it.id == chatId }
            .ifEmpty { throw ChatNotFound("✖ Чат не найден!") }
            .forEach { chatsList.remove(it) }

        messagesList
            .filter { it.chatId == chatId }
            .forEach { messagesList.remove(it) }
        return true
    }

    @Throws(MessageNotFound::class)
    fun editMessage(messageId: Int, newText: String): Messages {
        val filterMessagesList = messagesList
            .filter { it.id == messageId }
            .ifEmpty { throw MessageNotFound("✖ Сообщение не найдено!") }

        val msg = filterMessagesList[0].copy(text = newText)
        messagesList[messagesList.indexOf(filterMessagesList[0])] = msg
        return messagesList[messagesList.indexOf(msg)]
    }

    @Throws(MessageNotFound::class)
    fun deleteMessage(msgId: Int): Boolean {
        val msg = messagesList
            .filter { msg -> msg.id == msgId }
            .ifEmpty { throw MessageNotFound("✖ Сообщение не найдено!") }
            .let { messages -> messages[0] }
            .run {
                messagesList
                    .filter { it.chatId == this.chatId }
                    .also {
                        if (it.size == 1) {
                            println(" ❗ Это единственное сообщение в чате [${chatsList.filter { it.id == this.chatId }[0]}]. Удаляю чат.")
                            return deleteChat(this.chatId)
                        }
                    }
                return@run this
            }

        println("✔ Сообщение удалено")
        return messagesList.remove(msg)
    }

    fun getAllMessagesFromChat(chatId: Int) = messagesList.filter { it.chatId == chatId }

    @Throws(ChatNotFound::class)
    fun getMessagesFromChat(chatId: Int, lastMessageId: Int, count: Int): List<Messages> {
        chatsList
            .filter { it.id == chatId }
            .ifEmpty { throw ChatNotFound("✖ Чат не найден!") }

        return messagesList
            .filter { it.chatId == chatId && it.id >= lastMessageId }
            .take(count)
    }

    fun getUnreadChatsCount(userId: Int): Int {
        //Непрочитанными считаем сообщения в статусе !read, и только если такое сообщение отправлено не нами
        //Также, в одном чате может быть более одного непрочитанного сообщения, поэтому подсчитывать кол-во чатов с непрочитанными сообщениями будем с учетом уникальности Id чата

        print("\nНепрочитанные сообщения в чатах:")
        return messagesList
            .filter { it.receiverId == userId && !it.read }
            .fold(
                Pair(
                    listOf<Int>(),
                    ""
                )
            ) { acc, msg -> (if (acc.first.indexOf(msg.chatId) == -1) acc.first + msg.chatId else acc.first) to acc.second + "\n- ${msg.text} [Не прочитано] (chatId: ${msg.chatId})" }
            .also { println(it.second) }.first.count()
    }

    fun clearAllData() {
        chatsList.clear()
        messagesList.clear()
    }
}