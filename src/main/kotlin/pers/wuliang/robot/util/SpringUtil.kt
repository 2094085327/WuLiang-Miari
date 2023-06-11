package pers.wuliang.robot.util

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component


/**
 *@Description:普通类调用Spring bean对象 注意：此类需要放到App.java同包或者子包下才能被扫描，否则失效。
 *@Author zeng
 *@Date 2023/6/11 12:10
 *@User 86188
 */
@Component
class SpringUtil : ApplicationContextAware {
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        if (Companion.applicationContext == null) {
            Companion.applicationContext = applicationContext
        }
    }

    companion object {
        //获取applicationContext
        var applicationContext: ApplicationContext? = null
            private set

        //通过name获取 Bean.
        fun getBean(name: String?): Any {
            return applicationContext!!.getBean(name!!)
        }

        //通过class获取Bean.
        fun <T> getBean(clazz: Class<T>): T {
            return applicationContext!!.getBean(clazz)
        }

        //通过name,以及Clazz返回指定的Bean
        fun <T> getBean(name: String?, clazz: Class<T>): T {
            return applicationContext!!.getBean(name!!, clazz)
        }
    }
}