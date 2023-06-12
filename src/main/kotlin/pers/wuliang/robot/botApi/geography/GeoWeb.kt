package pers.wuliang.robot.botApi.geography

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

/**
 *@Description:
 *@Author zeng
 *@Date 2023/6/11 16:56
 *@User 86188
 */
@Controller
class GeoWeb {
    @RequestMapping("/weather")
    fun addWeatherInfo(model: Model): String {
        val geoData = GetGeoApi().getGeoData()
        val cityJson = geoData.cityJson!!
        val weatherJson = geoData.weatherJson!!
        val dailyJson = geoData.dailyJson!!
        val preJson = geoData.prediction!!

        val city = cityJson["location"][0]["name"].textValue()
        val time = weatherJson["updateTime"].textValue().replace("T", " ").replace("+08:00", "")
        val temp = weatherJson["now"]["temp"].textValue()
        val text = weatherJson["now"]["text"].textValue()
        val feelsLike = weatherJson["now"]["feelsLike"].textValue()
        val carText = dailyJson["daily"][1]["text"].textValue()
        val sportText = dailyJson["daily"][0]["text"].textValue()
        val humidity = weatherJson["now"]["humidity"].textValue()
        val pressure = weatherJson["now"]["pressure"].textValue()
        val vis = weatherJson["now"]["vis"].textValue()
        val windScale = weatherJson["now"]["windScale"].textValue()
        val windSpeed = weatherJson["now"]["windSpeed"].textValue()
        val windDir = weatherJson["now"]["windDir"].textValue()
        val wind360 = weatherJson["now"]["wind360"].textValue()
        val fxDate = preJson["daily"][0]["fxDate"].textValue()
        val sunrise = preJson["daily"][0]["sunrise"].textValue()
        val sunset = preJson["daily"][0]["sunset"].textValue()
        val textDay = preJson["daily"][0]["textDay"].textValue()
        val textNight = preJson["daily"][0]["textNight"].textValue()
        val tempMax = preJson["daily"][0]["tempMax"].textValue()
        val tempMin = preJson["daily"][0]["tempMin"].textValue()

        model.addAttribute("city", city)
        model.addAttribute("time", time)
        model.addAttribute("temp", "${temp}°C $text")
        model.addAttribute("tempNow", "${temp}°C")
        model.addAttribute("carText", carText)
        model.addAttribute("sportText", sportText)
        model.addAttribute("feelsLike", "${feelsLike}°C")
        model.addAttribute("humidity", "${humidity}%")
        model.addAttribute("text", text)
        model.addAttribute("pressure", "$pressure hpa")
        model.addAttribute("vis", "$vis km")
        model.addAttribute("windDir", "${windDir}${wind360}°")
        model.addAttribute("windInfo", "${windScale}级-${windSpeed} m/s")
        model.addAttribute("fxDate", fxDate)
        model.addAttribute("sunrise", sunrise)
        model.addAttribute("sunset", sunset)
        model.addAttribute("textDay", textDay)
        model.addAttribute("textNight", textNight)
        model.addAttribute("tempMax", "${tempMax}°C")
        model.addAttribute("tempMin", "${tempMin}°C")

        return "Geography/Geography"
    }
}