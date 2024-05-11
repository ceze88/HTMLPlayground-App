package me.ceze88.htmlplayground.database;

import android.os.AsyncTask;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import me.ceze88.htmlplayground.HTMLPlaygroundApplication;
import me.ceze88.htmlplayground.model.SavedHTML;
import me.ceze88.htmlplayground.model.SavedSetting;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class FireDataStore {


    public static void saveHTML(SavedHTML html, Callback<Boolean> result) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                firestore.collection("htmls").add(new SavedHTML(UUID.randomUUID().toString(), html.getTitle(), html.getContent(), html.getUserId(), System.currentTimeMillis())).addOnCompleteListener(task -> {
                    result.accept(task.isSuccessful());
                });
                return null;
            }
        }.execute();
    }

    public static void deleteHTML(SavedHTML html) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                firestore.collection("htmls").whereEqualTo("id", html.getId()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (!documents.isEmpty()) {
                            // Delete the document
                            firestore.collection("htmls").document(documents.get(0).getId()).delete();
                        }
                    }
                });
                return null;
            }
        }.execute();
    }

    public static void getHTMLs(String uid, FireDataStoreCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        new AsyncTask<Void, Void, LinkedList<SavedHTML>>() {

            @Override
            protected LinkedList<SavedHTML> doInBackground(Void... voids) {
                LinkedList<SavedHTML> saved = new LinkedList<>();
                firestore.collection("htmls").whereEqualTo("userId", uid).orderBy("createdAt", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<SavedHTML> documents = task.getResult().toObjects(SavedHTML.class);
                        saved.addAll(documents);
                        callback.onCallback(saved);
                    }
                });
                return saved;
            }
        }.execute();
    }

    public static void updateHTML(SavedHTML html, Callback<Boolean> result) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                firestore.collection("htmls").whereEqualTo("id", html.getId()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (!documents.isEmpty()) {
                            // Update the document
                            firestore.collection("htmls").document(documents.get(0).getId()).set(html);
                            result.accept(true);
                        } else {
                            result.accept(false);
                        }
                    }
                });
                return null;
            }
        }.execute();
    }

    public static void saveSettings(boolean autoRefresh, Callback<Boolean> result) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = HTMLPlaygroundApplication.getInstance().getAuthManager().getCurrentUser().getUid();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //Use where to filter in the documents where the uid equals the user id
                firestore.collection("settings").whereEqualTo("uid", uid).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (!documents.isEmpty()) {
                            // Update the document
                            firestore.collection("settings").document(documents.get(0).getId()).set(new SavedSetting(uid, autoRefresh));
                            result.accept(true);
                        } else {
                            // Save a new document
                            firestore.collection("settings").add(new SavedSetting(uid, autoRefresh)).addOnCompleteListener(task1 -> {
                                result.accept(task1.isSuccessful());
                            });
                        }
                    }
                });
                return null;
            }
        }.execute();
    }

    public static void getSettings(Callback<SavedSetting> callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String uid = HTMLPlaygroundApplication.getInstance().getAuthManager().getCurrentUser().getUid();
        new AsyncTask<Void, Void, SavedSetting>() {
            @Override
            protected SavedSetting doInBackground(Void... voids) {
                //Use where to filter in the documents where the uid equals the user id
                firestore.collection("settings").whereEqualTo("uid", uid).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<SavedSetting> documents = task.getResult().toObjects(SavedSetting.class);
                        if (!documents.isEmpty()) {
                            callback.accept(documents.get(0));
                        } else {
                            callback.accept(null);
                        }
                    }
                });
                return null;
            }
        }.execute();
    }

    public interface FireDataStoreCallback {
        void onCallback(LinkedList<SavedHTML> result);
    }

    public interface Callback<T> {
        void accept(T result);
    }
}
