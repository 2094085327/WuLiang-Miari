package pers.wuliang.robot.botApi.genShin.apps

import cn.hutool.extra.qrcode.QrCodeUtil
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.QuoteReply
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.springframework.stereotype.Component
import pers.wuliang.robot.botApi.genShin.MysApi
import pers.wuliang.robot.config.Listener
import pers.wuliang.robot.config.MatchType
import pers.wuliang.robot.util.MessageSender.reply
import pers.wuliang.robot.util.MessageSender.send
import java.io.ByteArrayOutputStream
import java.net.URLEncoder

/**
 *@Description:
 *@Author zeng
 *@Date 2023/9/8 10:13
 *@User 86188
 */
@Component
open class QRLogin {
    private val mysApi = MysApi("144853327", "")

    private suspend fun getQrCodeStatus(ticket: String): JsonNode {
        return mysApi.getData(
            "qrCodeStatus", mutableMapOf("device" to "CBEC8312-AA77-489E-AE8A-8D498DE24E90", "ticket" to ticket)
        )
    }

    fun makeQrCode(): Pair<ByteArray, String> {
        val ticketUrl = mysApi.getData("qrCode", mutableMapOf("device" to "CBEC8312-AA77-489E-AE8A-8D498DE24E90"))
        val url = ticketUrl["data"]["url"].textValue()
        val ticket = url.substringAfter("ticket=")
        val outputStream = ByteArrayOutputStream()
        QrCodeUtil.generate(
            url,
            300,
            300,
            "jpg",
            outputStream
        )

        return Pair(outputStream.toByteArray(), ticket)
    }

    suspend fun checkQrCode(ticket: String): Pair<JsonNode, Boolean> {
        var qrCodeStatus: JsonNode? = null
        while (qrCodeStatus?.get("data")?.get("stat")?.textValue() != "Confirmed") {
            delay(3000)
            qrCodeStatus = getQrCodeStatus(ticket)
            if (qrCodeStatus.get("message")?.textValue() == "ExpiredCode") {
                return Pair(qrCodeStatus, false)
            }
        }
        return Pair(qrCodeStatus, true)
    }

    fun getStoken(qrCodeStatus: JsonNode): JsonNode {
        val gameTokenRaw = qrCodeStatus["data"]["payload"]["raw"].textValue()
        val mapper = ObjectMapper()
        val tokenJson = mapper.readTree(gameTokenRaw)
        val accountId = tokenJson["uid"].textValue().toInt()


        return mysApi.getData(
            "getStokenByGameToken",
            mutableMapOf("accountId" to accountId, "gameToken" to tokenJson["token"].textValue())
        )
    }

    fun getAccountInfo(stoken: JsonNode): JsonNode {
        mysApi.cookie =
            "mid=${stoken["data"]["user_info"]["mid"].textValue()};stoken=${stoken["data"]["token"]["token"].textValue()}"

        return mysApi.getData("getAccountInfo")
    }

    fun getCookieStoken(qrCodeStatus: JsonNode): JsonNode {
        val gameTokenRaw = qrCodeStatus["data"]["payload"]["raw"].textValue()
        val mapper = ObjectMapper()
        val tokenJson = mapper.readTree(gameTokenRaw)
        val accountId = tokenJson["uid"].textValue().toInt()
        return mysApi.getData(
            "getCookieByGameToken",
            mutableMapOf("accountId" to accountId, "gameToken" to tokenJson["token"].textValue())
        )
    }

    fun getHk4eToken(cookieToken: JsonNode): ArrayList<JsonNode> {
        val servers = arrayOf("cn_gf01", "cn_qd01")
        val accountId = cookieToken["data"]["uid"].textValue()
        val hk4eArray = arrayListOf<JsonNode>()
        mysApi.cookie =
            "account_id=$accountId;cookie_token=${cookieToken["data"]["cookie_token"].textValue()}"
        for (server in servers) {
            val hk4eToken = mysApi.getData(
                "getHk4eByCookieToken",
                mutableMapOf("region" to server, "uid" to accountId)
            )

            if (hk4eToken["retcode"].textValue() != "-1002") {
                hk4eArray.add(hk4eToken)
            }
        }
        return hk4eArray
    }

    @Listener("#扫码登录", desc = "获取登录二维码", matchType = MatchType.REGEX_MATCHES)
    suspend fun GroupMessageEvent.getQRLogin() {
        send("免责声明:您将通过扫码完成获取米游社sk以及ck。\n本Bot将不会保存您的登录状态。\n我方仅提供米游社查询及相关游戏内容服务,若您的账号封禁、被盗等处罚与我方无关。\n害怕风险请勿扫码~")

        val (outputStream, ticket) = makeQrCode()
        send(outputStream.toExternalResource().uploadAsImage(group))
        val (qrCodeStatus, checkQrCode) = checkQrCode(ticket)
        if (!checkQrCode) {
            reply("二维码过期，请重新获取")
            return
        }
    }
}