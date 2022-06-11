package ru.netology

import ru.netology.data.Users
import ru.netology.exceptions.ChatNotFound
import ru.netology.exceptions.MessageNotFound
import ru.netology.services.ChatService

fun main(args: Array<String>) {
    val userYou = Users(0, "Вы")
    val userAleksandr = Users(1, "Саша")
    val userTanya = Users(2, "Таня")
    val userIgor = Users(3, "Игорь")
    val userNadya = Users(4, "Надя")
    val userEgor = Users(5, "Егор")

    ChatService.sendMessage(userYou, userAleksandr, "Сообщение от Вас для Александра")
    ChatService.sendMessage(userNadya, userYou, "Сообщение от Нади для Вас")
    ChatService.sendMessage(userEgor, userYou, "Сообщение от Егора для Вас")
    ChatService.sendMessage(userYou, userNadya, "Ваш ответ Наде")
    ChatService.sendMessage(userYou, userTanya, "Сообщение от Вас для Тани")
    ChatService.sendMessage(userIgor, userYou, "Сообщение от Игоря для Вас")
    ChatService.sendMessage(userYou, userIgor, "Ваш ответ Игорю")

    printChatListAndMessages(userYou)

    println("\nУдаление чата с Id = 3")
    ChatService.deleteChat(3)
    ChatService.getAllChatsForUser(userYou).forEach { println(it) }
    printChatListAndMessages(userYou)

    println("\nПопробуем удалить несуществующий чат")
    try {
        ChatService.deleteChat(73)
    } catch (e: ChatNotFound) {
        println(e.message)
    }


    println("\nОтредактируем существующее сообщение")
    ChatService.editMessage(3, "Ваш ответ Наде (ИЗМЕНЕННЫЙ)")
    printChatListAndMessages(userYou)

    println("\nПопробуем отредактировать несуществующее сообщение")
    try {
        ChatService.editMessage(16, "Несуществующее сообщение")
    } catch (e: MessageNotFound) {
        println(e.message)
    }

    println("\nУдалим существующее сообщение в чате")
    ChatService.deleteMessage(5)
    printChatListAndMessages(userYou)
    println("\nУдалим существующее сообщение в чате (оставшееся)")
    ChatService.deleteMessage(6)
    printChatListAndMessages(userYou)

    println("\nПопробуем удалить несуществующее сообщение")
    try {
        ChatService.deleteMessage(5)
    } catch (e: MessageNotFound) {
        println(e.message)
    }

    ChatService.sendMessage(userTanya, userYou, "Сообщение от Тани для Вас")
    ChatService.sendMessage(userNadya, userYou, "Ответ Нади на Ваше сообещние")
    println("Количество непрочитанных чатов: ${ChatService.getUnreadChatsCount(userYou.id)}")

    printChatListAndMessages(userYou)

    println("\nПолучим 2 сообщения из чата с Надей:")
    ChatService.getMessagesFromChat(1, 1, 2)
        .forEach { println(" - ${it.text} [${if (it.read) "Прочитано" else "Не прочитано"}]") }



    try {
        println("\nПопробуем получить 5 сообщений из несуществующего чата:")
        ChatService.getMessagesFromChat(12, 0, 5)
    } catch (e: ChatNotFound) {
        println(e.message)
    }

}

fun printChatListAndMessages(user: Users) {
    val allYourChatsList = ChatService.getAllChatsForUser(user)
    println("\nЧаты пользователя [${user.name}]: ")
    allYourChatsList.forEach {
        println("\n$it")
        ChatService.getAllMessagesFromChat(it.id)
            .forEach { msg -> println("    - ${msg.text} [${if (msg.read) "Прочитано" else "Не прочитано"}]") }
    }
}