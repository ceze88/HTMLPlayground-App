package me.ceze88.htmlplayground.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.snackbar.Snackbar;
import me.ceze88.htmlplayground.HTMLPlaygroundApplication;
import me.ceze88.htmlplayground.MainActivity;
import me.ceze88.htmlplayground.R;
import me.ceze88.htmlplayground.database.FireDataStore;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view
        setContentView(R.layout.activity_login);

        //Show toolbar without menu button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the root layout
        TextView rootLayout = findViewById(R.id.login_title);

        // Get the screen height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        // Set the top padding to half the screen height
        rootLayout.setPadding(0, screenHeight / 3, 0, 0);

        // Get the login button
        Button loginButton = findViewById(R.id.login_button);

        // Set a click listener on the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);

                EditText emailEditText = findViewById(R.id.email_input);
                EditText passwordEditText = findViewById(R.id.password_input);

                HTMLPlaygroundApplication.getInstance().getAuthManager().signIn(emailEditText.getText().toString(), passwordEditText.getText().toString(), task -> {
                    if (task.isSuccessful()) {
                        //Download settings from cloud and save them to shared preferences
                        FireDataStore.getSettings(settings -> {
                            MainActivity.saveAutoRefreshSetting(settings.isAutoRefresh());
                        });
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Snackbar.make(v, "Email or password is incorrect or account does not exist", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Get the register link
        TextView registerLink = findViewById(R.id.register_link);

        // Set a click listener on the register link
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}