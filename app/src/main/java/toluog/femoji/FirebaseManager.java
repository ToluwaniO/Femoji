package toluog.femoji;

import android.app.Activity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class FirebaseManager {

    private static FirebaseAuth auth = null;
    private static final int RC_SIGN_IN = 123;


    public static boolean signedIn() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        return auth.getCurrentUser() != null;
    }

    public static void fbUi(Activity activity) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

}
