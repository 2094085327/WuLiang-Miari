package pers.wuliang.robot

import net.mamoe.mirai.event.events.MessageEvent
import org.springframework.stereotype.Component
import pers.wuliang.robot.config.Listener

/**
 *@Description:
 *@Author zeng
 *@Date 2023/6/11 12:44
 *@User 86188
 */
@Component
class test {
    @Listener("111", desc = "测试")
    fun MessageEvent.test() {
        println("收到111")
    }
}