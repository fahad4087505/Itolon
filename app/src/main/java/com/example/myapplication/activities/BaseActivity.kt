package com.example.myapplication.activities
import android.app.AlertDialog
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.app.colepower.view.CustomProgressBar
import com.example.myapplication.R
import com.example.myapplication.prefrences.SharedPref
import kotlinx.android.synthetic.main.alert_dialog.view.*
import java.util.*

open class BaseActivity : AppCompatActivity() {
    val progressBar = CustomProgressBar()
    var myLocale: Locale? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val languageToLoad = "fr" // your language
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
        super.onCreate(savedInstanceState)
        changeActionBarColor()
        SharedPref.init(this)
//        setLocale("fr")
    }
    fun showDialog(message: String) {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null)
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView).setTitle("")
        val mAlertDialog = mBuilder.show()
        val errorMessageTextView = mDialogView.error_message
        val dividerView = mDialogView.divider_view
        val okButton = mDialogView.okButton
        val cancelButton = mDialogView.dialogCancelBtn
        cancelButton.visibility = View.GONE
        dividerView.visibility = View.GONE
        errorMessageTextView.text = message
        okButton.setOnClickListener {
            mAlertDialog.dismiss()
        }
        cancelButton.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }
     fun changeActionBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.BLACK
        }
    }
    fun setLocale(lang: String?) {
        myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.getDisplayMetrics()
        val conf: Configuration = res.getConfiguration()
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        ActivityCompat.finishAffinity(this)
//        val refresh =
//        startActivity(Intent(activity!!, LoginOptionActivity::class.java))
    }

}