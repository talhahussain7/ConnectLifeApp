package com.example.connectlife.fragments.WelcomeFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class RegisterFragment extends Fragment {
    FirebaseAuth fAuth;
    FirebaseFirestore firebaseFirestore;
    String authenticationId;
    PhoneAuthProvider.ForceResendingToken token;
    Button RegisterBtn;
    boolean verificationInProgress = false;
    EditText phoneField,nameField,cityField,countryField,dobField,emailField ,otpField;
    CountryCodePicker ccp;
    View phoneLayout;
    Activity activity;
    String codeBySystem;
    String UID;
    View registerView, otpView;


    public RegisterFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        activity=getActivity();
        registerView= view.findViewById(R.id.register_layout);
        otpView=view.findViewById(R.id.otp_layout);

        RegisterBtn=view.findViewById(R.id.register_btn);
        phoneField=view.findViewById(R.id.phone_field);
        nameField=view.findViewById(R.id.name_field);
        cityField=view.findViewById(R.id.city_field);
        countryField=view.findViewById(R.id.country_field);
        dobField=view.findViewById(R.id.dob_field);
        emailField=view.findViewById(R.id.email_field);

        otpField=view.findViewById(R.id.otp_field);
        ccp = (CountryCodePicker) view.findViewById(R.id.ccp);
        phoneLayout=view.findViewById(R.id.phoneLayout);

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name,phoneNumber,country, city,dob,email ;
                name="";
                phoneNumber="";
                dob="";
                country="";
                city="";


                if(!(isNameValid()&&isAddressValid()&&isDobValid()&&isEmailValid()&&isPhoneValid())){
                Alerter.create(activity)
                        .setTitle("Missing Fields!")
                        .setText("All Fields are mandatory!")
                        .setBackgroundColorRes(R.color.colorPrimary) // or setBackgroundColorInt(Color.CYAN)
                        .show();
                }else{
                    // Fetch all data
                    name = nameField.getText().toString();
                    phoneNumber=phoneField.getText().toString();
                    country =countryField.getText().toString();
                    city=cityField.getText().toString();
                    dob = dobField.getText().toString();
                    email =emailField.getText().toString();
                    phoneNumber = ccp.getFullNumberWithPlus()+phoneNumber;
                    sendVerificationCodeToUser(phoneNumber);
                }





                /*try {
                    name = nameField.getText().toString();
                     phoneNumber=phoneField.getText().toString();
                     address =addressField.getText().toString();
                     dob = dobField.getText().toString();
                     email =emailField.getText().toString();
                }catch (Exception e){
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                }*/



            }
        });

       /* continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!verificationInProgress){
                    if(!phoneField.getText().toString().isEmpty()){
                        requestOTP(ccp.getFullNumberWithPlus()+phoneField.getText().toString());
                        otpField.setText("");
                    }else{
                        phoneField.setError("Enter the number please!");
                    }

                }else{
                    if(!otpField.getText().toString().isEmpty()){
                        String OTP =  otpField.getText().toString();
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(authenticationId,OTP);
                        verifyAuth(phoneAuthCredential);
                    }else{
                        otpField.setError("Enter OTP!");
                    }

                }
            }
        });*/
        fAuth= FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();


        return view;
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
            Toast.makeText(getContext(), "I was called", Toast.LENGTH_SHORT).show();
            codeBySystem= s;
            registerView.setVisibility(View.GONE);
            otpView.setVisibility(View.VISIBLE);

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            final String codeByUser= phoneAuthCredential.getSmsCode();
            Toast.makeText(getContext(), "Sent!", Toast.LENGTH_SHORT).show();
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
                    Map<String,Object> userInfo = new HashMap<>();
                    userInfo.put("name",nameField.getText().toString());
                    userInfo.put("city",cityField.getText().toString());
                    userInfo.put("country",countryField.getText().toString());
                    userInfo.put("dob",dobField.getText().toString());
                    userInfo.put("email",emailField.getText().toString());
                    userInfo.put("phoneNumber",ccp.getFullNumber()+phoneField.getText().toString());
                UID= fAuth.getCurrentUser().getUid();
                DocumentReference documentReference = firebaseFirestore.collection("users").document(UID);
                documentReference.set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(activity, "Correct!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(),MainActivity.class));
                    }
                });
                }else{
                    Toast.makeText(getContext(), "Could Not Sign Up!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isNameValid(){
        return !TextUtils.isEmpty(nameField.getText().toString());
    }
    public boolean isAddressValid(){
        return !TextUtils.isEmpty(cityField.getText().toString()) && !TextUtils.isEmpty(countryField.getText().toString());
    }
    public boolean isDobValid(){
        return !TextUtils.isEmpty(dobField.getText().toString());
    }
    public boolean isEmailValid(){
        return !TextUtils.isEmpty(emailField.getText().toString());
    }
    public boolean isPhoneValid(){
        return !TextUtils.isEmpty(phoneField.getText().toString());
    }



}