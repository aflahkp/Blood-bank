package com.muod.bloodbank.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.muod.bloodbank.R
import com.muod.bloodbank.model.DonorModel
import com.muod.bloodbank.model.UserModel
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.util.concurrent.TimeUnit

class SignIn : AppCompatActivity() {

    final var SIGN_IN_VIEW = 101
    final var OTP_VIEW = 102


    var phoneNumber = ""
    lateinit var firebaseAuth:FirebaseAuth


    var TAG = "SignIn"
    var storedVerificationId = ""
    lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    lateinit var db : FirebaseFirestore
    var uid = ""
    lateinit var userdata:DonorModel

    lateinit var preferences:SharedPreferences

    lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        firebaseAuth= FirebaseAuth.getInstance()

        db = FirebaseFirestore.getInstance()


        preferences =  getSharedPreferences("userdata", Context.MODE_PRIVATE)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                Toast.makeText(this@SignIn,e.localizedMessage,Toast.LENGTH_SHORT).show()

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                    verificationId: String?,
                    token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId!!)

                Toast.makeText(this@SignIn,"Code Sent",Toast.LENGTH_SHORT).show()

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                // ...
            }
        }

        skipTextView.setOnClickListener{
            this.skipLogin()
        }




        editTextPhone.addTextChangedListener (object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.toString().isNullOrEmpty()){
                    phoneTextInputLayout.isErrorEnabled =true
                    phoneTextInputLayout.error = "Phone number should not be empty"
                }
                else{
                    phoneTextInputLayout.error =null
                    phoneTextInputLayout.isErrorEnabled = false
                }
            }

        })


        otpTextInput.addTextChangedListener (object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if(p0!!.toString().isNullOrEmpty()){
                    lTextOTP.isErrorEnabled =true
                    lTextOTP.error = "Enter OTP"
                }
                else{
                    lTextOTP.error =null
                    lTextOTP.isErrorEnabled = false
                }
            }

        })

        btnLogin.setOnClickListener{v-> this.validateAndVerifyNumber()}
        btnChangeNumber.setOnClickListener{ v -> this.toggleView(SIGN_IN_VIEW) }
        btnVerify.setOnClickListener { v-> validateAndVerifyOTP() }


    }


    fun skipLogin(){
        var editor = preferences.edit()
        editor.putBoolean("skipLogin",true)
        editor.commit()
        editor.apply()
    }


    fun getUserData(uid:String){

        Log.d("uid",uid)
        db!!.collection("Users")
                .whereEqualTo("uid",uid)
                .get()
                .addOnSuccessListener { result ->
                if(result.size()>0){
                    Log.d("userdata",result.documents[0].data!!.toString())
//                     var userData = DonorModel(result.documents[0].data!!)
                    var editor = preferences.edit()
                    editor.putString("uid",uid)
                    editor.putString("phone",phoneNumber)
                    editor.putString("userName",result.documents[0].toObject(UserModel::class.java)!!.userName)
                    editor.putBoolean("isRegistered",true)
                    editor.commit()
                    editor.apply()
                    var intent = Intent(this@SignIn,HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()

                }
                    else{
                    Log.d("userdata","No Such Usr")
                    var intent = Intent(this@SignIn,Register::class.java)
                    intent.putExtra("uid",uid)
                    intent.putExtra("phone",phoneNumber)
                    startActivity(intent)
                    finish()
                }
                 }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")

                        val user = task.result?.user
                        getUserData((user!!.uid))
                        // ...
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                }
    }


    fun verifyNumber(phoneNumber:String){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,      // Phone number to verify
                60,               // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this,             // Activity (for callback binding)
                callbacks) // OnVerificationStateChangedCallbacks
    }


    fun verifiedReceivedOTP (otp:String){
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@SignIn, "Verification Success", Toast.LENGTH_SHORT).show()
                        val user = task.result?.user
                        getUserData((user!!.uid))
                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this@SignIn, "Verification Failed, Invalid credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                })

    }

    fun validateAndVerifyNumber(){
        if(editTextPhone.text.trim().length==10){
            phoneTextInputLayout.error = null
            phoneTextInputLayout.isErrorEnabled = false
            phoneNumber = editTextPhone.text.trim().toString()
            toggleView(OTP_VIEW)
            verifyNumber("+91"+editTextPhone.text.trim().toString())
        }
        else{
            phoneTextInputLayout.error = "Phone number should be 10 digits"
            phoneTextInputLayout.isErrorEnabled = true
        }
    }

    fun validateAndVerifyOTP(){
        if(otpTextInput.text.trim().length==6){
            lTextOTP.error = null
            lTextOTP.isErrorEnabled = false
            phoneNumber = otpTextInput.text.trim().toString()
            Log.d("OTP",otpTextInput.text.trim().toString())
            Log.d("VerificationId",storedVerificationId)
            verifiedReceivedOTP(otpTextInput.text.trim().toString())
        }
        else{
            lTextOTP.error = "Invalid OTP"
            lTextOTP.isErrorEnabled = true
        }
    }

    fun toggleView(viewType:Int){
        when(viewType){
            SIGN_IN_VIEW ->{
                llotp.visibility = View.GONE
                llsignin.visibility = View.VISIBLE

            }

            OTP_VIEW ->{
                llsignin.visibility = View.GONE
                llotp.visibility = View.VISIBLE
            }

        }
    }
}
