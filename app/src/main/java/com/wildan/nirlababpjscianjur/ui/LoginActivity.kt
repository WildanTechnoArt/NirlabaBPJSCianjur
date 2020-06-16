package com.wildan.nirlababpjscianjur.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.wildan.nirlababpjscianjur.R
import com.wildan.nirlababpjscianjur.utils.Constant.SEND_PHONE_NUMBER
import com.wildan.nirlababpjscianjur.utils.Constant.SERVICES_RESOLUTION_REQUEST
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var mAnalytics: FirebaseAnalytics? = null
    private var mAuth: FirebaseAuth? = null
    private var checkUser: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        checkPlayServices()
    }

    override fun onResume() {
        super.onResume()
        checkPlayServices()
    }

    override fun onStart() {
        super.onStart()
        mAuth?.addAuthStateListener(checkUser as FirebaseAuth.AuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        if (checkUser != null) {
            mAuth?.removeAuthStateListener(checkUser as FirebaseAuth.AuthStateListener)
        }
    }

    private fun initView() {
        mAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()
        checkUser = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
                intent.putExtra(SEND_PHONE_NUMBER, firebaseAuth.currentUser?.phoneNumber.toString())
                finish()
            }
        }
        btn_login.setOnClickListener {
            sendNumber()
        }
    }

    private fun sendNumber() {
        val phoneNumber = input_phone_number.text.toString().trim()

        if (TextUtils.isEmpty(input_phone_number.text.toString())) {
            Toast.makeText(applicationContext, "Masukan Nomor Telepon", Toast.LENGTH_SHORT).show()
        } else {
            val bundle = Bundle()
            val intent = Intent(this, VerificationActivity::class.java)
            bundle.putString(SEND_PHONE_NUMBER, phoneNumber)
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        }
    }

    private fun checkPlayServices(): Boolean {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(
                    this, result,
                    SERVICES_RESOLUTION_REQUEST
                ).show()
            }
            return false
        }
        return true
    }
}