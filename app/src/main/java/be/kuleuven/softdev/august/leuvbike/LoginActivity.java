package be.kuleuven.softdev.august.leuvbike;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText edittextphone;
    EditText edittextcode;
    String codeSent;
    ConstraintLayout myLayout;
    AnimationDrawable animationdrawable;
    FirebaseUser user;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        mAuth = FirebaseAuth.getInstance();
        edittextcode = findViewById(R.id.code);
        edittextphone = findViewById(R.id.phone);

        // coole achtergrond
        myLayout = findViewById(R.id.mylayout);
        animationdrawable = (AnimationDrawable) myLayout.getBackground();
        animationdrawable.setEnterFadeDuration(4500);
        animationdrawable.setExitFadeDuration(4500);
        animationdrawable.start();

    }

    public void getCode (View v){
        sendverificationcode();
    }


    /*public void skip (View v){
        Intent lp = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(lp);
    }*/


    public void checkVerification (View v){
        verifySignInCode();
    }

    private void verifySignInCode() {
        String  code = edittextcode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void sendverificationcode() {

        phoneNumber = edittextphone.getText().toString();


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(),"Verification failed", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
            Toast.makeText(getApplicationContext(),"Verification sent", Toast.LENGTH_LONG).show();
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(getApplicationContext(),"Login Succesful", Toast.LENGTH_LONG).show();
                            user = task.getResult().getUser();
                            Intent in = new Intent(LoginActivity.this, MainActivity.class);
                            in.putExtra("phoneNumber", phoneNumber);
                            startActivity(in);
                            finish();

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(),"ERROR", Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),"INCORRECT CODE", Toast.LENGTH_LONG).show();

                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
