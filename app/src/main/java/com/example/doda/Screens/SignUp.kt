package com.example.doda.Screens

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.NumberPickerBindingAdapter.setValue
import androidx.lifecycle.ViewModelProvider

import com.example.doda.R
import com.example.doda.User_info
import com.example.doda.ViewModel.AuthViewModel
import com.example.doda.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class SignUp : AppCompatActivity() {

    private lateinit var binding : ActivitySignUpBinding
    private lateinit var authViewModel : AuthViewModel
    private var progressDialog: ProgressDialog? = null
    private var firebaseAuth: FirebaseAuth? = null




    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)
        firebaseAuth = FirebaseAuth.getInstance()



        //authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.registerBtn.setOnClickListener(View.OnClickListener {
            Toast.makeText(applicationContext,binding.emailid.text.toString(),Toast.LENGTH_SHORT).show()
            signUp(binding.emailid.text.toString(),binding.password.text.toString(),binding.username.text.toString())
        })

        binding.signinBtn.setOnClickListener {
            signIn()
        }

        binding.signTxt.setOnClickListener {

            if(binding.signTxt.text.equals("Register Now"))
            {
                binding.username.visibility = View.VISIBLE
                binding.registerBtn.visibility = View.VISIBLE
                binding.signinBtn.visibility = View.GONE
                binding.signTxt.text = "Already a user? Sign In"
            }
            else{
                binding.username.visibility = View.GONE
                binding.registerBtn.visibility = View.GONE
                binding.signinBtn.visibility = View.VISIBLE
                binding.signTxt.text = "Register Now"
            }

        }

        if(firebaseAuth!!.currentUser!=null)
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

    private fun signIn() {
        // Perform the sign-in process

        firebaseAuth?.signInWithEmailAndPassword(binding.emailid.text.toString(), binding.password.text.toString())
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in is successful
                    val user: FirebaseUser? = firebaseAuth?.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    // You can perform any additional actions here
                } else {
                    // Sign-in failed
                    // Handle the error
                    val exception = task.exception
                    // ...
                }
            }
    }

    fun signUp(
        email: String,
        pass: String?,
        username: String?
    ) {
        //Log.d("repository", "clicked")
//        progressDialog = ProgressDialog(context)
//        progressDialog!!.setTitle("Please Wait..")
//        progressDialog!!.setMessage("We are creating your account")
//        progressDialog!!.setCancelable(false)
//        progressDialog!!.show()
        firebaseAuth!!.createUserWithEmailAndPassword(email, pass!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val user_info = User_info(username, email, pass)
                    Log.d(
                        "id",
                        Objects.requireNonNull(FirebaseAuth.getInstance().currentUser)?.getUid()
                            .toString()
                    )

                    FirebaseDatabase.getInstance()
                        .reference
                        .child("Users")
                        .child(FirebaseAuth.getInstance().currentUser?.getUid().toString())
                        .setValue(user_info) //.updateChildren((Map<String, Object>) user_info)
                        .addOnSuccessListener { //sent verificaion email
                            //sendVerificationEmail()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                applicationContext,
                                e.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    //val user = firebaseAuth!!.currentUser
                    //updateUI(user)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    //Toast.makeText(context, "User SignUp Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "signInWithEmail:failure", task.exception)

                    Toast.makeText(applicationContext, "User not verified sucessfully", Toast.LENGTH_SHORT)
                        .show()
                }
            }

    }

    fun Sign_Up(mail: String,pass: String,username: String)
    {

}



}
