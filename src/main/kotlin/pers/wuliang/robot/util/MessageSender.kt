package pers.wuliang.robot.util

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*

/**
 *@Description:
 *@Author zeng
 *@Date 2023/6/11 12:37
 *@User 86188
 */
object MessageSender {

//    suspend fun GroupMessageEvent.send(message: String): MessageReceipt<Group> = this.subject.sendMessage(message)
//
//    suspend fun GroupMessageEvent.send(message: Message): MessageReceipt<Group> = this.subject.sendMessage(message)

    suspend fun MessageEvent.send(message: Message): MessageReceipt<Contact> = this.subject.sendMessage(message)

    suspend fun MessageEvent.send(message: String): MessageReceipt<Contact> = this.subject.sendMessage(message)

    suspend fun MessageEvent.reply(message: MessageChain): MessageReceipt<Contact> = this.send(QuoteReply(message))

    suspend fun MessageEvent.reply(message: String): MessageReceipt<Contact> = this.send(QuoteReply(messageChainOf(PlainText(message))))

}