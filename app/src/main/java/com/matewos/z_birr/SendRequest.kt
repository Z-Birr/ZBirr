package com.matewos.z_birr

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.util.concurrent.locks.ReentrantLock

class SendRequest {
    companion object {
        @Volatile
        private var respons = JSONObject()
        fun authorized(token: String?, method: Int,url: String, jsonObject: JSONObject?): JSONObject{

            val jsonObjectRequest = object : JsonObjectRequest(
                method, url, jsonObject,
                Response.Listener { response ->
                    respons = response
                    Log.i("Backend", "Response: %s".format(response.toString()))
                },
                Response.ErrorListener { error ->
                    Log.i("Backend", "Response: %s".format(error.toString()))
                }
            )
            {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Authorization"] =  "Token $token"
                    //..add other headers
                    return params
                }
            }

            MySingleton.getInstance(SplashScreen.instance.applicationContext).addToRequestQueue((jsonObjectRequest))
            return respons
        }

        fun unauthorized (url: String, method: Int, jsonObject: JSONObject?): JSONObject{

                val jsonObjectRequest = JsonObjectRequest(
                    method, url, jsonObject,
                    { response ->
                        respons = response
                        val sharedPref =
                            SplashScreen.instance.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)

                        try {
                            with(sharedPref?.edit()) {
                                this?.putString("Token", response.getString("key"))
                                this?.apply()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        Log.i("Backend", "Response: %s".format(response.toString()))
                    },
                    { error ->
                        Log.i("Backend", "Response: %s".format(error.toString()))

                    }
                )
                MySingleton.getInstance(SplashScreen.instance.applicationContext)
                    .addToRequestQueue((jsonObjectRequest))

            return respons
        }
    }
}