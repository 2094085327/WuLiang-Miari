package pers.wuliang.robot

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.auth.BotAuthorization
import net.mamoe.mirai.utils.BotConfiguration.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import xyz.cssxsh.mirai.tool.FixProtocolVersion
import xyz.cssxsh.mirai.tool.FixProtocolVersion.fetch
import xyz.cssxsh.mirai.tool.KFCFactory
import java.io.FileNotFoundException


/**
 *@Description:
 *@Author zeng
 *@Date 2023/6/11 11:45
 *@User 86188
 */
@Component
@PropertySource("classpath:bot.properties")
@Configuration
open class QQBot {
    @Value("\${bot.account}")
    final val qq: Long = 0

    @Value("\${bot.pwd}")
    val pwd :String = ""

    /**
     * 启动BOT
     */
    @Bean
    open fun startBot(): Bot {
//        val bot = BotFactory.newBot(qq, BotAuthorization.byQRCode()) {
//            fileBasedDeviceInfo()
//            protocol = MiraiProtocol.ANDROID_WATCH
//        }
        try {
            FixProtocolVersion.load(MiraiProtocol.ANDROID_PHONE)
        } catch (ignored: FileNotFoundException) {
            fetch(MiraiProtocol.ANDROID_PHONE, "8.9.63")
        }
        KFCFactory.install()

        val bot = BotFactory.newBot(qq, BotAuthorization.byPassword(pwd)) {
            fileBasedDeviceInfo()
//            protocol = MiraiProtocol.ANDROID_PAD
        }
        runBlocking { bot.login() }
        return bot
    }

}