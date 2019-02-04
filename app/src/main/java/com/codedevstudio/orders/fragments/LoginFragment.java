package com.codedevstudio.orders.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codedevstudio.orders.ServerDataWrappers.AuthenticationWrapper;
import com.codedevstudio.orders.R;
import com.codedevstudio.orders.ServerApi;
import com.codedevstudio.orders.activities.App;
import com.codedevstudio.orders.activities.NavDrawer;
import com.codedevstudio.orders.models.User;
import com.codedevstudio.orders.models.UserCredentials;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codedevstudio.orders.Constants.*;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;

    private EditText mEmail, mPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mEmail = (EditText) view.findViewById(R.id.email);
        mPassword = (EditText) view.findViewById(R.id.password);
        Button button = (Button) view.findViewById(R.id.log_in);

        // При нажатии на текст на почту отправляется письмо для восстановления пароля
        TextView forgetPassword = (TextView) view.findViewById(R.id.forget_password);
//        forgetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (checkEmail(mEmail.getText().toString())) {
//                    FirebaseAuth auth = FirebaseAuth.getInstance();
//                    String emailAddress = mEmail.getText().toString();
//                    auth.sendPasswordResetEmail(emailAddress)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @SuppressWarnings("ConstantConditions")
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Log.d(TAG, "Email sent.");
//                                        Toast.makeText(getActivity(), "Письмо отправлено на почту", Toast.LENGTH_SHORT).show();
//
//                                        // Закрываем клавиатуру
//                                        View view = getActivity().getCurrentFocus();
//                                        if (view != null) {
//                                            InputMethodManager manager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                                            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                                        }
//                                    } else if (task.getException().getClass() == FirebaseAuthInvalidUserException.class)
//                                        Toast.makeText(getActivity(), "Пользователя с таким адресом не существует", Toast.LENGTH_SHORT).show();
//                                    else {
//                                        Log.w(TAG, task.getException().toString());
//                                    }
//                                }
//                            });
//
//                }else Toast.makeText(getActivity(), "Введите корректную почту", Toast.LENGTH_SHORT).show();
//            }
//        });

//        mAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEmail.getText() != null && mPassword.getText() != null) {
                    ServerApi api = App.getApi();
                    User user = new User(mEmail.getText().toString(), mPassword.getText().toString());
                    Call<UserCredentials> call = api.Login(new AuthenticationWrapper(user));
                    call.enqueue(new Callback<UserCredentials>() {
                        @Override
                        public void onResponse(@NonNull Call<UserCredentials> call, @NonNull Response<UserCredentials> response) {
                            // Если все успещно, то отправляем пользователя в главную активность
                            if (response.code() == 200) {
                                Toast.makeText(getActivity(), "All ok", Toast.LENGTH_SHORT).show();
                                UserCredentials credentials = response.body();
                                App.saveCredentials(credentials);
                                startActivity(NavDrawer.newIntent(getActivity()));
                            } else{
                                Log.w(TAG, "signInWithEmail:failed" + response.errorBody());
                                Toast.makeText(getActivity(), R.string.login_error,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<UserCredentials> call, @NonNull Throwable t) {
                            Log.w(TAG, t.toString());
                        }
                    });
//                    mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
//
//                            // If sign in fails, display a message to the user. If sign in succeeds
//                            // the auth state listener will be notified and logic to handle the
//                            // signed in user can be handled in the listener.
//                            if (!task.isSuccessful()) {
//                                Log.w(TAG, "signInWithEmail:failed", task.getException());
//                                Toast.makeText(getActivity(), R.string.login_error,
//                                        Toast.LENGTH_SHORT).show();
//                            } else
//                                startActivity(NavDrawer.newIntent(getActivity()));
//
//                        }
//                    });
                }
            }
        });

        return view;
    }

    // Регулярное выражение, проверяющее корректность почты по RFC 5322 с вероятностью 99.98%
    private boolean checkEmail(String email) {
        return email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    }
}
