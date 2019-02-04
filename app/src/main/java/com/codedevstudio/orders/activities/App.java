package com.codedevstudio.orders.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.codedevstudio.orders.Constants;
import com.codedevstudio.orders.ServerApi;
import com.codedevstudio.orders.models.UserCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.PendingIntent.getActivity;

public class App extends MultiDexApplication {

    private static ServerApi sServerApi;
    private static Context appContext;
    public  static Context getAppContext(){
        return appContext;
    }
    private static NavDrawer navDrawer;

    public static NavDrawer getNavDrawer() {
        return navDrawer;
    }

    public static void setNavDrawer(NavDrawer navDrawer) {
        App.navDrawer = navDrawer;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        UserCredentials credentials = getCredentials();
                        Request request = null;
                        if (credentials != null){
                            request = original.newBuilder()
                                    .addHeader(Constants.EMAIL_HEADER_NAME,credentials.getEmail())
                                    .addHeader(Constants.TOKEN_HEADER_NAME,credentials.getToken())
                                    .build();
                        }

                        return request == null ?  chain.proceed(original) : chain.proceed(request);
                    }
                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        okhttp3.Response response = chain.proceed(request);

                        ActivityManager am = (ActivityManager)appContext.getSystemService(Context.ACTIVITY_SERVICE);
                        assert am != null;
                        String currentActivityName = am.getRunningTasks(1).get(0).baseActivity.getClassName();

                        // Всегда отправляем пользователя на страницу авторизации, если он не авторизован,
                        // кроме тех случаев, когда он находится на странице авторизации
                        if (!currentActivityName.equals(SignupActivity.class.getName()) && response.code() == 401) {
                            Logout();
                            startActivity(new Intent(appContext, SignupActivity.class));
                            return response;
                        }

                        return response;
                    }
                })
                .build();

        //Инициализируем retrofit api при старте нашего приложения
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.SERVER_ADDRESS) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create(gson)) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        sServerApi = retrofit.create(ServerApi.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    //Метод, который должен возвращать объект для взаимодействия с api сервера
    public static ServerApi getApi() {
        return sServerApi;
    }

    public static void saveCredentials(UserCredentials credentials){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", credentials.getEmail());
        editor.putString("token",credentials.getToken());
        editor.putString("userId",credentials.getId());
        editor.apply();
    }

    public static void Logout(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", null);
        editor.putString("token",null);
        editor.apply();
    }


    public static UserCredentials getCredentials(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(appContext);
        String email = pref.getString("email","");
        String token = pref.getString("token","");
        String userId = pref.getString("userId","");

        if (email.isEmpty() || token.isEmpty() || userId.isEmpty()){
            return null;
        } else {
            return new UserCredentials(userId, email,token);
        }
    }
}
