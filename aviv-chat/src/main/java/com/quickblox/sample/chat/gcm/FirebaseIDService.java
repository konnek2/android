package com.quickblox.sample.chat.gcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.quickblox.messages.services.SubscribeService;

/**
 * Created by Lenovo on 30-06-2017.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Log.d("CHATTOEKN", "  FirebaseIDService  onTokenRefresh");


        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SubscribeService.subscribeToPushes(this, true);
        Log.d("CHATTOEKN", "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        Log.d("CHATTOEKN", "   FirebaseIDService  sendRegistrationToServer  ");
    }
}
