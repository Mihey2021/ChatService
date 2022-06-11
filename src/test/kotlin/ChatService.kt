import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.netology.data.Messages
import ru.netology.data.Users
import ru.netology.exceptions.ChatNotFound
import ru.netology.exceptions.MessageNotFound
import ru.netology.services.ChatService
import kotlin.math.exp

class ChatService {
    private val userYou = Users(0, "Вы")
    private val userAleksandr = Users(1, "Саша")
    private val userTanya = Users(2, "Таня")
    private val userIgor = Users(3, "Игорь")
    private val userNadya = Users(4, "Надя")
    private val userEgor = Users(5, "Егор")

    @Before
    fun init() {
        ChatService.sendMessage(userYou, userAleksandr, "Сообщение от Вас для Александра")
        ChatService.sendMessage(userNadya, userYou, "Сообщение от Нади для Вас")
        ChatService.sendMessage(userEgor, userYou, "Сообщение от Егора для Вас")
        ChatService.sendMessage(userYou, userNadya, "Ваш ответ Наде")
        ChatService.sendMessage(userYou, userTanya, "Сообщение от Вас для Тани")
        ChatService.sendMessage(userIgor, userYou, "Сообщение от Игоря для Вас")
        ChatService.sendMessage(userYou, userIgor, "Ваш ответ Игорю")
        ChatService.sendMessage(userTanya, userYou, "Сообщение от Тани для Вас")
        ChatService.sendMessage(userNadya, userYou, "Ответ Нади на Ваше сообещние")
    }

    @After
    fun clearData() {
        ChatService.clearAllData()
    }

    @Test
    fun addMessage() {
        val result = ChatService.sendMessage(userYou, userTanya, "Ваш ответ Тане")
        Assert.assertEquals(true, result)
    }

    //Удаление чата с Id = 3
    @Test
    fun deleteChat() {
        val result = ChatService.deleteChat(3)
        Assert.assertEquals(true, result)
    }

    //Попытка удалить несуществующий чат
    @Test(expected = ChatNotFound::class)
    fun deleteNonexistentChat() {
        ChatService.deleteChat(73)
    }

    //Отредактируем существующее сообщение
    @Test
    fun editMessage() {
        val result = ChatService.editMessage(3, "Ваш ответ Наде (ИЗМЕНЕННЫЙ)")
        Assert.assertEquals(Messages::class, result::class)
    }

    //Попытка отредактировать несуществующее сообщение
    @Test(expected = MessageNotFound::class)
    fun editNonexistentMessage() {
        ChatService.editMessage(52, "Ваш ответ Наде (ИЗМЕНЕННЫЙ)")
    }

    //Удалим существующее сообщение в чате
    @Test
    fun deleteMessage() {
        val result = ChatService.deleteMessage(5)
        Assert.assertEquals(true, result)
    }

    //Удалим существующее сообщение в чате (оставшееся)
    @Test
    fun deleteLastMessageInChat() {
        ChatService.deleteMessage(5)
        val result = ChatService.deleteMessage(6)
        Assert.assertEquals(true, result)
    }

    //Удалим несуществующее сообщение в чате
    @Test(expected = MessageNotFound::class)
    fun deleteNonexistentMessage() {
        ChatService.deleteMessage(55)
    }

    //Получим количество непрочитанных чатов
    @Test
    fun unreadChatsCount() {
        val result = ChatService.getUnreadChatsCount(userYou.id)
        Assert.assertEquals(Int::class, result::class)
    }

    //Получим 2 сообщения из чата с Надей
    @Test
    fun getMessagesFromChat() {
        val result = ChatService.getMessagesFromChat(1, 1, 2)
        Assert.assertEquals(2, result.size)
    }

    //Попробуем получить 5 сообщений из несуществующего чата
    @Test(expected = ChatNotFound::class)
    fun getMessagesFromNonexistentChat() {
        ChatService.getMessagesFromChat(12, 0, 5)
    }

    //Получим все сообщения из чата с Надей
    @Test
    fun getAllMessagesFromChat() {
        val result = ChatService.getAllMessagesFromChat(1)
        Assert.assertEquals(ArrayList::class, result::class)
    }

    //Получим все чаты пользователя
    @Test
    fun getAllChatsForUser() {
        val result = ChatService.getAllChatsForUser(userYou)
        Assert.assertEquals(ArrayList::class, result::class)
    }
}