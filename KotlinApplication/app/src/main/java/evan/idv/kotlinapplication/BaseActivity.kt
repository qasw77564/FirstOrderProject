package evan.idv.kotlinapplication

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

open class BaseActivity : AppCompatActivity() {

    inner class doAsync(private val url: String, private val req: String, private val onSuccess: (resp: String) -> Unit, private val onFail: (err_msg: String) -> Unit) : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg p0: String?): String? {

            val httpClient: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            httpClient.requestMethod = "GET"
            httpClient.permission
            if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
                try {
                    val stream = BufferedInputStream(httpClient.inputStream)
                    val data: String = readStream(inputStream = stream)
                    onSuccess(data)
                    return "complete"
                } catch (e: Exception) {
                    onFail("ERROR ${e.message}")
                } finally {
                    httpClient.disconnect()
                }
            } else {
                println("ERROR ${httpClient.responseCode}")
                onFail("ERROR ${httpClient.responseCode}")
            }
            return "complete"
        }

        private fun readStream(inputStream: BufferedInputStream): String {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it) }
            return stringBuilder.toString()
        }

        override fun onCancelled() {
            super.onCancelled()
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onCancelled(result: String?) {
            Log.i("onCancelled", result)
            super.onCancelled(result)
        }

        override fun onPostExecute(result: String?) {
            Log.i("onPostExecute", result)
            super.onPostExecute(result)
        }

        override fun onProgressUpdate(vararg values: Int?) {
            Log.i("onProgressUpdate", values.toString())
            super.onProgressUpdate(*values)
        }
    }

    inline fun String.get(req: String, crossinline onSuccess: (re: String) -> Unit, crossinline onFail: (err_msg: String) -> Unit) {

        doAsync(this, "", { it ->
            onSuccess(it)
        }, { it ->
            onFail(it)
        }).execute()
    }
}