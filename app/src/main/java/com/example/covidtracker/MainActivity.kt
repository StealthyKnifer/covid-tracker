package com.example.covidtracker

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class MainActivity : AppCompatActivity() {
    private val apiUrl:String = "https://api.covid19india.org/data.json"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getJsonData()
        refresh.setOnClickListener {
            getJsonData()
        }
    }
    private fun getJsonData(){
        val client = OkHttpClient()
        val request = Request.Builder().url(apiUrl).build()
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.d("response", "failed")
                }
                val json = response.body()!!.string()
                val myData = JSONObject(json)
                runOnUiThread {
                    val timeSeries = myData.getJSONArray("cases_time_series")
                    Log.d("lets go ", timeSeries.get(timeSeries.length() - 1).toString())
                    val currTimeSeriesData = timeSeries.getJSONObject(timeSeries.length() - 1)
                    val currTotalData = myData.getJSONArray("statewise").getJSONObject(0)
                    val nf = java.text.NumberFormat.getIntegerInstance()
                    confirmedCasesTV.text = nf.format(Integer.parseInt(currTimeSeriesData.get("dailyconfirmed").toString()))
                    deathCasesTV.text = nf.format(Integer.parseInt(currTimeSeriesData.get("dailydeceased").toString()))
                    recoveredCasesTV.text = nf.format(Integer.parseInt(currTimeSeriesData.get("dailyrecovered").toString()))
                    activeCasesTV.text = nf.format(Integer.parseInt(currTotalData.get("active").toString()))
                    totalConfirmedCasesTV.text = nf.format(Integer.parseInt(currTotalData.get("confirmed").toString()))
                    totalDeathCasesTV.text = nf.format(Integer.parseInt(currTotalData.get("deaths").toString()))
                    totalRecoveredCasesTV.text = nf.format(Integer.parseInt(currTotalData.get("recovered").toString()))
                }
            }
        })
    }

}