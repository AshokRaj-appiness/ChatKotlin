package com.example.chatkotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.chatkotlin.utils.Utils
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.*

class SignInActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1
    private val SIGN_IN_PROVIDERS = listOf(AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(true).setRequireName(true).build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        account_sign_in.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(SIGN_IN_PROVIDERS)
                .setLogo(R.drawable.ic_account_circle_black_24dp).build()
            startActivityForResult(intent, RC_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val progressDialog = indeterminateProgressDialog("Setting up your account")
                Utils.initCurrentUserIfFirstTime {
                    startActivity(intentFor<HomeActivity>().newTask().clearTask())
                    progressDialog.dismiss()
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                if (response == null) return

                when (response.error?.errorCode) {
                    ErrorCodes.NO_NETWORK ->
                        Log.e("error=>","No network")
                    ErrorCodes.UNKNOWN_ERROR ->
                        Log.e("error=>","Unknown error")
                }
            }
        }
    }
}
