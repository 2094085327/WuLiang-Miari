package pers.wuliang.robot.botApi.geography

import net.coobird.thumbnailator.Thumbnails
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.springframework.stereotype.Component
import pers.wuliang.robot.config.Listener
import pers.wuliang.robot.config.MatchType
import pers.wuliang.robot.util.MessageSender.send
import pers.wuliang.robot.util.WebImgUtil
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO


/**
 *@Description:
 *@Author zeng
 *@Date 2023/6/11 15:14
 *@User 86188
 */
@Component
class GeoMain {
    /**
     * 将图片转换为InputStream
     */
    fun getInputStreamImg(byte: ByteArray, width: Int? = null, height: Int? = null): ExternalResource {
        val inputStream = ByteArrayInputStream(byte)
        val thumbnail = Thumbnails.of(inputStream)
            .scale(1.0)
            .asBufferedImage()
        val ex = ByteArrayOutputStream().use { bos ->
            ImageIO.setUseCache(false)
            ImageIO.write(thumbnail, "png", bos)
            bos.toByteArray()
        }.toExternalResource()
        return ex
    }

    @Listener("{city}天气", desc = "获取天气图片", matchType = MatchType.REGEX_MATCHES)
    suspend fun GroupMessageEvent.getWeatherImg(city: String) {
        val geoApi = GetGeoApi()
        if (!geoApi.checkCode(geoApi.getWeatherData(city))) {
            send("没有找到'${city}'的信息，请检查是否输入错误")
            return
        }
        val byte =
            WebImgUtil().getImg(url = "http://localhost:${WebImgUtil.usePort}/weather")

        val ex = getInputStreamImg(byte)

        val img: Image = ex.uploadAsImage(group)
        send(img)
        ex.closed
        System.gc()
    }

    @Listener("{city}地理", desc = "获取地理信息图片", matchType = MatchType.REGEX_MATCHES)
    suspend fun GroupMessageEvent.getGeoImg(city: String) {
        val geoApi = GetGeoApi()
        if (!geoApi.checkCode(geoApi.getCityData(city))) {
            send("没有找到'${city}'的信息，请检查是否输入错误")
            return
        }
        val byte =
            WebImgUtil().getImg(url = "http://localhost:${WebImgUtil.usePort}/geo")

        val ex = getInputStreamImg(byte)

        val img: Image = ex.uploadAsImage(group)
        send(img)
        ex.closed
        System.gc()
    }
}