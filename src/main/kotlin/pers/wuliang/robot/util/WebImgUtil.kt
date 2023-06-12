package pers.wuliang.robot.util

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.OutputType
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

/**
 *@Description:
 *@Author zeng
 *@Date 2023/6/11 15:05
 *@User 86188
 */
@Component
@PropertySource("classpath:config.properties")
@Configuration
class WebImgUtil {
    companion object {
        var driverPath: String = ""
        var defaultPath: String = ""
    }

    @Value("\${webImg.driverPath}")
    fun setDriverPath(path: String) {
        driverPath = path
    }

    @Value("\${webImg.defaultPath}")
    fun setDefaultPath(path: String) {
        defaultPath = path
    }

    fun getImg(url: String, imgPath: String? = null, width: Int, height: Int, sleepTime: Long = 0): ByteArray {

//        System.setProperty("webdriver.chrome.driver", driverPath)
        WebDriverManager.chromedriver().setup()
        val options = ChromeOptions()
        options.addArguments("--remote-allow-origins=*")
        options.addArguments("--headless")
        options.addArguments("--disable-gpu")
        options.addArguments("--no-sandbox")
        options.addArguments("--disable-dev-shm-usage")
        options.addArguments("--start-maximized")
        // 创建ChromeDriver实例
        val driver = ChromeDriver(options)

        // 访问本地网页
        driver.get(url)

        val widths = driver.executeScript("return document.documentElement.scrollWidth") as Long
        val heights = driver.executeScript("return document.documentElement.scrollHeight") as Long
        println("高度：$height 宽度：$width")


        // 设置窗口大小
        driver.manage().window().size = org.openqa.selenium.Dimension(widths.toInt(), heights.toInt())

        // 等待页面加载完成
        Thread.sleep(sleepTime)

        val srcFile: ByteArray = driver.getScreenshotAs(OutputType.BYTES)
        val bais = ByteArrayInputStream(srcFile)
        val image = ImageIO.read(bais)
        val writer = ImageIO.getImageWritersByFormatName("png").next()
        val iwp = writer.defaultWriteParam
        iwp.compressionMode = ImageWriteParam.MODE_EXPLICIT
        iwp.compressionQuality = 1f //Adjust the quality here
        val baos = ByteArrayOutputStream()
        writer.output = ImageIO.createImageOutputStream(baos)
        writer.write(null, IIOImage(image, null, null), iwp)

        val bytes = baos.toByteArray()

        Thread.sleep(500)
        // 关闭浏览器
        driver.quit()
        return bytes
    }
}