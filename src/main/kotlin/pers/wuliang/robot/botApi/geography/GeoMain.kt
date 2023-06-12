package pers.wuliang.robot.botApi.geography

import net.coobird.thumbnailator.Thumbnails
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.Image
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
    @Listener("{city}天气", desc = "获取天气图片", matchType = MatchType.REGEX_MATCHES)
    suspend fun GroupMessageEvent.getGeoImg(city: String) {
        GetGeoApi().getWeatherData(city)
        val byte = WebImgUtil().getImg(url = "http://localhost:9600/weather", width = 450, height = 702)

        val inputStream = ByteArrayInputStream(byte)
        val thumbnail = Thumbnails.of(inputStream)
            .scale(1.0)
            .asBufferedImage()
        val ex = ByteArrayOutputStream().use { bos ->
            ImageIO.setUseCache(false)
            ImageIO.write(thumbnail, "png", bos)
            bos.toByteArray()
        }.toExternalResource()

        val img: Image = ex.uploadAsImage(group)
        send(img)
        ex.closed
    }
}