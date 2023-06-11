package pers.wuliang.robot.eventListen

import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.ListeningStatus
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.MessageEvent
import org.jetbrains.annotations.NotNull
import org.springframework.stereotype.Component
import kotlin.coroutines.CoroutineContext


/**
 *@Description:
 *@Author zeng
 *@Date 2023/6/11 12:28
 *@User 86188
 */
@Component
class MyEventHandlers : SimpleListenerHost() {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        // 处理事件处理时抛出的异常
    }

    @EventHandler
    @Throws(Exception::class)
    suspend fun onMessage(@NotNull event: MessageEvent) { // 可以抛出任何异常, 将在 handleException 处理
        event.subject.sendMessage("received")
        // 无返回值, 表示一直监听事件.
    }

    @NotNull
    @EventHandler
    @Throws(Exception::class)
    suspend fun onMessageStatus(@NotNull event: MessageEvent): ListeningStatus { // 可以抛出任何异常, 将在 handleException 处理
        event.subject.sendMessage("received")
        return ListeningStatus.LISTENING // 表示继续监听事件
        // return ListeningStatus.STOPPED; // 表示停止监听事件
    }
}

// 在 QQbot 的 startBot() 方法中注册：
// bot.getEventChannel.registerListenerHost(new MyEventHandlers())