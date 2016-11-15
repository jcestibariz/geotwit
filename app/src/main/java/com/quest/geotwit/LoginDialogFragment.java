package com.quest.geotwit;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class LoginDialogFragment extends DialogFragment {

    private static final String TAG = "LoginDialogFragment";

    private TwitterLoginButton button;

    public interface LoginListener {
        void onLogin(TwitterSession session);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(activity.getLayoutInflater().inflate(R.layout.login, null));

        button = (TwitterLoginButton) dialog.findViewById(R.id.loginButton);
        button.setText(R.string.login);
        button.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.i(TAG, "success: " + result);
                ((LoginListener) activity).onLogin(result.data);
                dismiss();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.i(TAG, "failure: " + exception);
                // TODO show something?
            }
        });

        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        button.onActivityResult(requestCode, resultCode, data);
    }
}
