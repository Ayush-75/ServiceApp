package e.piyush.serviceapp.chosser;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import e.piyush.serviceapp.Customersetting;
import e.piyush.serviceapp.MainActivity;
import e.piyush.serviceapp.R;

import static android.support.v4.content.ContextCompat.startActivity;

public class serviceAdapter extends RecyclerView.Adapter<serviceAdapter.serviceViewHolder> {

    private String[] data;
    public serviceAdapter(String[] data){
        this.data=data;

    }


    @NonNull
    @Override
    public serviceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
        View view=inflater.inflate(R.layout.list_item_service,viewGroup,false);
        return new serviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull serviceViewHolder serviceViewHolder, int i) {
        String title=data[i];
        serviceViewHolder.txttitle.setText(title);

    }


    @Override
    public int getItemCount() {
        return data.length;
    }

    public class serviceViewHolder extends RecyclerView.ViewHolder {
        ImageView imgicon;
        TextView txttitle;
        public serviceViewHolder(@NonNull View itemView) {
            super(itemView);
            imgicon=(ImageView) itemView.findViewById(R.id.firsts);
            txttitle=(TextView) itemView.findViewById(R.id.title1);


        }
    }

    }



