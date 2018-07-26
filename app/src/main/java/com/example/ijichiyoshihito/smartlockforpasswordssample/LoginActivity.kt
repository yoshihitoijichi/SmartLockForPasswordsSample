package com.example.ijichiyoshihito.smartlockforpasswordssample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.ijichiyoshihito.smartlockforpasswordssample.R.id.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){

    private lateinit var service: LoginService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        service = LoginService(this)

        email_sign_in_button.setOnClickListener {
            service.save(email.text.toString(), password.text.toString()){
                // 保存完了後処理
            }
        }

        service.load {
            email.setText(it.id)
            password.setText(it.password)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LoginService.RC_READ, LoginService.RC_SAVE ->
                service.resolveActivityResult(requestCode, resultCode, data, {
                    // 読み込み完了後処理
                    email.setText(it.id)
                    password.setText(it.password)
                }, {
                    // 保存完了後処理
                })
        }
    }
}
