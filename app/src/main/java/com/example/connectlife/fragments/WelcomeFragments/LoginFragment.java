package com.example.connectlife.fragments.WelcomeFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.connectlife.MainActivity;
import com.example.connectlife.R;
import com.example.connectlife.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.tapadoo.alerter.Alert;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LoginFragment extends Fragment {
    Button accountRegisterBtn, continueButton;
    EditText emailField,phoneField;
    FirebaseFirestore firebaseFirestore;
    CountryCodePicker ccp;
    String codeBySystem;
    View loginView;
    View otpView;



    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_login, container, false);
        firebaseFirestore= FirebaseFirestore.getInstance();
        otpView = root.findViewById(R.id.otp_layout);
        loginView = root.findViewById(R.id.login_layout);
        accountRegisterBtn = root.findViewById(R.id.acc_register_btn);
        continueButton=root.findViewById(R.id.continue_btn);
        phoneField=loginView.findViewById(R.id.phone_field);
        ccp = (CountryCodePicker) loginView.findViewById(R.id.ccp_login);
        otpView.setVisibility(View.GONE);
        loginView.setVisibility(View.VISIBLE);


        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!(phoneField.getText().toString().isEmpty())){
                    String number = ccp.getFullNumberWithPlus() +phoneField.getText().toString() ;
                   // Toast.makeText(getContext(), number, Toast.LENGTH_SHORT).show();
                   numberAlreadyExists(number);


                }else{
                    phoneField.setError("Missing Field!");
                }

            }
        });

        accountRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeActivity.fragmentManager.beginTransaction().replace(R.id.welcome_container,new RegisterFragment(),"Register Fragment").commit();
            }
        });



        return root;
    }


    public void sendVerificationCodeToUser(String phoneNumber){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,60,
                TimeUnit.SECONDS,
                getActivity(),
                //TaskExecutors.MAIN_THREAD,
                mCallbacks
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //Toast.makeText(getContext(), "I was called", Toast.LENGTH_SHORT).show();
            codeBySystem= s;
            loginView.setVisibility(View.GONE);
            otpView.setVisibility(View.VISIBLE);

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            final String codeByUser= phoneAuthCredential.getSmsCode();
           // Toast.makeText(getContext(), "Code Sent!", Toast.LENGTH_SHORT).show();
            if(codeByUser!=null){
                final EditText otpField = otpView.findViewById(R.id.otp_field);
                otpField.setText(codeByUser);
                Button verifyButton = otpView.findViewById(R.id.verify_btn);
                verifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verifyCode(otpField.getText().toString());
                    }
                });

            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.i("Verification Failed", e.getMessage());

        }
    };

    private void verifyCode(String codeByUser){
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(codeBySystem,codeByUser);
        signInUserWithCredentials(phoneAuthCredential);

    }
    private void signInUserWithCredentials(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getContext(),MainActivity.class));
                }else{
                    Alerter.create(getActivity())
                            .setText("Invalid Passcode!")
                            .setBackgroundColorRes(R.color.colorPrimaryDark)
                            .setTitle("Try Again!").show();
                    //  Toast.makeText(getContext(), "Invalid Passcode! Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void numberAlreadyExists(final String phoneNumber){

        firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean exists = false;
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document :task.getResult()){
                        if(document.getData().containsKey("phoneNumber")){
                            if(document.getData().get("phoneNumber").equals(phoneNumber)){
                                exists = true;
                            }
                        }
                    }
                    if(exists){
                        sendVerificationCodeToUser(phoneNumber);
                    }else{
                        Alerter.create(getActivity())
                                .setTitle("Invalid Number!")
                                .setText("No account associated to this number found!")
                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                .show();
                    }
                }
            }

        });
    }




}