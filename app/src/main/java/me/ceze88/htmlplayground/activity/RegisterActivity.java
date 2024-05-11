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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view
        setContentView(R.layout.activity_register);

        //Show toolbar without menu button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the root layout
        TextView rootLayout = findViewById(R.id.registration_title);

        // Get the screen height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        // Set the top padding to half the screen height
        rootLayout.setPadding(0, screenHeight / 3, 0, 0);

        // Get the register button
        Button registerButton = findViewById(R.id.register_button);

        // Set a click listener on the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Close the keyboard
                hideKeyboard(v);


                // Handle the register button click here
                // Get email and password from the input fields
                EditText emailEditText = findViewById(R.id.email_input);
                EditText passwordEditText = findViewById(R.id.password_input);
                EditText passwordConfirmEditText = findViewById(R.id.password_confirm_input);

                //verify password
                if (!passwordEditText.getText().toString().equals(passwordConfirmEditText.getText().toString())) {
                    Snackbar.make(v, "Passwords do not match", Snackbar.LENGTH_LONG).show();
                    return;
                }

                //Check if password is at least 6 characters long
                if (passwordEditText.getText().toString().length() < 6) {
                    Snackbar.make(v, "Password must be at least 6 characters long", Snackbar.LENGTH_LONG).show();
                    return;
                }

                //Check if email is valid
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()) {
                    Snackbar.make(v, "Invalid email address", Snackbar.LENGTH_LONG).show();
                    return;
                }

                //Register user
                HTMLPlaygroundApplication.getInstance().getAuthManager().signUp(emailEditText.getText().toString(), passwordEditText.getText().toString(), task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Snackbar.make(v, "Registration failed", Snackbar.LENGTH_LONG).show();
                        task.getException().printStackTrace();
                    }
                });
            }
        });

        // Get the login link
        TextView loginLink = findViewById(R.id.login_link);

        // Set a click listener on the login link
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}