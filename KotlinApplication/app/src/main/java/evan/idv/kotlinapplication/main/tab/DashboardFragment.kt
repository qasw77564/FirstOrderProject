package evan.idv.kotlinapplication.main.tab


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import evan.idv.kotlinapplication.ApiUrl
import evan.idv.kotlinapplication.R


class DashboardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_dashboard, container, false)
        getViews(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getViews(view : View){
//        val i : Int = 0
        val c : Int = 1
        var count : Int = 0
        val textView2 : TextView = view.findViewById(R.id.dashhText)
        val textView : TextView = view.findViewById(R.id.dashText)
        val btn : Button = view.findViewById(R.id.dashBtn)
        btn.setOnClickListener(View.OnClickListener {
            count += c
            textView.setText(count.toString())
            for (i in 1..10){
                count += i
                textView2.setText(count.toString())
            }
        })
        textView.setOnClickListener(View.OnClickListener {
            count = 0
            textView.setText(count.toString())
        })
    }

}
