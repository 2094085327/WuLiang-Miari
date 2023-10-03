package pers.wuliang.robot.botApi.genShin.database.gacha

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 *@Description:
 *@Author zeng
 *@Date 2023/10/3 22:30
 *@User 86188
 */
@Service
open class GaChaServiceImpl : ServiceImpl<GaChaMapper?, GaChaEntity?>(), GaChaService {
    @Autowired
    lateinit var gaChaMapper: GaChaMapper

//    data class ItemData(val key: String, val value: Int)

    override fun selectByUid(uid: String, type: String) {
//        val queryWrapper = QueryWrapper<GaChaEntity>().eq("uid", uid).eq("type", type).orderByDesc("item_name")
//        val gachaBefore = gaChaMapper.selectList(queryWrapper)
//        if (gachaBefore == null) {
////            return null
//        } else {
//            val gachaDataList = mutableListOf<ItemData>()
//
//            for (itemInfo in gachaBefore) {
//                if (itemInfo != null) {
//                    val gachaArray = ItemData(
//                        key = itemInfo.itemName.toString(),
//                        value = itemInfo.times!!.toInt()
//                    )
//                    gachaDataList.add(gachaArray)
//                }
//            }
//
//        }
    }

    override fun selectByUid(uid: String): Boolean {
//        val queryWrapper = QueryWrapper<GaChaEntity>().eq("uid", uid)
//        val gachaInfo = gaChaMapper.selectList(queryWrapper)
//        return gachaInfo.size != 0
        return false
    }

    override fun insertByUid(uid: String, type: String, itemName: String, times: Int) {
//        val gachaInfo = GaChaEntity(
//            uid = uid,
//            gachaType = type,
//            itemName = itemName,
//            times = times,
//            updateTime = System.currentTimeMillis().toString()
//        )
//        val queryWrapper = QueryWrapper<GaChaEntity>()
//            .eq("uid", uid)
//            .eq("type", type)
//            .eq("item_name", itemName)
//        val existGachaInfo = gaChaMapper.selectOne(queryWrapper)
//        if (existGachaInfo != null) {
//            if (itemName == "已抽次数") {
//                gaChaMapper.update(gachaInfo, queryWrapper)
//            }
//        } else {
//            gaChaMapper.insert(gachaInfo)
//        }
    }
}