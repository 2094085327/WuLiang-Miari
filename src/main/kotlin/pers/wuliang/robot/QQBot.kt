package pers.wuliang.robot

import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.auth.BotAuthorization
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.BotConfiguration.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import pers.wuliang.robot.eventListen.MyEventHandlers


/**
 *@Description:
 *@Author zeng
 *@Date 2023/6/11 11:45
 *@User 86188
 */
@Component
@PropertySource("classpath:bot.properties")
class QQBot {
    @Value("\${bot.account}")
    final val qq: Long = 0

    /**
     * 启动BOT
     */
    suspend fun startBot() {
        val bot = BotFactory.newBot(qq, BotAuthorization.byQRCode()) {
            fileBasedDeviceInfo()
            protocol = BotConfiguration.MiraiProtocol.ANDROID_WATCH
        }
        bot.login()
    }
}