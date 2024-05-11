package me.ceze88.htmlplayground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import me.ceze88.htmlplayground.HTMLPlaygroundApplication;
import me.ceze88.htmlplayground.MainActivity;
import me.ceze88.htmlplayground.R;
import me.ceze88.htmlplayground.auth.FirebaseAuthManager;
import me.ceze88.htmlplayground.database.FireDataStore;
import me.ceze88.htmlplayground.model.SavedHTML;
import me.ceze88.htmlplayground.navigation.NavigationManager;

import java.util.LinkedList;
import java.util.List;

public class SavesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saves);
        NavigationManager.setupNavigation(this);

        FireDataStore.getHTMLs(HTMLPlaygroundApplication.getInstance().getAuthManager().getCurrentUser().getUid(), result -> {
            findViewById(R.id.loading).setVisibility(View.GONE);
            if (result.isEmpty()) {
                findViewById(R.id.no_saved_codes_text_view).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.no_saved_codes_text_view).setVisibility(View.GONE);

                //Create rows for the saved HTMLs without xml and recycler view
                LinearLayout linearLayout = findViewById(R.id.saves_layout);
                for (SavedHTML savedHTML : result) {
                    // Create a CardView for the row
                    CardView cardView = new CardView(this);
                    cardView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    cardView.setRadius(9); // set corner radius
                    cardView.setCardElevation(3); // set elevation

                    // Create a RelativeLayout for the row
                    RelativeLayout row = new RelativeLayout(this);
                    row.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

                    // Create a TextView for the title
                    TextView titleTextView = new TextView(this);
                    titleTextView.setText(savedHTML.getTitle());
                    RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    titleParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE); // Center vertically
                    titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    titleTextView.setLayoutParams(titleParams);
                    //Add margin left
                    titleParams.setMarginStart(16);
                    row.addView(titleTextView);

                    // Create a Button to delete the saved HTML
                    Button deleteButton = new Button(this);
                    deleteButton.setBackgroundColor(getResources().getColor(R.color.red));
                    deleteButton.setText("Delete");
                    RelativeLayout.LayoutParams deleteParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    deleteParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    deleteButton.setLayoutParams(deleteParams);
                    deleteButton.setOnClickListener(v -> {
                        //Show a confirmation dialog
                        new AlertDialog.Builder(this)
                                .setTitle("Delete")
                                .setMessage("Are you sure you want to delete this saved HTML?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    FireDataStore.deleteHTML(savedHTML);
                                    linearLayout.removeView(cardView);
                                    //Show a snackbar
                                    Snackbar.make(findViewById(R.id.saves_layout), "Deleted " + savedHTML.getTitle(), Snackbar.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("No", null)
                                .show();
                    });
                    row.addView(deleteButton);

                    // Create a Button to edit the saved HTML
                    Button editButton = new Button(this);
                    editButton.setText("Edit");
                    RelativeLayout.LayoutParams editParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    editParams.addRule(RelativeLayout.LEFT_OF, deleteButton.getId()); // Position to the left of the delete button
                    editParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE); // Center vertically
                    //Float it next to the delete button with a 4dp margin
                    editParams.setMarginEnd(4);
                    //Calculate margin start, get the screen width and subtract the width of the delete button and 4dp
                    editParams.setMarginStart(getResources().getDisplayMetrics().widthPixels/2 - deleteButton.getWidth());
                    editButton.setLayoutParams(editParams);
                    // Add your edit action here
                    editButton.setOnClickListener(v -> {
                        //Redirect to main activity with the content of the saved HTML, pass the SavedHTML object to the intent so we can get the content in the main activity
                        MainActivity.setEditingHTML(savedHTML);
                        //Navigate to the main activity
                        this.startActivity(new Intent(this, MainActivity.class));
                    });

                    // Add the buttons to the row
                    row.addView(editButton);

                    // Add the row to the CardView
                    cardView.addView(row);

                    //add margin bottom for the cards
                    LinearLayout.LayoutParams cardParams = (LinearLayout.LayoutParams) cardView.getLayoutParams();
                    cardParams.setMargins(0, 0, 0, 16);

                    // Add the CardView to the LinearLayout
                    linearLayout.addView(cardView);
                }

            }
        });
    }
}
