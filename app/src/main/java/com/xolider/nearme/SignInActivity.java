package com.xolider.nearme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xolider.nearme.utils.SigninRequest;

import java.util.concurrent.ExecutionException;

public class SignInActivity extends AppCompatActivity {

    public static boolean isCreated = false;

    private EditText mUsernaame;
    private EditText mPass;
    private EditText mName;
    private EditText mEmail;
    private Button mSignin;

    private TextView mError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mUsernaame = (EditText)findViewById(R.id.username_s);
        mPass = (EditText)findViewById(R.id.password_s);
        mName = (EditText)findViewById(R.id.name_s);
        mEmail = (EditText)findViewById(R.id.email_s);
        mSignin = (Button)findViewById(R.id.button_s);

        mError = (TextView)findViewById(R.id.error_empty);

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(mUsernaame.getText().toString().isEmpty() && mPass.getText().toString().isEmpty() && mEmail.getText().toString().isEmpty() && mName.getText().toString().isEmpty())) {
                    try {
                        boolean b = new SigninRequest(SignInActivity.this).execute(mUsernaame.getText().toString(), mPass.getText().toString(), mEmail.getText().toString(), mName.getText().toString()).get();
                        if(!b) {
                            mError.setText(getResources().getString(R.string.error_account_exist));
                            mError.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                else {
                    mError.setText(getResources().getString(R.string.error_empty));
                    mError.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
