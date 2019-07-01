package e.piyush.serviceapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import e.piyush.serviceapp.chosser.servicec;

public class select extends AppCompatActivity {
    Button Gc,Cs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        Gc=(Button)findViewById(R.id.goC);
        Cs=(Button)findViewById(R.id.goS);

        Gc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(select.this, servicec.class));
            }
        });
        Cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(select.this,Servicesetting.class));
            }
        });
    }
}
