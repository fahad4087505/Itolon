package com.example.myapplication.utils

import android.content.Context
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.example.myapplication.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.kishandonga.csbx.CustomSnackbar
import io.reactivex.rxjava3.subjects.PublishSubject

object ViewUtils {
    fun showSnackBar(context:Context,message:String,hideAction:Boolean,title:String): PublishSubject<Boolean> {
        val res= PublishSubject.create<Boolean>()
        CustomSnackbar(context).show {
            customView(R.layout.snack_layout)
            padding(10)
            if(!hideAction) {
                duration(BaseTransientBottomBar.LENGTH_INDEFINITE)
            }
            else{
                duration(BaseTransientBottomBar.LENGTH_LONG)
            }
            withCustomView {
                it.findViewById<TextView>(R.id.message_textview).text = message
                if(!hideAction) {
                    duration(BaseTransientBottomBar.LENGTH_INDEFINITE)
                    it.findViewById<View>(R.id.btnUndo).visibility= View.VISIBLE
                    it.findViewById<TextView>(R.id.btnUndo).text = title
                    it.findViewById<TextView>(R.id.btnUndo).setOnClickListener {
                        dismiss()
                        res.onNext(true)
                    }
                }
                else{
                    duration(BaseTransientBottomBar.LENGTH_LONG)
                }
            }
        }
        return res
    }

    fun showMaterialDialog(context: Context,messageShow: String):PublishSubject<Boolean>{
        val res=PublishSubject.create<Boolean>()
        MaterialDialog(context).show {
            title(R.string.app_name)
            message(text=messageShow)
            positiveButton(R.string.agree) { dialog ->
                // Do something
                res.onNext(true)
            }
            negativeButton(R.string.disagree) { dialog ->
                // Do something
            }
        }
        return res
    }
}