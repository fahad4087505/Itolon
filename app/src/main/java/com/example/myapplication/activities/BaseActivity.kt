package com.example.myapplication.activities
import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.app.colepower.view.CustomProgressBar
import com.example.myapplication.R
import com.example.myapplication.prefrences.SharedPref
import kotlinx.android.synthetic.main.alert_dialog.view.*

open class BaseActivity : AppCompatActivity() {
    val progressBar = CustomProgressBar()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeActionBarColor()
        SharedPref.init(this)
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
}