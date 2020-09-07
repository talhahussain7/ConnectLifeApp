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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    Button RegisterBtn,signInButton;
    boolean verificationInProgress = false;
    EditText phoneField,nameField,cityField,countryField,dobField ,otpField;
    Spinner bloodGroupSpinner;
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
        bloodGroupSpinner = view.findViewById(R.id.blood_group_selector);
        signInButton = view.findViewById(R.id.sign_in_btn);

        otpField=view.findViewById(R.id.otp_field);
        ccp = (CountryCodePicker) view.findViewById(R.id.ccp);
        phoneLayout=view.findViewById(R.id.phoneLayout);


        final String[] arraySpinner = new String[] {
                "A+","A-","B+","B-","AB+","AB-","O+","O-","OH+","Other"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(adapter);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeActivity.fragmentManager.beginTransaction().replace(R.id.welcome_container,new LoginFragment(),"Login Fragment").commit();
            }
        });

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber;

                phoneNumber="";



                if(!(isNameValid()&&isAddressValid()&&isDobValid()&&isPhoneValid())){
                Alerter.create(activity)
                        .setTitle("Missing Fields!")
                        .setText("All Fields are mandatory!")
                        .setBackgroundColorRes(R.color.colorPrimary) // or setBackgroundColorInt(Color.CYAN)
                        .show();
                }else{
                    // Fetch Phone Number.
                    phoneNumber = ccp.getFullNumberWithPlus()+phoneField.getText().toString();
                    Toast.makeText(getContext(), phoneNumber +"", Toast.LENGTH_SHORT).show();
                        numberAlreadyExists(phoneNumber);


                }


            }
        });


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
            //Toast.makeText(getContext(), "I was called", Toast.LENGTH_SHORT).show();
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

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
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
                    userInfo.put("bloodGroup",bloodGroupSpinner.getSelectedItem().toString());
                    userInfo.put("LatLng","0,0");
                    userInfo.put("token","");
                    userInfo.put("requestsCount","0");
                    userInfo.put("donationsCount","0");
                    userInfo.put("phoneNumber",ccp.getFullNumberWithPlus()+phoneField.getText().toString());
                    userInfo.put("docRef","");
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
                    Alerter.create(getActivity())
                            .setText("Invalid Passcode!")
                            .setBackgroundColorRes(R.color.colorPrimaryDark)
                            .setTitle("Try Again!").show();
                  //  Toast.makeText(getContext(), "Invalid Passcode! Try Again", Toast.LENGTH_SHORT).show();
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


    /*public boolean isEmailValid(){
        return !TextUtils.isEmpty(emailField.getText().toString());
    }*/

    public boolean isPhoneValid(){
        return !TextUtils.isEmpty(phoneField.getText().toString());
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
                        Alerter.create(getActivity())
                                .setTitle("Invalid Number!")
                                .setText("Account Already Exists! Please Login")
                                .setBackgroundColorRes(R.color.colorPrimaryDark)
                                .show();
                    }else{

                        sendVerificationCodeToUser(phoneNumber);

                    }
                }
            }

        });
    }



}