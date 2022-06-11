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

        var filterChatsList = getChatList(senderUser, receiverUser)

        //Проверим есть ли ранее созданный чат между этими пользователями и создадим его, если такого чата нет
        if (filterChatsList.isEmpty()) {
            createChat(senderUser, receiverUser)
            filterChatsList = getChatList(senderUser, receiverUser)
        }

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

    fun createChat(senderUser: Users, receiverUser: Users): Boolean {
        val chatId = if (chatsList.isEmpty()) 0 else chatsList.last().id + 1
        return chatsList.add(Chat(chatId, senderUser vs receiverUser))
    }

    fun markMessagesAsRead(chatId: Int, receiverId: Int) {
        messagesList.filter { it.chatId == chatId && !it.read && it.receiverId == receiverId }
            .forEach { it.read = true }
    }

    @Throws(ChatNotFound::class)
    fun deleteChat(chatId: Int): Boolean {
        val filterChatList = chatsList.filter { it.id == chatId }.toList()
        if (filterChatList.isEmpty()) throw ChatNotFound("✖ Чат не найден!")
        val filterMessagesList = messagesList.filter { it.chatId == chatId }.toList()
        filterMessagesList.forEach { messagesList.remove(it) }
        filterChatList.forEach { chatsList.remove(it) }
        return true
    }

    @Throws(MessageNotFound::class)
    fun editMessage(messageId: Int, newText: String): Messages {
        val filterMessagesList = messagesList.filter { it.id == messageId }

        if (filterMessagesList.isEmpty()) throw MessageNotFound("✖ Сообщение не найдено!")
        var msg = filterMessagesList[0].copy(text = newText)
        messagesList[messagesList.indexOf(filterMessagesList[0])] = msg
        return messagesList[messagesList.indexOf(msg)]
    }

    @Throws(MessageNotFound::class)
    fun deleteMessage(msgId: Int): Boolean {
        val filterMessagesList = messagesList.filter { it.id == msgId }
        if (filterMessagesList.isEmpty()) throw MessageNotFound("✖ Сообщение не найдено!")
        val msg = filterMessagesList[0]
        val filterMessagesInChat = messagesList.filter { it.chatId == msg.chatId }
        //Если это единственное оставшееся сообщение в чате, удалим весь чат
        if (filterMessagesInChat.size == 1) {
            val filterChatList = chatsList.filter { it.id == msg.chatId }
            println(" ❗ Это единственное сообщение в чате [${filterChatList[0]}]. Удаляю чат.")
            deleteChat(msg.chatId)
            return true
        }
        println("✔ Сообщение удалено")
        return messagesList.remove(msg)
    }

    fun getAllMessagesFromChat(chatId: Int) = messagesList.filter { it.chatId == chatId }

    @Throws(ChatNotFound::class)
    fun getMessagesFromChat(chatId: Int, lastMessageId: Int, count: Int): List<Messages> {
        if (chatsList.filter { it.id == chatId }.isEmpty()) throw ChatNotFound("✖ Чат не найден!")
        val filterMessagesList = messagesList.filter { it.chatId == chatId }
        val totalCount = if (count < 0) filterMessagesList.size else count
        val countRec =
            if (filterMessagesList.size >= lastMessageId + totalCount) lastMessageId + totalCount else filterMessagesList.size
        return filterMessagesList.subList(lastMessageId, countRec)
    }

    fun getUnreadChatsCount(userId: Int): Int {
        //Непрочитанными считаем сообщения в статусе !read, и только если такое сообщение отправлено не нами
        var chatsIdList = listOf<Int>()
        println("\nНепрочитанные сообщения в чатах:")
        messagesList.filter { it.receiverId == userId && !it.read }.forEach {
            println("- ${it.text} [Не прочитано] (chatId: ${it.chatId})")
            if (chatsIdList.indexOf(it.chatId) == -1) chatsIdList += it.chatId
        }
        //return chatsList.count{it.id in chatsIdList }
        return chatsIdList.count()
    }

    fun clearAllData(){
        chatsList.clear()
        messagesList.clear()
    }
}