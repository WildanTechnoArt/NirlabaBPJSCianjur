package com.wildan.nirlababpjscianjur.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.firebase.firestore.FirebaseFirestore
import com.wildan.nirlababpjscianjur.R
import com.wildan.nirlababpjscianjur.utils.Constant.SEND_PHONE_NUMBER
import com.wildan.nirlababpjscianjur.utils.Validation.Companion.validate
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class RegisterActivity : AppCompatActivity() {

    private var mPetani: String? = null
    private var mKec: String? = null
    private var mDesa: String? = null
    private var mSegmen: String? = null
    private var mSubsegmen: String? = null
    private var mPhone: String? = null
    private var mUbinan: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Daftar Pengguna Baru"
        bindProgressButton(btn_register)
        btn_register.attachTextChangeAnimator()
        btn_register.setOnClickListener {
            saveDataUser()
        }
    }

    private fun saveDataUser() {
        mPhone = intent?.getStringExtra(SEND_PHONE_NUMBER).toString()

        mPetani = input_namapetani.text.toString()
        mDesa = input_des.text.toString()
        mSegmen = input_segmen.text.toString()
        mSubsegmen = input_subsegmen.text.toString()
        mKec = input_kec.text.toString()
        mUbinan = input_ubinan.text.toString()

        if (validate(mPhone) || validate(mPetani) || validate(mDesa) || validate(mSegmen) ||
            validate(mSubsegmen) || validate(mKec) || validate(mUbinan)
        ) {
            Toast.makeText(
                this,
                "Tidak Boleh ada Data Yang Kosong",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            btn_register.showProgress { progressColor = Color.WHITE }
            val data = hashMapOf<String, String>()
            data["namaPetani"] = mPetani.toString()
            data["desa"] = mDesa.toString()
            data["segmen"] = mSegmen.toString()
            data["subsegmen"] = mSubsegmen.toString()
            data["kecamatan"] = mKec.toString()
            data["tglUbinan"] = mUbinan.toString()
            data["nomorTelepon"] = mPhone.toString()

            val db = FirebaseFirestore.getInstance()
            db.collection("daftarPetani")
                .document(mPhone.toString())
                .set(data)
                .addOnSuccessListener {
                    btn_register.hideProgress(R.string.btn_register)

                    Toast.makeText(
                        this,
                        "Registrasi Berhasil",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(SEND_PHONE_NUMBER, mPhone)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    btn_register.hideProgress(R.string.btn_register)
                    Toast.makeText(
                        this,
                        it.localizedMessage?.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}