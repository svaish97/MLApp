package vaish.saurabh.kailash

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var yr:Int =0
    var mnt:Int =0
    var day:Int =0
    var myDate=""
    var myTime=""
    var src=""
    var dst=""
    var hr=0
    var min=0
    var m=""
    var d=""
    var h=""
    var minu=""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pbar.visibility= View.GONE

        date.setOnClickListener {
            val cal= Calendar.getInstance()
             yr=cal.get(Calendar.YEAR)
             mnt=cal.get(Calendar.MONTH)
             day=cal.get(Calendar.DAY_OF_MONTH)
            val dialog=DatePickerDialog(this@MainActivity,DatePickerDialog.OnDateSetListener{view,year,month,dayOfMonth ->
                if(month<9){
                    m="0"+(month+1)
                }
                else{
                    m=(month+1).toString()
                }

                if(dayOfMonth<10){
                   d= "0$dayOfMonth"
                }
                else{
                    d=dayOfMonth.toString()
                }
                selectedDate.text= "$d-$m-$year"
                myDate="$d-$m-$year"
            },yr,mnt,day)
            dialog.show()
            }

        time.setOnClickListener {
            val cal = Calendar.getInstance()
            hr=cal.get(Calendar.HOUR)
            min=cal.get(Calendar.MINUTE)
            val dialog=TimePickerDialog(this@MainActivity,TimePickerDialog.OnTimeSetListener{
                view,hour,minute ->

                if(hour<10){
                    h="0$hour"
                }
                else{
                    h=hour.toString()
                }
                if (minute<10){
                    minu="0$minute"
                }
                else{
                    minu=minute.toString()
                }

                selectedTime.text= "$h:$minu"
                myTime="$h:$minu"
            },hr,min,false)
            dialog.show()
        }

        btn.setOnClickListener {
            if(source.text.toString()=="" || destination.text.toString()==""){
                Toast.makeText(baseContext,"Please Fill all the details",Toast.LENGTH_SHORT).show()
            }
            else{
                src=source.text.toString()
                dst=destination.text.toString()
                pbar.visibility=View.VISIBLE
                myresult.text=""
                myresult2.text=""
                val network=NetworkFetch()
                network.execute("http://192.168.43.117:5001/")

            }
        }
        }

    @SuppressLint("StaticFieldLeak")
    inner class NetworkFetch:AsyncTask<String,Void,String>(){
        override fun doInBackground(vararg params: String?): String? {
            val stringurl=params[0]
            val client=OkHttpClient().newBuilder().readTimeout(15,TimeUnit.MINUTES)
                .connectTimeout(15,TimeUnit.MINUTES)
                .writeTimeout(15,TimeUnit.MINUTES)
                .build()
            val JSON=MediaType.parse("application/json; charset=utf-8")
            val actualdata=JSONObject()
            actualdata.put("date",myDate)
            actualdata.put("time",myTime)
            actualdata.put("source",src)
            actualdata.put("dest",dst)
            val body=RequestBody.create(JSON,actualdata.toString())
            val request=Request.Builder().url(stringurl).post(body).build()
            val response=client.newCall(request).execute()
            return response.body()?.string()
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            val res= result!!.split("-")
            pbar.visibility=View.GONE
            myresult.text="Total Travel Time="+res[0]
            myresult2.text="Departure At="+res[1]
            super.onPostExecute(result)

        }
    }
    }



