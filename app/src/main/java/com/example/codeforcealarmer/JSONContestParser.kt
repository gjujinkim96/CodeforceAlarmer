package com.example.codeforcealarmer

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.IllegalArgumentException

class JSONContestParser(val input: String) {
    enum class STATUS{
        OK, FAILED
    }

    var status : STATUS
        private set
    private var comment : String? = null
    var contests : ArrayList<Contest>? = null
        private set

    init {
        try {
            val rootJSON = JSONObject(input)
            status = when(rootJSON.getString("status")){
                "OK" -> STATUS.OK
                "FAILED" -> STATUS.FAILED
                else -> throw JSONException("Invalid status returned")
            }

            if (status == STATUS.FAILED){
                comment = rootJSON.getString("comment")
            }else{
                contests = arrayListOf()
                var result = rootJSON.getJSONArray("result")
                for (i in 0 until result.length()){
                    val entry = result.getJSONObject(i)
                    val id = entry.getInt("id")
                    val name = entry.getString("name")
                    val type = SCORE_SYSTEM.fromStr(entry.getString("type"))
                    val phase = PHASE.fromStr(entry.getString("phase"))
                    val durationSecs = entry.getLong("durationSeconds")
                    val startTimeSecs = getLongOrNull("startTimeSeconds", entry)
                    val relativeTimeSecs = getLongOrNull("relativeTimeSeconds", entry)
                    val websiteUrl = getStringOrNull("websiteUrl", entry)
                    contests?.add(Contest(id, name, type, phase, durationSecs,
                        startTimeSecs, relativeTimeSecs, websiteUrl))
                }
            }
        }catch (e : JSONException){
            Log.e(this::class.java.simpleName, e.toString())
            throw IllegalArgumentException()
        }
    }

    fun getLongOrNull(name: String, jsonObject: JSONObject) =
        try{
            jsonObject.getLong(name)
        }catch (e : JSONException){
            null
        }

    fun getStringOrNull(name: String, jsonObject: JSONObject) =
        try{
            jsonObject.getString(name)
        }catch (e : JSONException){
            null
        }
}