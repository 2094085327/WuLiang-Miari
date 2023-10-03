package pers.wuliang.robot

import net.mamoe.mirai.utils.BotConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.annotation.EnableScheduling
import pers.wuliang.robot.util.SpringUtil


@EnableScheduling
@SpringBootApplication
open class MiariDemoApplication

fun main(args: Array<String>) {
    runApplication<MiariDemoApplication>(*args)
    val context: ApplicationContext = SpringUtil.applicationContext!!
    val qqBot = context.getBean(QQBot::class.java)
    qqBot.startBot()
}
