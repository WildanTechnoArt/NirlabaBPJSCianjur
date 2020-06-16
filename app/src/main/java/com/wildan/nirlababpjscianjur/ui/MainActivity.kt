package com.wildan.nirlababpjscianjur.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wildan.nirlababpjscianjur.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        btn_logout.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
                .setTitle("Konfirmasi")
                .setMessage("Anda Yakin Ingin Keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    btn_logout.showProgress { progressColor = Color.WHITE }

                    AuthUI.getInstance()
                        .signOut(this)
                        .addOnSuccessListener {
                            btn_logout.hideProgress(R.string.btn_logout)

                            Toast.makeText(
                                this,
                                getString(R.string.request_logout),
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }.addOnFailureListener {
                            btn_logout.hideProgress(R.string.btn_logout)

                            Toast.makeText(
                                this,
                                getString(R.string.request_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }

            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun initView() {
        bindProgressButton(btn_logout)
        btn_logout.attachTextChangeAnimator()
    }
}