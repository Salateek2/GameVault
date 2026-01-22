package com.example.gamevault;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private String userId;  // Firebase anonymous user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sign in anonymously
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    userId = user.getUid();
                    Log.d("MainActivity", "Anonymous user ID: " + userId);
                    // You can use userId to save/load favorites from Firestore
                }
            } else {
                Log.e("MainActivity", "Anonymous sign-in failed", task.getException());
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new HomeFragment())
                    .commit();
        }
    }

    public String getUserId() {
        return userId;
    }
}
