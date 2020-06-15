package com.example.tableorderserver.Service;

import com.example.tableorderserver.Common.Common;
import com.example.tableorderserver.Module.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseService extends FirebaseMessagingService {

    public void onTokenRefresh(){
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        updateToServer(tokenRefreshed);

    }

    private void updateToServer(String tokenRefreshed) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token token = new Token(tokenRefreshed,true);
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}
