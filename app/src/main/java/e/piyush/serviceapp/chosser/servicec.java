package e.piyush.serviceapp.chosser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import e.piyush.serviceapp.Customersetting;
import e.piyush.serviceapp.R;

public class servicec extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicec);
        RecyclerView servicelist=(RecyclerView)findViewById(R.id.servicelist);
        servicelist.setLayoutManager(new LinearLayoutManager(this));
        String[] Services={"Home Cleaning","Furniture Assembly","Plumbing","Handyman Service","Electrical Service","Interior Painting Services","Moving Help","Lawn Trimming"};
        servicelist.setAdapter(new serviceAdapter(Services));


    }
    public void Info(View v)
    {
        startActivity(new Intent(servicec.this,Customersetting.class));
    }
}
