package me.ceze88.htmlplayground;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import me.ceze88.htmlplayground.auth.FirebaseAuthManager;

public class HTMLPlaygroundApplication extends Application {

    private static HTMLPlaygroundApplication instance;
    private FirebaseAuthManager authManager;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        authManager = new FirebaseAuthManager();
        instance = this;
    }

    public static HTMLPlaygroundApplication getInstance() {
        return instance;
    }

    public FirebaseAuthManager getAuthManager() {
        return authManager;
    }
}
