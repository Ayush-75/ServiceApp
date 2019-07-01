package e.piyush.serviceapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import e.piyush.serviceapp.Interface.IFirebaseLoadDone;
import e.piyush.serviceapp.Model.User;
import e.piyush.serviceapp.ViewHolder.ServiceRequestViewHolder;
import e.piyush.serviceapp.utils.Common;

public class ServiceRequestActivity extends AppCompatActivity implements IFirebaseLoadDone {

    FirebaseRecyclerAdapter<User, ServiceRequestViewHolder> adapter,searchAdapter;
    RecyclerView recyclerView_all_user;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String> suggestList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request);

        searchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest=new ArrayList<>();
                for (String search:suggestList)
                {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled){
                if(!enabled){
                    if (adapter !=null)
                    {
                        recyclerView_all_user.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        recyclerView_all_user=(RecyclerView)findViewById(R.id.recycler_all);
        recyclerView_all_user.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView_all_user.setLayoutManager(layoutManager);
        recyclerView_all_user.addItemDecoration(new DividerItemDecoration(this,((LinearLayoutManager) layoutManager).getOrientation()));


        firebaseLoadDone=this;

        loadServiceRequestList();
        loadSearchData();
    }

    private void startSearch(String search_value) {
        Query query=FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.SERVICE_REQUEST)
                .orderByChild("name")
                .startAt(search_value);
        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, ServiceRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ServiceRequestViewHolder holder, int position, @NonNull final User model) {
                holder.txt_user_email.setText(model.getEmail());
                holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletFriendRequest(model,false);
                        addToAcceptList(model);
                        addUserToFriendContact(model);

                    }
                });

                holder.btn_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletFriendRequest(model,true);

                    }
                });


            }

            @NonNull
            @Override
            public ServiceRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView= LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_service_request,viewGroup,false);
                return new ServiceRequestViewHolder(itemView);
            }
        };

        searchAdapter.startListening();
        recyclerView_all_user.setAdapter(searchAdapter);

    }

    private void loadServiceRequestList() {
        Query query=FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.SERVICE_REQUEST);
        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, ServiceRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ServiceRequestViewHolder holder, int position, @NonNull final User model) {
                holder.txt_user_email.setText(model.getEmail());
                holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletFriendRequest(model,false);
                        addToAcceptList(model);
                        addUserToFriendContact(model);

                    }
                });

                holder.btn_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletFriendRequest(model,true);

                    }
                });


            }

            @NonNull
            @Override
            public ServiceRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView= LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_service_request,viewGroup,false);
                return new ServiceRequestViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerView_all_user.setAdapter(adapter);


    }

    private void addUserToFriendContact(User model)  {
        DatabaseReference acceptList=FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(model.getUid())
                .child(Common.ACCEPT_LIST);

        acceptList.child(model.getUid()).setValue(Common.loggedUser);
    }


    private void addToAcceptList(User model) {
        DatabaseReference acceptList=FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);

        acceptList.child(model.getUid()).setValue(model);
    }



    @Override
    protected void onStop() {
        if (adapter !=null)
            adapter.stopListening();
        if (searchAdapter !=null)
            searchAdapter.stopListening();
        super.onStop();
    }

    private void loadSearchData() {


        final List<String> lstUserEmail=new ArrayList<>();
        DatabaseReference userList=FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.SERVICE_REQUEST);
        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapShot:dataSnapshot.getChildren())
                {
                    User user=userSnapShot.getValue(User.class);
                    lstUserEmail.add(user.getEmail());
                }
                firebaseLoadDone.onFirebaseloadUserNameDone(lstUserEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());

            }
        });


    }
    private void deletFriendRequest(final User model,final boolean isShowMessage){
        DatabaseReference serviceRequest=FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.SERVICE_REQUEST);
        serviceRequest.child(model.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (isShowMessage)
                            Toast.makeText(ServiceRequestActivity.this, "Remove", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onFirebaseloadUserNameDone(List<String> lstEmail) {
        searchBar.setLastSuggestions(lstEmail);

    }
    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }
}
