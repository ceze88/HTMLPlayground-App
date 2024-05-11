package me.ceze88.htmlplayground.navigation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import me.ceze88.htmlplayground.MainActivity;
import me.ceze88.htmlplayground.R;
import me.ceze88.htmlplayground.activity.ProfileActivity;
import me.ceze88.htmlplayground.activity.SavesActivity;
import me.ceze88.htmlplayground.activity.SettingsActivity;
import org.jetbrains.annotations.NotNull;

public class NavigationManager {

    public static void setupNavigation(Context context) {

        AppCompatActivity activity = (AppCompatActivity) context;
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        NavigationView navigationView = activity.findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NotNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NotNull View drawerView) {

                // Main
                activity.findViewById(R.id.action_main).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //check if the current activity is not the main activity
                        if (!(activity instanceof MainActivity)) {
                            activity.startActivity(new Intent(context, MainActivity.class));
                        }
                    }
                });

                // Settings
                activity.findViewById(R.id.action_settings).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!(activity instanceof SettingsActivity)) {
                            activity.startActivity(new Intent(context, SettingsActivity.class));
                        }
                    }
                });

                // Saves
                activity.findViewById(R.id.action_saves).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!(activity instanceof SavesActivity)) {
                            activity.startActivity(new Intent(context, SavesActivity.class));
                        }
                    }
                });

                // Profile
                activity.findViewById(R.id.action_account).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!(activity instanceof ProfileActivity)) {
                            activity.startActivity(new Intent(context, ProfileActivity.class));
                        }
                    }
                });
            }

            @Override
            public void onDrawerClosed(@NotNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_SETTLING) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
            }
        });
    }
}
