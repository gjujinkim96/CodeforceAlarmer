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

    private val status : STATUS
    private var comment : String? = null
    private var contests : ArrayList<Contest>? = null

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
                    val name = entry.getString("name")

                    contests?.add(Contest(name))
                }
            }
        }catch (e : JSONException){
            Log.e(this::class.java.simpleName, e.toString())
            throw IllegalArgumentException()
        }
    }
}