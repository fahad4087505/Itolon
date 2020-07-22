package com.example.myapplication.activities

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import com.example.myapplication.R
import com.example.myapplication.prefrences.SharedPref
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login_option.*

class LoginOptionActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_option)
        playMusic()
        setVideoViewFullScreen()
        if(SharedPref.read(SharedPref.IS_LOGIN, false)){
            if (SharedPref.read(SharedPref.USER_TYPE, "").equals("user")) {
                val i = Intent(this@LoginOptionActivity, MainActivity::class.java)
                startActivity(i)
            }else{
                if(SharedPref.read(SharedPref.IS_FORM_SUBMITTED,false)){
                    startActivity(Intent(this@LoginOptionActivity, MainActivity::class.java).putExtra("verifyType","artist"))
                }else{
                    startActivity(Intent(this@LoginOptionActivity, MainActivity::class.java).putExtra("verifyType","artist"))
                }
            }
            finish()
        }
        inscrire_btn.setOnClickListener {
            getFcmToken()
            startActivity(Intent(this@LoginOptionActivity, SignupActivity::class.java))
            finish()
        }
        passoword_textview.setOnClickListener {
            getFcmToken()
            startActivity(Intent(this@LoginOptionActivity, LoginActivity::class.java))
            finish()
        }
    }
    private fun getFcmToken(){
        try {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result?.token
                SharedPref.write(SharedPref.REFRESH_TOKEN,token)
            })
        }catch (ignored:Exception){
        }
    }
    private fun setVideoViewFullScreen(){
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
//        val params =
//            parent_layout.getLayoutParams()
        var width = metrics.widthPixels
        var height = metrics.heightPixels
//        params.leftMargin = 0
        video_view.minimumWidth=width
        video_view.minimumHeight=height
//        video_view.setLayoutParams(params)
    }
    private fun playMusic() {
         val uri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.two)
        video_view.setVideoURI(uri)
        video_view.start()
        video_view.setOnPreparedListener { mp ->
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
            mp.isLooping = true
            mp.setVolume(0.0f,0.0f)
        }
    }
}