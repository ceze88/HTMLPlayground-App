package me.ceze88.htmlplayground.auth;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthManager {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    public FirebaseAuthManager() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void signIn(String email, String password, OnCompleteListener<AuthResult> listener) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public void signUp(String email, String password, OnCompleteListener<AuthResult> listener) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }
}
