package evan.idv.kotlinapplication.menu


import android.media.Image
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import evan.idv.kotlinapplication.R

class CameraFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_camera, container, false)
        getViews(view)
        return view
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

    private fun getViews(view : View){
        val photoImage : ImageView = view.findViewById(R.id.photoImage)
        val loginBtn : Button = view.findViewById(R.id.loginBtn)
        val register : TextView = view.findViewById(R.id.register)
        val forgetPasswd : TextView = view.findViewById(R.id.forgetPasswd)
        var account : EditText = view.findViewById(R.id.account)
        var password : EditText = view.findViewById(R.id.password)

    }
}
