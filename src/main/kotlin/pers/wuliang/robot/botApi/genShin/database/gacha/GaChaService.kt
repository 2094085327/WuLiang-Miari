package pers.wuliang.robot.botApi.genShin.database.gacha

import com.baomidou.mybatisplus.extension.service.IService
import org.springframework.stereotype.Service

/**
 *@Description:
 *@Author zeng
 *@Date 2023/10/3 22:25
 *@User 86188
 */
interface GaChaService : IService<GaChaEntity?> {
    /**
     * 根据uid查询数据
     */
    fun selectByUid(uid: String, type: String)
//    fun selectByUid(uid: String, type: String): MutableList<GaChaEntity>?

    /**
     * 重构查询，根据uid判断数据是否存在
     */
    fun selectByUid(uid: String): Boolean

    /**
     * 根据uid插入数据
     */
    fun insertByUid(uid: String, type: String, itemName: String, times: Int)
}