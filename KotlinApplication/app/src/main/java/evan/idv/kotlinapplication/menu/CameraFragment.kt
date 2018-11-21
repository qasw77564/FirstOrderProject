package evan.idv.kotlinapplication.menu


import android.content.Context
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import evan.idv.kotlinapplication.R

class CameraFragment : Fragment() {

    companion object {
        private lateinit var context : Context
        //google login
        private val RC_SIGN_IN = 1144
        private lateinit var mAuth: FirebaseAuth
        // [END declare_auth]
        private lateinit var googleSignInClient: GoogleSignInClient

    }

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
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(Companion.context, gso)
        mAuth = FirebaseAuth.getInstance()
    }

//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = mAuth.currentUser
//
//    }

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
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

}
