package com.example.weatherapp

//import android.app.DownloadManager.Request
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

const val API_KEY="1b5477407fcd0a5e98cc35ff5e6cd759"
class MainActivity : AppCompatActivity() {
    private lateinit var weather:TextView
    private lateinit var temp:TextView
    private lateinit var minTemp:TextView
    private lateinit var maxTemp:TextView
    private lateinit var sunrise_time:TextView
    private lateinit var sunset_time:TextView
    private lateinit var wind:TextView
    private lateinit var pressure:TextView
    private lateinit var humidity:TextView
    private lateinit var update_time:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getWeather("Moscow","Ru")

    }
    private fun getWeather(City:String,Country:String){
        val url="https://api.openweathermap.org/data/2.5/weather?q=$City,$Country&APPID=$API_KEY"
        val queue=Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET,
            url,
            {
                    response->
                val obj = JSONObject(response)
                findViewById<TextView>(R.id.city_text).text=obj.getString("name")
                //вытаскиваем основные данные
                var temp = obj.getJSONObject("main")
                findViewById<TextView>(R.id.temp_text).text=ToCelsius(temp.getString("temp"))+"\u00B0C"
                findViewById<TextView>(R.id.maxTemp).text="Max Temp: "+ToCelsius(temp.getString("temp_max"))+"\u00B0C"
                findViewById<TextView>(R.id.minTemp).text="Min Temp: "+ToCelsius(temp.getString("temp_min"))+"\u00B0C"
                findViewById<TextView>(R.id.pressure_text).text=temp.getString("pressure")
                findViewById<TextView>(R.id.humidity_text).text=temp.getString("humidity")
                //вытаскиваем скорость ветра
                temp=obj.getJSONObject("wind")
                findViewById<TextView>(R.id.wind_text).text=temp.getString("speed")
                //вытаскиваем текущую погоду
                var Arr=obj.getJSONArray("weather")
                temp= JSONObject(Arr[0].toString())
                findViewById<TextView>(R.id.weather_text).text=temp.getString("main")
                Log.d("MyLog","Volley res:$Arr")
                //вытаскиваем время обновления
                temp=obj.getJSONObject("sys")
                findViewById<TextView>(R.id.updateTime_text).text="Update at:"+ UpdateTime(obj.getString("dt"))
                //восход-закат
                temp=obj.getJSONObject("sys")
                findViewById<TextView>(R.id.sunrise_time).text=SunRiseSet(temp.getString("sunrise"))
                findViewById<TextView>(R.id.sunset_time).text=SunRiseSet(temp.getString("sunset"))

            },
            {
                Log.d("MyLog","Volley error: $it")
            }
        )
        queue.add(stringRequest)
    }
    private fun ToCelsius(Kelvin:String):String{
        var temp:Double=Kelvin.toDouble()
        temp=temp-273.15
        return temp.roundToInt().toString()
    }
    private fun UpdateTime(unixTime:String):String{
        val sdf = java.text.SimpleDateFormat("dd/M/yyyy hh:mm a")
        var temp:Long=unixTime.toLong()*1000
        val date = java.util.Date(temp)
        return sdf.format(date)
    }

    private fun SunRiseSet(unixTime: String):String{
        val sdf = java.text.SimpleDateFormat("hh:mm a")
        var temp:Long=unixTime.toLong()-28800
        val date = java.util.Date(temp*1000)
        return sdf.format(date)
    }

}