package com.wildan.nirlababpjscianjur.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.wildan.nirlababpjscianjur.R
import com.wildan.nirlababpjscianjur.utils.Constant.SEND_PHONE_NUMBER
import kotlinx.android.synthetic.main.activity_verification.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.concurrent.TimeUnit

class VerificationActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mVerificationId: String? = null
    private var mToken: PhoneAuthProvider.ForceResendingToken? = null
    private var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mPhoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        initView()
    }

    private fun getDataReg() {
        mPhoneNumber = intent?.getStringExtra(SEND_PHONE_NUMBER).toString()
        tv_phone_number.text = mPhoneNumber
    }

    private fun sendCode() {
        setupVerificationCallBack()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mPhoneNumber.toString(),
            60,
            TimeUnit.SECONDS,
            this,
            mCallBack as PhoneAuthProvider.OnVerificationStateChangedCallbacks
        )
    }

    private fun resendCode() {
        getDataReg()
        setupVerificationCallBack()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mPhoneNumber.toString(),
            60,
            TimeUnit.SECONDS,
            this,
            mCallBack as PhoneAuthProvider.OnVerificationStateChangedCallbacks,
            mToken
        )
        progress_bar.visibility = View.VISIBLE
        tv_progmsg.text = getString(R.string.resend_code)
        btn_resend_code.isEnabled = false
    }

    private fun setupVerificationCallBack() {
        mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                mVerificationId = p0
                mToken = p1
                progress_bar.visibility = View.VISIBLE
                tv_progmsg.text = getString(R.string.verification_process)
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                Toast.makeText(applicationContext, "Verification Time Out!", Toast.LENGTH_SHORT)
                    .show()
                progress_bar.visibility = View.GONE
                tv_progmsg.text = ""
                btn_resend_code.visibility = View.VISIBLE
                btn_resend_code.isEnabled = true
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                tv_progmsg.text = ""
                progress_bar.visibility = View.GONE
                signInWithPhoneAuthCredential(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(
                    applicationContext,
                    "Verifikasi Gagal, Silakan coba lagi",
                    Toast.LENGTH_SHORT
                ).show()
                progress_bar.visibility = View.GONE
                tv_progmsg.text = ""
                btn_resend_code.visibility = View.VISIBLE
                btn_resend_code.isEnabled = true
            }
        }
    }

    private fun verificationNumber() {
        try {
            val code = input_phone_number.text.toString()
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(applicationContext, "Masukan Kode Verifikasi", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val credential: PhoneAuthCredential =
                    PhoneAuthProvider.getCredential(mVerificationId as String, code)
                signInWithPhoneAuthCredential(credential)
                progress_bar.visibility = View.VISIBLE
                tv_progmsg.text = getString(R.string.tv_process)
            }
        } catch (ex: Exception) {
            progress_bar.visibility = View.GONE
            tv_progmsg.text = ""
            btn_resend_code.visibility = View.VISIBLE
            btn_resend_code.isEnabled = true
            Toast.makeText(applicationContext, "Kode Yang Anda Masukan Salah", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkData()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            applicationContext,
                            "Kode Yang Dimasukan Tidak Valid",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    btn_resend_code.isEnabled = true
                    progress_bar.visibility = View.GONE
                    tv_progmsg.text = ""
                }
            }
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Verifikasi"

        btn_resend_code.isEnabled = false
        //btn_resend_code.setTextColor(Color.parseColor("#FFD2D2D2"))

        mAuth = FirebaseAuth.getInstance()
        mAuth?.setLanguageCode("id")
        getDataReg()
        sendCode()

        btn_verify.setOnClickListener {
            verificationNumber()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(layout.windowToken, 0)
        }

        btn_resend_code.setOnClickListener {
            resendCode()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(btn_resend_code.windowToken, 0)
        }
    }

    private fun checkData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("daftarPetani")
            .document(mPhoneNumber.toString())
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(SEND_PHONE_NUMBER, mPhoneNumber.toString())
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, RegisterActivity::class.java)
                    intent.putExtra(SEND_PHONE_NUMBER, mPhoneNumber.toString())
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    it.localizedMessage?.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}