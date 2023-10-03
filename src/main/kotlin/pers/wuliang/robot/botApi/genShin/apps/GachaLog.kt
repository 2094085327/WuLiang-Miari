package pers.wuliang.robot.botApi.genShin.apps

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pers.wuliang.robot.botApi.genShin.MysApi
import pers.wuliang.robot.botApi.genShin.database.gacha.GaChaService
import pers.wuliang.robot.config.Listener
import pers.wuliang.robot.config.MatchType
import pers.wuliang.robot.util.MessageSender.reply
import pers.wuliang.robot.util.MessageSender.send
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*


/**
 *@Description:
 *@Author zeng
 *@Date 2023/9/8 17:01
 *@User 86188
 */
@Component
class GachaLog {

    @Autowired
    lateinit var gaChaService: GaChaService

    private val qrLogin = QRLogin()
    private var mysApi = MysApi("0", "")
    private val objectMapper = ObjectMapper()

    fun test2(gachaId: String, type: String): MutableList<MutableMap<String, Any>> {
        val typeString = if (type == "up") {
            "r5_up_items"
        } else {
            "r5_prob_list"
        }
        val jsonData = mysApi.getData("gacha_Info", mutableMapOf("gachaId" to gachaId))
        val jsonArray = jsonData[typeString]
        val data: MutableList<MutableMap<String, Any>> = mutableListOf()
        jsonArray.forEach { item ->
            data.add(
                mutableMapOf(
                    "item_name" to item["item_name"],
                    "item_type" to item["item_type"]
                )
            )
        }
        return data
    }

    fun test() {
        val currentDir = File(".").absoluteFile
//        val resourcesDir = File(currentDir, "resources")
        val jsonFile = File(currentDir, "resources/gacha_up.json")

        val gachaUp = mysApi.getData("gacha_Id")["data"]["list"]

        val gachaUpMap: MutableMap<String, Map<String, Any>> = mutableMapOf(
            "常驻" to mutableMapOf(
                "begin_time" to gachaUp[0]["begin_time"].textValue(),
                "end_time" to gachaUp[0]["end_time"].textValue(),
                "gacha_id" to gachaUp[0]["gacha_id"].textValue(),
                "r5_prob_list" to test2(gachaUp[0]["gacha_id"].textValue(), "prob")
            ),
            "角色活动" to mutableMapOf(
                "begin_time" to gachaUp[1]["begin_time"].textValue(),
                "end_time" to gachaUp[1]["end_time"].textValue(),
                "gacha_id" to gachaUp[1]["gacha_id"].textValue(),
                "r5_up_items" to test2(gachaUp[1]["gacha_id"].textValue(), "up")
            ),
            "角色活动-2" to mutableMapOf(
                "begin_time" to gachaUp[2]["begin_time"].textValue(),
                "end_time" to gachaUp[2]["end_time"].textValue(),
                "gacha_id" to gachaUp[2]["gacha_id"].textValue(),
                "r5_up_items" to test2(gachaUp[2]["gacha_id"].textValue(), "up")
            ),
            "武器活动" to mutableMapOf(
                "begin_time" to gachaUp[3]["begin_time"].textValue(),
                "end_time" to gachaUp[3]["end_time"].textValue(),
                "gacha_id" to gachaUp[3]["gacha_id"].textValue(),
                "r5_up_items" to test2(gachaUp[3]["gacha_id"].textValue(), "up")
            ),
        )


        val objectMapper = ObjectMapper()
        val jsonString = objectMapper.writeValueAsString(gachaUpMap)

        val outputStream = FileOutputStream(jsonFile, false)
        outputStream.write(jsonString.toByteArray())
        outputStream.close()
    }

    /**
     * 获取当期卡池数据
     */
    private fun getInfoList(): JsonNode {
        val currentDir = File(".").absoluteFile
//        val resourcesDir = File(currentDir, "resources")
        val jsonFile = File(currentDir, "resources/genshinConfig/gacha_up.json")

        if (jsonFile.exists()) {
            val gachaUp = objectMapper.readTree(jsonFile)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val lastModified = Date(jsonFile.lastModified())
            val formattedDate = dateFormat.format(lastModified)
            val endDate = dateFormat.parse(gachaUp["角色活动"]["end_time"].textValue())

            if (lastModified.after(endDate)) {
                println("Last modified date is after target date.")
            } else if (lastModified.before(endDate)) {
                println("Last modified date is before target date.")
            } else {
                println("Last modified date is equal to target date.")
            }


        } else {
            println("1.json file does not exist in resources directory.")
            test()
        }

        val data = mysApi.getData("gacha_Id")
        return data["data"]["list"]
    }

    fun gachaThread(authKeyB: JsonNode, gachaId: Int) {
        // 本页最后一条数据的id
        var endId = "0"
        for (i in 1..10000) {
            val gachaData = mysApi.getData(
                "gachaLog",
                mutableMapOf(
                    "authkey" to URLEncoder.encode(authKeyB["data"]["authkey"].textValue(), "UTF-8"),
                    "size" to 20,
                    "end_id" to endId,
                    "page" to i,
                    "gacha_type" to gachaId
                )
            )
            println("gachaData: $gachaData ")
            val length = gachaData["data"]["list"].size()
            endId = gachaData["data"]["list"][length - 1]["id"].textValue()

            Thread.sleep(500)
            if (length < 20) break
        }
    }

    fun getData(authKeyB: JsonNode) {
        val idList = listOf(301, 302, 200)
        for (id in idList) {
            gachaThread(authKeyB, id)
        }
        println("All threads finished")
    }


    @Listener("#抽卡记录", desc = "通过token获取抽卡记录", matchType = MatchType.REGEX_MATCHES)
    suspend fun GroupMessageEvent.getGachaLog(): JsonNode? {
        bot.launch {
            getInfoList()
        }
        val (outputStream, ticket) = qrLogin.makeQrCode()
        send("免责声明:您将通过扫码完成获取米游社sk以及ck。\n本Bot将不会保存您的登录状态。\n我方仅提供米游社查询及相关游戏内容服务,若您的账号封禁、被盗等处罚与我方无关。\n害怕风险请勿扫码~")

        val meg = send(outputStream.toExternalResource().uploadAsImage(group))
        bot.launch {
            delay(60000)
            meg.recall()
        }

        val (qrCodeStatus, checkQrCode) = qrLogin.checkQrCode(ticket)
        if (!checkQrCode) {
            reply("二维码过期，请重新获取")
            return null
        }
        val stoken = qrLogin.getStoken(qrCodeStatus)
        val accountInfo = qrLogin.getAccountInfo(stoken)

        mysApi = MysApi(
            accountInfo["data"]["list"][0]["game_uid"].textValue(),
            "mid=${stoken["data"]["user_info"]["mid"].textValue()};stoken=${stoken["data"]["token"]["token"].textValue()}"
        )


        val authKeyB = mysApi.getData("authKeyB")
        println("authKeyB:$authKeyB")

        getData(authKeyB)
        return null
    }
}