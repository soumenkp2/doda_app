package com.example.doda.Repository

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import com.example.doda.User_info
import com.example.doda.Screens.MainActivity

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.FirebaseDatabase
import java.io.Closeable
import java.util.*


class AuthRepository(application: Application, context: Context) {


    private var application: Application;
    private var firebaseUserMutableLiveData: MutableLiveData<FirebaseUser?>? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var context : Context? = null
    private var progressDialog: ProgressDialog? = null

    init
    {
        this.application = application
//        this.context = context
        firebaseUserMutableLiveData = MutableLiveData()
        firebaseAuth = FirebaseAuth.getInstance()
    }


    fun getFirebaseUserMutableLiveData(): MutableLiveData<FirebaseUser?>? {
        return firebaseUserMutableLiveData
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth!!.currentUser
    }

    fun AuthRepository(application: Application?, context: Context?) {


    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    fun signUp(
        email: String,
        pass: String?,
        username: String?
    ) {
        //Log.d("repository", "clicked")
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle("Please Wait..")
        progressDialog!!.setMessage("We are creating your account")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
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
                        .child(getCurrentUser()?.uid.toString())
                        .setValue(user_info) //.updateChildren((Map<String, Object>) user_info)
                        .addOnSuccessListener { //sent verificaion email
                            //sendVerificationEmail()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                e.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    //val user = firebaseAuth!!.currentUser
                    //updateUI(user)
                    val intent = Intent(context, MainActivity::class.java)
                    context?.startActivity(intent)

                    //Toast.makeText(context, "User SignUp Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "User not verified sucessfully", Toast.LENGTH_SHORT)
                        .show()
                }
            }

    }



    private fun sendVerificationEmail() {
        Objects.requireNonNull(FirebaseAuth.getInstance().currentUser)
            ?.sendEmailVerification()
            ?.addOnSuccessListener(OnSuccessListener<Void?> {
                progressDialog!!.dismiss()
                firebaseUserMutableLiveData!!.postValue(firebaseAuth!!.currentUser)
                Log.d("link sent", "yes sent")
                Toast.makeText(
                    context,
                    "Email Verification link sent to your email. please verifiy and then signin",
                    Toast.LENGTH_SHORT
                ).show()
//                val intent = Intent(context, SignInActivity::class.java)
//                context.startActivity(intent)
            })
            ?.addOnFailureListener(OnFailureListener {
                progressDialog!!.dismiss()
                Toast.makeText(context, "Email not sent", Toast.LENGTH_SHORT).show()
            })
    }

    fun signIn(email: String?, pass: String?) {
//        if (email != null) {
//            if (pass != null) {
//                firebaseAuth?.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull(Task<AuthResult>) task) {
//                        if (task.isSuccessful()){
//                            firebaseUserMutableLiveData.postValue(firebaseAuth.getCurrentUser());
//                        }else{
//                            Toast.makeText(application, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })
//            }
//        };
    }

    fun signOut() {
        firebaseAuth!!.signOut()
    }

}