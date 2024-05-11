package me.ceze88.htmlplayground.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import me.ceze88.htmlplayground.MainActivity;
import me.ceze88.htmlplayground.R;
import me.ceze88.htmlplayground.database.FireDataStore;
import me.ceze88.htmlplayground.navigation.NavigationManager;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox autoRefreshCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        NavigationManager.setupNavigation(this);

        autoRefreshCheckbox = findViewById(R.id.auto_refresh_checkbox);

        //Load the state of the checkbox from the shared preferences
        autoRefreshCheckbox.setChecked(MainActivity.getAutoRefreshSetting());


        autoRefreshCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String message = isChecked ? "Auto Refresh Enabled" : "Auto Refresh Disabled";
            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();

            // Save the state of the checkbox
            MainActivity.saveAutoRefreshSetting(isChecked);
        });
    }

    //save the state of the checkbox to the shared preferences when we exit the view

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.saveAutoRefreshSetting(autoRefreshCheckbox.isChecked());
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainActivity.saveAutoRefreshSetting(autoRefreshCheckbox.isChecked());
        saveToFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoRefreshCheckbox.setChecked(MainActivity.getAutoRefreshSetting());
    }

    private void saveToFirebase() {
        FireDataStore.saveSettings(autoRefreshCheckbox.isChecked(), result -> {
            if (!result) {
                Toast.makeText(SettingsActivity.this, "Failed to save settings to cloud", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
