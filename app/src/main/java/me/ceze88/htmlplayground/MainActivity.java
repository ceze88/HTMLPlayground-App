package me.ceze88.htmlplayground;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import me.ceze88.htmlplayground.activity.*;
import me.ceze88.htmlplayground.auth.FirebaseAuthManager;
import me.ceze88.htmlplayground.database.FireDataStore;
import me.ceze88.htmlplayground.model.SavedHTML;
import me.ceze88.htmlplayground.navigation.NavigationManager;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static Context context;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private final HTMLPlaygroundApplication app = HTMLPlaygroundApplication.getInstance();
    private static SavedHTML editingHTML;
    private final String html = "<html><head></head><body>" + "<h1>Hello World!</h1>" + "</body></html>";
    private boolean exitedFromEditor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;


        //Check if user is logged in
        if (!app.getAuthManager().isUserLoggedIn()) {
            showLoginPage();
        } else {
            showMainPage();
            createNotificationChannel();
        }
    }

    private void showMainPage() {
        // Set the content view
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationManager.setupNavigation(this);

        EditText htmlInput = findViewById(R.id.html_input);
        WebView webView = findViewById(R.id.webview);

        if (isDarkModeOn()) {
            webView.setBackgroundColor(Color.BLACK);
        } else {
            webView.setBackgroundColor(Color.WHITE);
        }

        // Enable JavaScript (if needed)
        WebSettings webSettings = webView.getSettings();

        // Load the HTML code from the EditText into the WebView


        // Add a TextWatcher to the EditText
        htmlInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Check if auto refresh is enabled
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                boolean autoRefresh = sharedPreferences.getBoolean("auto_refresh", false);
                if (autoRefresh) {
                    loadHTMLIntoWebView();
                }
            }
        });

        //get the refresh and the save button
        Button saveButton = findViewById(R.id.save_button);
        Button clearButton = findViewById(R.id.clear_button);
        Button refreshButton = findViewById(R.id.refresh_button);

        //set the click listener for the refresh button
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHTMLIntoWebView();
            }
        });

        //TODO fix editing HTML, need to load prev input so we can resrume when we navigate back

        if (editingHTML != null) {
            // Load the editing HTML data into the EditText
            htmlInput.setText(editingHTML.getContent());
            //Set clear button it to the back button
            clearButton.setText(R.string.back);
            //Update view
            webView.loadData(editingHTML.getContent(), "text/html", "UTF-8");
            //set the click listener for the clear button
            clearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Set the editing HTML to null and go back to saves activity
                    editingHTML = null;
                    exitedFromEditor = true;
                    Intent intent = new Intent(MainActivity.this, SavesActivity.class);
                    startActivity(intent);
                }
            });

            //set the click listener for the save button to update the html and navigate back to the saves activity
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editingHTML.setContent(htmlInput.getText().toString());
                    FireDataStore.updateHTML(editingHTML, result -> {
                        if (result) {
                            Snackbar.make(findViewById(R.id.drawer_layout), "HTML updated successfully", Snackbar.LENGTH_SHORT).show();
                            editingHTML = null;
                            exitedFromEditor = true;
                            Intent intent = new Intent(MainActivity.this, SavesActivity.class);
                            startActivity(intent);
                        } else {
                            Snackbar.make(findViewById(R.id.drawer_layout), "Failed to update HTML", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            //set the click listener for the clear button
            clearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    htmlInput.setText("");
                }
            });

            //set the click listener for the save button
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Create a dialog to get a title for the saved HTML
                    LinearLayout layout = new LinearLayout(MainActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    TextView titleTextView = new TextView(MainActivity.this);
                    titleTextView.setText("Title:");
                    layout.addView(titleTextView);

                    EditText titleEditText = new EditText(MainActivity.this);
                    layout.addView(titleEditText);

                    //Create the dialog
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Save HTML")
                            .setView(layout)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Save the HTML
                                    SavedHTML savedHTML = new SavedHTML();
                                    savedHTML.setTitle(titleEditText.getText().toString());
                                    savedHTML.setContent(htmlInput.getText().toString());
                                    savedHTML.setUserId(app.getAuthManager().getCurrentUser().getUid());
                                    savedHTML.setCreatedAt(System.currentTimeMillis());
                                    FireDataStore.saveHTML(savedHTML, result -> {
                                        if (result) {
                                            Snackbar.make(findViewById(R.id.drawer_layout), "HTML saved successfully", Snackbar.LENGTH_SHORT).show();
                                        } else {
                                            Snackbar.make(findViewById(R.id.drawer_layout), "Failed to save HTML", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                };
            });
        }
    }

    private void showLoginPage() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!exitedFromEditor) {
            // Save the HTML input to shared preferences
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            EditText htmlInput = findViewById(R.id.html_input);
            if (htmlInput != null) {
                editor.putString("html_input", htmlInput.getText().toString());
                editor.apply();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (editingHTML == null) {
            exitedFromEditor = false;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String htmlInputText = sharedPreferences.getString("html_input", "");
            EditText htmlInput = findViewById(R.id.html_input);
            if (htmlInput != null) {
                htmlInput.setText(htmlInputText);
                //Update the webview
                loadHTMLIntoWebView();
            }
        }
    }

    public static void setEditingHTML(SavedHTML editingHTML) {
        MainActivity.editingHTML = editingHTML;
    }

    public void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "htmlplayground_motd")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // replace with your own icon
                .setContentTitle("Welcome to HTML Playground!")
                .setContentText("Have fun coding!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(100, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.motd_channel);
            String description = getString(R.string.motd_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("htmlplayground_motd", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            sendNotification();
        }
    }

    public static void saveAutoRefreshSetting(boolean autoRefresh) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("auto_refresh", autoRefresh);
        editor.apply();
    }

    public static boolean getAutoRefreshSetting() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("auto_refresh", false);
    }

    public boolean isDarkModeOn() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public void loadHTMLIntoWebView() {
        EditText htmlInput = findViewById(R.id.html_input);
        WebView webView = findViewById(R.id.webview);

        String htmlContent = htmlInput.getText().toString();

        if (isDarkModeOn()) {
            webView.setBackgroundColor(Color.BLACK);
            htmlContent = "<style>body { color: white; }</style>" + htmlContent;
        } else {
            webView.setBackgroundColor(Color.WHITE);
            htmlContent = "<style>body { color: black; }</style>" + htmlContent;
        }

        webView.loadData(htmlContent, "text/html", "UTF-8");
    }
}