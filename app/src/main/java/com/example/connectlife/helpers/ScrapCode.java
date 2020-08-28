package com.example.connectlife.helpers;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.connectlife.MainActivity;
import com.example.connectlife.R;
import com.example.connectlife.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.tapadoo.alerter.Alerter;

import java.util.concurrent.TimeUnit;

public class ScrapCode {



    /*private void verifyAuth(PhoneAuthCredential phoneAuthCredential) {
        fAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Authentication is Successful", Toast.LENGTH_SHORT).show();
                    checkUserProfile();


                }else {
                    Alerter.create(activity)
                            .setTitle("Authentication Failed")
                            .setText("Please try again!")
                            .setBackgroundColorRes(R.color.colorPrimary)
                            .show();
                    // Toast.makeText(getContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                    phoneLayout.setVisibility(View.VISIBLE);
                    otpField.setVisibility(View.GONE);
                    RegisterBtn.setText("Continue");
                    verificationInProgress=false;
                }
            }
        });
    }

    public void requestOTP(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60L, TimeUnit.SECONDS, getActivity(), new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getContext(), "Code Sent!", Toast.LENGTH_SHORT).show();
                authenticationId = s;
                token=forceResendingToken;
                verificationInProgress = true;
                RegisterBtn.setText("Verify");
                phoneLayout.setVisibility(View.GONE);
                otpField.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                //  Toast.makeText(getContext(), "Cannot Create Account" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Alerter.create(activity)
                        .setTitle("Error")
                        .setText("Please try again!")
                        .setBackgroundColorRes(R.color.colorPrimary)
                        .show();
            }
        });
    }

    public void checkUserProfile(){

        final DocumentReference documentReference = firebaseFirestore.collection("users").document(fAuth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);

                }else{
                    WelcomeActivity.addDetailsPage();
                }
            }
        });
    }*/
}
