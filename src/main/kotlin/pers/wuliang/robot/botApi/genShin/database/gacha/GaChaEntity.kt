package pers.wuliang.robot.botApi.genShin.database.gacha

import com.baomidou.mybatisplus.annotation.*


/**
 *@Description:
 *@Author zeng
 *@Date 2023/10/3 21:51
 *@User 86188
 */
@TableName("gachainfo")
data class GaChaEntity(
    @TableId(value = "id", type = IdType.AUTO)
    private val id: Int? = null,

    /**
     * 用户UID
     */
    @TableField(value = "uid")
    private val uid: String? = null,

    /**
     * 卡池类型
     */
    @TableField(value = "type")
    private val gachaType: String? = null,

    /**
     * 物品名称
     */
    @TableField(value = "item_name") val itemName: String? = null,

    /**
     * 所抽次数
     */
    @TableField(value = "times") val times: Int? = null,

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    val updateTime: String? = null,

    /**
     * 物品类型
     */
    @TableField(value = "item_type")
    val itemType: String? = null,


    /**
     * 物品等级
     */
    @TableField(value = "rank_type")
    val rankType: Int? = null,

    /**
     * 物品ID
     */
    @TableField(value = "item_id")
    val itemId: String? = null,
)