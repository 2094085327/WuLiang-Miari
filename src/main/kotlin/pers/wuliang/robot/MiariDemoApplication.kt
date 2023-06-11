package pers.wuliang.robot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import pers.wuliang.robot.util.SpringUtil


@SpringBootApplication
class MiariDemoApplication

suspend fun main(args: Array<String>) {
    runApplication<MiariDemoApplication>(*args)
    val context: ApplicationContext = SpringUtil.applicationContext!!
    val qqBot = context.getBean(QQBot::class.java)
    qqBot.startBot()
}
