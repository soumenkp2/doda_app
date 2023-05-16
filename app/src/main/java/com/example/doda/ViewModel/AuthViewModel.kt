package com.example.doda.ViewModel

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doda.Repository.AuthRepository
import com.google.firebase.auth.FirebaseUser


class AuthViewModel(application: Application, context: Context?) : ViewModel() {

    private var firebaseUserMutableLiveData: MutableLiveData<FirebaseUser?>? = null
    private var currentUser: FirebaseUser? = null
    private var repository: AuthRepository? = null

    fun getFirebaseUserMutableLiveData(): MutableLiveData<FirebaseUser?>? {
        return firebaseUserMutableLiveData
    }

    fun getCurrentUser(): FirebaseUser? {
        return currentUser
    }

    init
    {
        repository = context?.let { AuthRepository(application, it) }
        currentUser = repository!!.getCurrentUser()
        firebaseUserMutableLiveData = repository!!.getFirebaseUserMutableLiveData()
    }



    @RequiresApi(api = Build.VERSION_CODES.S)
    fun signUp(
        email: String?,
        pass: String?,
        username: String?
    ) {
        Log.d("viewmodel", "clicked")
        if (username != null) {
            repository?.signUp( username, email, pass )
        }
    }

    fun signIn(email: String?, pass: String?) {
        repository?.signIn(email, pass)
    }

    fun signOut() {
        repository?.signOut()
    }

}