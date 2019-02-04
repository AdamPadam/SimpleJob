package com.codedevstudio.orders.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.ServerDataWrappers.AuthenticationWrapper;
import com.codedevstudio.orders.models.UserCredentials;
import com.codedevstudio.orders.activities.App;
import com.codedevstudio.orders.activities.NavDrawer;
import com.codedevstudio.orders.activities.SignupActivity;
import com.codedevstudio.orders.activities.LoginActivity;
import com.codedevstudio.orders.models.User;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.codedevstudio.orders.Constants.*;

public class SignupFragment extends Fragment implements SignupActivity.OnBackPressedListener {

    private static final String ARG_ID = "signup_fragment_id";
    private static final int SELECT_PHOTO= 1;
    private static final int MAKE_PHOTO = 2;

    private User user;
    private EditText mEmail, mPassword, mFirstName, mLastName, mBio, mPhone;
    private LinearLayout mPersonalLayout, mMainLayout;
    private ImageView mLogoView;
    private Button selectPhoto;
    private Button takePhoto;
    private CircleImageView userPhoto;
    private String mEmailString;


    private String avatarBase64;

    public static SignupFragment newInstance() {
        return new SignupFragment();
    }

    public static SignupFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, user);

        SignupFragment fragment = new SignupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ARG_ID)) {
            user = (User) getArguments().getSerializable(ARG_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);

        ((SignupActivity) getActivity()).setOnBackPressedListener(this);


        mEmail = (EditText) v.findViewById(R.id.email);
        mPassword = (EditText) v.findViewById(R.id.password);
        mFirstName = (EditText) v.findViewById(R.id.first_name);
        mLastName = (EditText) v.findViewById(R.id.last_name);
        Button registerButton = (Button) v.findViewById(R.id.register_button);
        final Button completeButton = (Button) v.findViewById(R.id.complete_register);
        mPersonalLayout = (LinearLayout) v.findViewById(R.id.personal_layout);
        mMainLayout = (LinearLayout) v.findViewById(R.id.main_layout);
        mLogoView = (ImageView) v.findViewById(R.id.logo_image);
        mBio = (EditText) v.findViewById(R.id.bio);
        selectPhoto = (Button) v.findViewById(R.id.select_photo);
        takePhoto = (Button) v.findViewById(R.id.take_photo);
        userPhoto = (CircleImageView) v.findViewById(R.id.userPhoto);
        mPhone = (EditText) v.findViewById(R.id.phone);
        TextView textView = (TextView) v.findViewById(R.id.already_registered);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(LoginActivity.newIntent(getActivity()));
            }
        });

        if (user == null) {
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mEmail.getText() != null)
                        if (checkEmail(mEmail.getText().toString())) {
                            mEmailString = mEmail.getText().toString();
                            mMainLayout.setVisibility(View.GONE);
                            mLogoView.setVisibility(View.GONE);
                            mPersonalLayout.setVisibility(View.VISIBLE);
                            completeButton.setText(R.string.complete_register);
                        } else
                            Toast.makeText(getActivity(), R.string.warning_incorrect_email, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), R.string.warning_fields_empty, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            mMainLayout.setVisibility(View.GONE);
            mLogoView.setVisibility(View.GONE);
            mPersonalLayout.setVisibility(View.VISIBLE);
            mFirstName.setText(user.getFirstName());
            mLastName.setText(user.getLastName());
            mBio.setText(user.getBio());
            Picasso.get().load(user.getAvatar().getMedium()).into(userPhoto);
            completeButton.setText(R.string.update_profile);
        }


        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(
                        mEmailString,
                        mPassword.getText().toString(),
                        mFirstName.getText().toString(),
                        mLastName.getText().toString(),
                        mBio.getText().toString(),
                        mPhone.getText().toString()
                );
            }
        });


        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , SELECT_PHOTO);
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, MAKE_PHOTO);
            }
        });
        return v;
    }

    private void createUser(final String email, String password, final String name, final String lastName, final String bio, final String phone) {
        final boolean isNewUser = this.user == null;
        if ( password.length() >= 6 || (!isNewUser && password.isEmpty())) {
            //Если введенный пароль больше 6 символов, то отправляем асинхронный запрос на регистрацию и назначаем слушателя на результат


            // Отправляем запрос на регистрацию в БД пользователяdo
            final User user = isNewUser ? new User(email, password, name, lastName, bio, phone) : new User(email,name,lastName,bio, phone);
            if (avatarBase64 != null && !avatarBase64.isEmpty()){
                user.setAvatar64("data:image/jpeg;base64,"+avatarBase64);
            }

            Call<User> call;
            if (isNewUser) {
                call = App.getApi().createUser(new AuthenticationWrapper(user));
            } else {
                call = App.getApi().editUser(new AuthenticationWrapper(user));
            }
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    // Если все успещно, то отправляем пользователя в главную активность
                    if (response.code() == 200) {
                        // Получаем с сервера токен

                        Toast.makeText(getActivity(), "All ok", Toast.LENGTH_SHORT).show();
                        if (isNewUser) {
                            User savedUser = response.body();
                            UserCredentials credentials = savedUser.getCredentials();
                            App.saveCredentials(credentials);

                            startActivity(NavDrawer.newIntent(getActivity()));

                        } else {
                            NavDrawer nD = App.getNavDrawer();
                            nD.updateUser();
                            getActivity().finish();
                        }
                    } else {
                        Log.w(TAG, "Response is " + response.code());
                        try {
                            TreeMap<String, ArrayList<String>> errors = new Gson().fromJson(response.errorBody().string(), TreeMap.class);
                            String errorKey = errors.firstKey();
                            String errorValues = TextUtils.join(",", errors.firstEntry().getValue());
                            Toast.makeText(getActivity(), errorKey + ": " + errorValues, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    Log.w(TAG, t.toString());
                }
            });


        } else Toast.makeText(getActivity(), "Слабый пароль", Toast.LENGTH_SHORT).show();
    }

    /**
     * private void _createUser(final String email, String password, final String name, final String lastName) {
     * if (password.length() >= 6) {
     * //Если введенный пароль больше 6 символов, то отправляем асинхронный запрос на регистрацию и назначаем слушателя на результат
     * mAuth.createUserWithEmailAndPassword(email, password)
     * .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
     *
     * @Override public void onComplete(@NonNull Task<AuthResult> task) {
     * Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
     * if (task.isSuccessful()) {
     * task.getResult().getUser().getToken(true)
     * .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
     * public void onComplete(@NonNull final Task<GetTokenResult> task) {
     * if (task.isSuccessful()) {
     * final String idToken = task.getResult().getToken();
     * // Отправляем запрос на регистрацию в БД пользователя и токен серверу
     * // Call<String> call = App.getApi().createUser(idToken, name, lastName);
     * //                                                    call.enqueue(new Callback<String>() {
     * //                                                        @Override
     * //                                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
     * //                                                            // Если все успещно, то отправляем пользователя в главную активность
     * //                                                            if (response.code() == 200) {
     * //                                                                Toast.makeText(getActivity(), "All ok", Toast.LENGTH_SHORT).show();
     * //                                                                startActivity(NavDrawer.newIntent(getActivity()));
     * //                                                            } else
     * //                                                                Log.w(TAG, "Response is " + response.code());
     * //                                                        }
     * //
     * //                                                        @Override
     * //                                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
     * //                                                            Log.w(TAG, t.toString());
     * //                                                        }
     * //                                                    });
     * } else {
     * // Handle error -> task.getException();
     * //noinspection ConstantConditions
     * Log.w(TAG, task.getException().toString());
     * }
     * }
     * });
     * }
     * // If sign in fails, display a message to the user. If sign in succeeds
     * // the auth state listener will be notified and logic to handle the
     * // signed in user can be handled in the listener.
     * else {
     * Toast.makeText(getActivity(), R.string.auth_failed,
     * Toast.LENGTH_SHORT).show();
     * //noinspection ConstantConditions
     * Log.w(TAG, task.getException().getMessage());
     * }
     * }
     * });
     * } else Toast.makeText(getActivity(), "Слабый пароль", Toast.LENGTH_SHORT).show();
     * }
     **/

    // CallBack функция для обработки нажатия кнопки "Назад"
    @Override
    public void doBack() {

        if (user == null) {
            mMainLayout.setVisibility(View.VISIBLE);
            mLogoView.setVisibility(View.VISIBLE);
            mPersonalLayout.setVisibility(View.GONE);

        } else {
            getActivity().finish();
        }

    }
    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case SELECT_PHOTO:
                    final Uri imageUri = data.getData();
                    try {
                        InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        avatarBase64 = encodeImage(selectedImage);
                        userPhoto.setImageURI(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case MAKE_PHOTO:
                    Bundle extras = data.getExtras();
                    final Bitmap selectedImage = (Bitmap) extras.get("data");
                    avatarBase64 = encodeImage(selectedImage);
                    userPhoto.setImageBitmap(selectedImage);
                    break;
            }
        }
    }
    // Регулярное выражение, проверяющее корректность почты по RFC 5322 с вероятностью 99.98%
    private boolean checkEmail(String email) {
        return email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    }
}
