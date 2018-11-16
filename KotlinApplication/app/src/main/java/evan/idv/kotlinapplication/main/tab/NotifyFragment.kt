package evan.idv.kotlinapplication.main.tab


import android.nfc.Tag
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import evan.idv.kotlinapplication.R
import kotlinx.android.synthetic.main.fragment_notify.*


class NotifyFragment : Fragment() {
    val TAG : String = NotifyFragment::class.java.simpleName as String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_notify, container, false)
        getViews(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        priceCalculate()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun getViews(view : View) {
        val notifyEdText : EditText = view.findViewById(R.id.notifyEdText)
        val notifyBtn : Button = view.findViewById(R.id.notifyBtn)
        val disCount : TextView = view.findViewById(R.id.disCount)
        val disCountPrice : TextView = view.findViewById(R.id.disCountPrice)
        val notifyPrice : TextView = view.findViewById(R.id.notifyPrice)
        val notifyDisCountPrice : TextView = view.findViewById(R.id.notifyDisCountPrice)
        val notifySeeBar : SeekBar = view.findViewById(R.id.notifySeeBar)

        notifyEdText.text.toString()


    }

    private fun priceCalculate(){

    }

}
