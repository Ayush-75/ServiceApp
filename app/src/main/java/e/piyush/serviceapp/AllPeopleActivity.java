package e.piyush.serviceapp;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e.piyush.serviceapp.Interface.IFirebaseLoadDone;
import e.piyush.serviceapp.Interface.IRecyclerItemClickListener;
import e.piyush.serviceapp.Model.MyResponse;
import e.piyush.serviceapp.Model.Request;
import e.piyush.serviceapp.Model.User;
import e.piyush.serviceapp.ViewHolder.UserViewHolder;
import e.piyush.serviceapp.remote.IFCMService;
import e.piyush.serviceapp.utils.Common;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AllPeopleActivity extends AppCompatActivity implements IFirebaseLoadDone {

    FirebaseRecyclerAdapter<User, UserViewHolder> adapter,searchAdapter;
    RecyclerView recyclerView_all_user;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String> suggestList= new ArrayList<>();

    IFCMService ifcmService;
    CompositeDisposable compositeDisposable=new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people);

        ifcmService=Common.getFCMService();

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

        loadUserList();
        loadSearchData();

    }

    private void loadSearchData() {
        final List<String> lstUserEmail=new ArrayList<>();
        DatabaseReference userList=FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION);
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

    private void loadUserList() {
        Query query=FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION);
        FirebaseRecyclerOptions<User>options=new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        adapter= new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {
                if (model.getEmail().equals(Common.loggedUser.getEmail()))
                {
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()).append(" (me) "));
                    holder.txt_user_email.setTypeface(holder.txt_user_email.getTypeface(), Typeface.ITALIC);
                }
                else {
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()));
                }
                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        showDialogRequest(model);


                    }
                });

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView= LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user,viewGroup,false);

                return new UserViewHolder(itemView);

            }
        };
        adapter.startListening();
        recyclerView_all_user.setAdapter(adapter);


    }

    private void showDialogRequest(final User model) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this,R.style.MyRequestDialog);
        alertDialog.setTitle("Request service");
        alertDialog.setMessage("sent service request to "+model.getEmail());
        alertDialog.setIcon(R.drawable.ic_account);
        alertDialog.setNegativeButton("Cencel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference acceptList=FirebaseDatabase.getInstance()
                        .getReference(Common.USER_INFORMATION)
                        .child(Common.loggedUser.getUid())
                        .child(Common.ACCEPT_LIST);
                acceptList.orderByKey().equalTo(model.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null)
                                    sendFriendRequest(model);
                                else
                                    Toast.makeText(AllPeopleActivity.this, "you already send service request to"+model.getEmail()+"" +
                                            "!!!!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }
        });
        alertDialog.show();

    }

    private void sendFriendRequest(final User model) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference(Common.TOKENS);

        tokens.orderByKey().equalTo(model.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null)
                            Toast.makeText(AllPeopleActivity.this, "Token error", Toast.LENGTH_SHORT).show();
                        else {
                            Request request=new Request();


                            Map<String,String> datasend=new HashMap<>();
                            datasend.put(Common.FROM_UID,Common.loggedUser.getUid());
                            datasend.put(Common.FROM_NAME,Common.loggedUser.getEmail());
                            datasend.put(Common.TO_UID,model.getUid());
                            datasend.put(Common.TO_NAME,model.getEmail());

                            request.setTo(dataSnapshot.child(model.getUid()).getValue(String.class));
                            request.setData(datasend);

                            compositeDisposable.add(ifcmService.sendFriendRequestToUser(request)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<MyResponse>() {
                                           @Override
                                           public void accept(MyResponse myResponse) throws Exception {
                                               if (myResponse.success == 1)
                                                   Toast.makeText(AllPeopleActivity.this, "request Send!", Toast.LENGTH_SHORT).show();

                                           }
                                       }, new Consumer<Throwable>() {
                                           @Override
                                           public void accept(Throwable throwable) throws Exception {
                                               Toast.makeText(AllPeopleActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();


                                           }
                            }));


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    @Override
    protected void onStop() {
        if (adapter !=null)
            adapter.stopListening();
        if (searchAdapter !=null)
            searchAdapter.stopListening();

        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter !=null)
            adapter.startListening();
        if (searchAdapter !=null)
            searchAdapter.startListening();
    }

    private void startSearch(String text_search) {
        Query query=FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .orderByChild("name")
                .startAt(text_search);
        FirebaseRecyclerOptions<User>options=new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        searchAdapter= new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull final User model) {
                if (model.getEmail().equals(Common.loggedUser.getEmail()))
                {
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()).append(" (me) "));
                    holder.txt_user_email.setTypeface(holder.txt_user_email.getTypeface(), Typeface.ITALIC);
                }
                else {
                    holder.txt_user_email.setText(new StringBuilder(model.getEmail()));
                }
                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        showDialogRequest(model);

                    }
                });

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView= LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user,viewGroup,false);

                return new UserViewHolder(itemView);

            }
        };
        searchAdapter.startListening();
        recyclerView_all_user.setAdapter(searchAdapter);
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
