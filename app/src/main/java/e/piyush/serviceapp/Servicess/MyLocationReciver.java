package e.piyush.serviceapp.Servicess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import e.piyush.serviceapp.utils.Common;
import io.paperdb.Paper;

public class MyLocationReciver extends BroadcastReceiver {
    public static final String ACTION="e.piyush.serviceapp.UPDATE_LOCATION";

    DatabaseReference publiclocation;
    String uid;

    public MyLocationReciver() {
        publiclocation= FirebaseDatabase.getInstance().getReference(Common.PUBLIC_LOCATION);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Paper.init(context);

        uid = Paper.book().read(Common.USER_UID_SAVE_KEY);
        if (intent !=null){
            final String action = intent.getAction();
            if (action.equals(ACTION)){
                LocationResult result = LocationResult.extractResult(intent);
                if (result !=null)
                {
                    Location location=result.getLastLocation();
                    if (Common.loggedUser !=null)
                    {
                        publiclocation.child(Common.loggedUser.getUid()).setValue(location);
                    }
                    else
                    {
                        publiclocation.child(uid).setValue(location);
                    }
                }
            }
        }


    }
}
