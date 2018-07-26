package com.example.ijichiyoshihito.smartlockforpasswordssample

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.credentials.*
import com.google.android.gms.common.api.ResolvableApiException

class LoginService(private val activity: Activity) {

    companion object {
        const val RC_SAVE = 1
        const val RC_READ = 2
    }

    // 保存、読込時に使用
    private val credentialsClient: CredentialsClient =
        Credentials.getClient(activity, CredentialsOptions.Builder().forceEnableSaveDialog().build())

    // 保存処理
    fun save(email: String, pass: String, finish: () -> Unit) {
        val credential = Credential.Builder(email).setPassword(pass).build()
        credentialsClient.save(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                // ２回目以降の登録
                finish()
                return@addOnCompleteListener
            }
            val e = it.exception
            if (e is ResolvableApiException) {
                // 初回登録
                e.startResolutionForResult(activity, RC_SAVE)
            } else {
                // 登録失敗
                finish()
            }
        }
    }

    // 読込処理
    fun load(finish: (credential: Credential) -> Unit) {
        val request = CredentialRequest.Builder().setPasswordLoginSupported(true).build()
        credentialsClient.request(request).addOnCompleteListener {
            if (it.isSuccessful) {
                // 読込が１件
                finish(it.result.credential)
                return@addOnCompleteListener
            }
            val e = it.exception
            if (e is ResolvableApiException) {
                // 読込件数が複数件
                e.startResolutionForResult(activity, RC_READ)
            } else {
                e?.printStackTrace()
            }
        }
    }

    fun resolveActivityResult(requestCode: Int, resultCode: Int, data: Intent?,
                              finishLoad: (Credential) -> Unit, finishSave: () -> Unit) {
        when (requestCode) {
            RC_READ -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)?.let {
                        finishLoad(it)
                    }
                } else {
                    // 読込拒否
                }
            }
            RC_SAVE -> {
                if (resultCode == Activity.RESULT_OK) {
                    // 登録許可
                } else {
                    // 登録拒否
                }
                finishSave()
            }
        }
    }
}