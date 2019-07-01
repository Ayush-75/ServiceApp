package e.piyush.serviceapp;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Arrays;
import java.util.List;

import e.piyush.serviceapp.Model.User;
import e.piyush.serviceapp.chosser.servicec;
import e.piyush.serviceapp.utils.Common;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    DatabaseReference user_information;
    private static final int RC_SIGN_IN = 123;
    List<AuthUI.IdpConfig> providers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);
        user_information=FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION);

        providers=Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ShowSignInOPtion();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "U must accept", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

    }

    private void ShowSignInOPtion() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            IdpResponse response= IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK)
            {
                final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                user_information.orderByKey()
                        .equalTo(firebaseUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()==null)
                                {
                                    if (!dataSnapshot.child(firebaseUser.getUid()).exists())
                                    {
                                        Common.loggedUser=new User(firebaseUser.getUid(),firebaseUser.getEmail());
                                        user_information.child(Common.loggedUser.getUid())
                                                .setValue(Common.loggedUser);
                                    }

                                }
                                else
                                {
                                    Common.loggedUser=dataSnapshot.child(firebaseUser.getUid()).getValue(User.class);
                                }
                                Paper.book().write(Common.USER_UID_SAVE_KEY,Common.loggedUser.getUid());
                                UpdateToken(firebaseUser);
                                setupUI();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }
    }

    private void setupUI() {
        startActivity(new Intent(MainActivity.this, select.class));
        finish();
    }

    private void UpdateToken(final FirebaseUser firebaseUser) {
        final DatabaseReference tokens=FirebaseDatabase.getInstance()
                .getReference(Common.TOKENS);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        tokens.child(firebaseUser.getUid())
                                .setValue(instanceIdResult.getToken());

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}