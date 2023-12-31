package pers.wuliang.robot.config

import cn.hutool.core.util.ClassUtil
import cn.hutool.extra.spring.SpringUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AliasFor
import org.springframework.core.annotation.AnnotationUtils
import pers.wuliang.robot.common.permit.enums.PermitEnum
import pers.wuliang.robot.common.utils.EventClassUtil.getIfEvent
import pers.wuliang.robot.common.utils.EventClassUtil.isEvent
import pers.wuliang.robot.common.utils.EventClassUtil.isMessageEvent
import pers.wuliang.robot.common.utils.LoggerUtils.logInfo

import javax.annotation.PostConstruct
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.kotlinFunction
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * @author Chowhound
 * @date   2023/6/8 - 16:45
 * @description key: 拥有监听方法的bean, value: list集合，其中的元素pair.first为监听方法，pair.second为监听的事件类型
 */
open class ListenerFunctions: MutableMap<Any, MutableList<Pair<KFunction<*>, KClass<out Event>>>> by mutableMapOf()

/**
 * 持续会话
 */
@Suppress("all")
suspend inline fun  <reified E : Event> E.waitMessage(
    time: Duration = 15.seconds,
    noinline filter: (E) -> Boolean = { true }
): E? {
    val deferred = CoroutineScope(Dispatchers.IO).async {
        GlobalEventChannel.asFlow()
            .filterIsInstance<E>()
            .filter(filter)
            .first()
    }

    return withTimeoutOrNull(time) { deferred.await() }
}

/**
 * @author Chowhound
 * @date   2023/6/6 - 10:50
 */
@Configuration
open class MiraiEventListenerConfiguration(
    val bot: Bot
) {

    val listenerFunctions = ListenerFunctions()

    @PostConstruct
    fun init() {
        ClassUtil.scanPackage("pers.wuliang.robot").forEach { clazz ->

            if (clazz == MiraiEventListenerConfiguration::class.java){
                return@forEach
            }

            val bean = try {
                SpringUtil.getBean(clazz) // 监听方法所在的类的实例
            } catch (e: NoSuchBeanDefinitionException){
                return@forEach
            }
            parseEventOfClass(clazz, bean)

        }


        // 开始注册监听器
        bot.eventChannel.subscribeMessages {
            listenerFunctions.forEach{ entry ->
                entry.value.forEach { pair ->
                    if (pair.second.isMessageEvent()){
                        val listener = AnnotationUtils.getAnnotation(pair.first.javaMethod!!, Listener::class.java)
                            ?: throw RuntimeException("不是监听器")

                        when (listener.matchType) {
                            MatchType.TEXT_EQUALS -> case(listener.value) {
                                pair.first.call(entry.key, this)
                            }
                            MatchType.TEXT_EQUALS_IGNORE_CASE -> case(listener.value, ignoreCase = true) {
                                pair.first.call(entry.key, this)
                            }
                            MatchType.TEXT_STARTS_WITH -> startsWith(listener.value) {
                                pair.first.call(entry.key, this)
                            }
                            MatchType.TEXT_ENDS_WITH -> endsWith(listener.value) {
                                pair.first.call(entry.key, this)
                            }
                            MatchType.TEXT_CONTAINS -> contains(listener.value) {
                                pair.first.call(entry.key, this)
                            }
                            MatchType.REGEX_MATCHES -> matching(listener.value.parseReg().toRegex()) {

                                fun getParams(): Array<Any?> {
                                    val params = mutableListOf<Any?>()
                                    pair.first.parameters.forEach { param ->
                                        when (param.kind){
                                            KParameter.Kind.INSTANCE -> params.add(entry.key)
                                            KParameter.Kind.EXTENSION_RECEIVER -> params.add(this)
                                            else ->{
                                                val name =
                                                    param.annotations.find { it == FilterValue::class}
                                                        ?.let { it as FilterValue }?.value
                                                        ?: param.name
                                                        ?: throw RuntimeException("参数${param.name}没有指定名称")
                                                params.add(it.groups[name]?.value)
                                            }
                                        }
                                    }
                                    return params.toTypedArray()
                                }


//                                    pair.first.call(entry.key, this)
                                CoroutineScope(Dispatchers.IO).launch {
                                    logInfo("匹配到正则：参数: {}", pair.first.parameters)
                                    pair.first.callSuspend(*getParams())
                                }

                            }
                            MatchType.REGEX_CONTAINS -> finding(listener.value.parseReg().toRegex()) {
                                pair.first.call(entry.key, this)
                            }

                        }

                    }else{// 非MessageEvent
                        bot.eventChannel.subscribeAlways(eventClass = pair.second){
                            pair.first.call(entry.key, this)
                        }
                    }

                }
            }
        }

    }

    fun parseEventOfClass(clazz: Class<*>, bean: Any) {
        clazz.declaredMethods.forEach { functionJava ->
            val function = functionJava.kotlinFunction ?: return@forEach

            function.annotations.forEach { annotation ->
                if (annotation is Listener) {
                    // 获取扩展函数的被拓展的类型
                    val classifier = function.extensionReceiverParameter!!.type.classifier!! as KClass<*>
                    val event = classifier.getIfEvent() ?: throw RuntimeException("不是事件类型")

                    if (classifier.isEvent()){
                        listenerFunctions[bean]
                            ?.add(function to event)
                            ?: run {
                                listenerFunctions[bean] =
                                    mutableListOf(function to event)
                            }

                    }
                }
            }
        }


    }

    /**
     * 解析string中的{key,value}，并将其替换为正则表达式
     */
    fun String.parseReg(): String {
        val builder = StringBuilder()
        split("{").forEachIndexed { index, str ->
            if (index == 0) {
                builder.append(str)
                return@forEachIndexed
            }
            val strList = str.split("}")
            println("strList：$strList")
            if (strList.size == 1) {
                builder.append(str)
            } else {
                val pattern = strList[0].split(",")
                when (pattern.size) {
                    1 -> builder.append("(?<${pattern[0]}>.*?)")
                    2 -> builder.append("(?<${pattern[0]}>${pattern[1].replaceGroupNoCapture()})")
                }
            }
            builder.append(strList[1])
        }

        return builder.toString()
    }

    fun String.replaceGroupNoCapture() = this.replace("(", "(?:")
}



@Suppress("unused")
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Listener(
    @get:AliasFor("pattern")
    val value: String = "",

    val matchType: MatchType = MatchType.REGEX_MATCHES,

    val priority: EventPriority = EventPriority.NORMAL,
    @get:AliasFor("value")
    val pattern: String = "", // 正则表达式，用于匹配消息内容

    val desc: String = "无描述",

    val isBoot: Boolean = false,// 监听是否需要开机，为 false 时关机不监听
    val permit: PermitEnum = PermitEnum.MEMBER,// 监听方法的权限
)

/**
 *
 * @author ForteScarlet
 */
enum class MatchType{
    /**
     * 全等匹配
     */
    TEXT_EQUALS,

    /**
     * 忽略大小写的全等匹配
     */
    TEXT_EQUALS_IGNORE_CASE,


    /**
     * 开头匹配
     */
    TEXT_STARTS_WITH,

    /**
     * 尾部匹配.
     */
    TEXT_ENDS_WITH,

    /**
     * 包含匹配.
     */
    TEXT_CONTAINS,

    /**
     * 正则完全匹配. `regex.matches(...)`
     */
    REGEX_MATCHES,


    /**
     * 正则包含匹配. `regex.find(...)`
     */
    REGEX_CONTAINS,

    ;



}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class FilterValue(val value: String)