package me.ceze88.htmlplayground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import me.ceze88.htmlplayground.HTMLPlaygroundApplication;
import me.ceze88.htmlplayground.MainActivity;
import me.ceze88.htmlplayground.R;
import me.ceze88.htmlplayground.navigation.NavigationManager;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view
        setContentView(R.layout.activity_profile);

        // Set up the toolbar
        NavigationManager.setupNavigation(this);

        Button signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


    }

    private void signOut() {
        HTMLPlaygroundApplication.getInstance().getAuthManager().signOut();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finish();
    }
}