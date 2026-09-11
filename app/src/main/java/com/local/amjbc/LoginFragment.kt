package com.local.amjbc

import android.app.Fragment
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.local.amjbc.model.JSONParser
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.InputStreamReader

class LoginFragment : Fragment() {

    var login: Button? = null
    var username1: EditText? = null
    var pass1: EditText? = null
    var email: String? = null
    var password: String? = null
    var jParser = JSONParser()
    var success: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                             savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_login, container, false)

        username1 = rootView.findViewById<View>(R.id.username) as EditText
        pass1 = rootView.findViewById<View>(R.id.password) as EditText

        login = rootView.findViewById<View>(R.id.login) as Button
        login!!.setOnClickListener {
            val view = activity.currentFocus
            if (view != null) {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            val task = JSONLogin()
            task.execute()
        }

        setHasOptionsMenu(true)
        return rootView

    }

    private inner class JSONLogin : AsyncTask<String?, Void?, Void?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            email = username1!!.text.toString()
            password = pass1!!.text.toString()
        }

        override fun doInBackground(vararg params: String?): Void? {

            val hc = DefaultHttpClient()
            val message: String

            val p = HttpPost(url)
            val `object` = JSONObject()
            try {
                `object`.put("username", email.toString())
                `object`.put("password", password.toString())
            } catch (ex: Exception) {
            }
            try {
                message = `object`.toString()

                p.entity = StringEntity(message, "UTF8")
                p.setHeader("Content-type", "application/json")
                val resp = hc.execute(p)
                if (resp != null) {
                    val reader = BufferedReader(InputStreamReader(resp.entity.content,
                        "UTF-8"))
                    val json = reader.readLine()
                    val tokener = JSONTokener(json)
                    val finalResult = JSONObject(tokener)

                    if (finalResult.get("success").toString().contains("1")) {
                        success = 1
                    } else {
                        success = 0
                    }
                }
                Log.d("Status line", "" + resp.statusLine.statusCode)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null

        }

        override fun onPostExecute(result: Void?) {

            if (success == 1) {
                Toast.makeText(activity.applicationContext, "User found", Toast.LENGTH_LONG).show()
                val newFragment = AdminFragment()
                val transaction = fragmentManager.beginTransaction()
                activity.overridePendingTransition(R.animator.slide_in, R.animator.slide_out)
                transaction.replace(R.id.content_frame, newFragment)
                transaction.commit()

            } else {
                Toast.makeText(activity.applicationContext, "User Not found", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val url = "http://amj-bc.com/amjbc/check.php"
    }
}
