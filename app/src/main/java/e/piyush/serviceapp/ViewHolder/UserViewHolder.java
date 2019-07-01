package e.piyush.serviceapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import e.piyush.serviceapp.Interface.IRecyclerItemClickListener;
import e.piyush.serviceapp.R;

public class UserViewHolder extends RecyclerView.ViewHolder implements ExpandableListView.OnClickListener {
    public TextView txt_user_email;
    IRecyclerItemClickListener iRecyclerItemClickListener;

    public void setiRecyclerItemClickListener(IRecyclerItemClickListener iRecyclerItemClickListener) {
        this.iRecyclerItemClickListener = iRecyclerItemClickListener;
    }

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_user_email=(TextView)itemView.findViewById(R.id.txt_user_email);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       iRecyclerItemClickListener.onItemClickListener(view,getAdapterPosition());

    }
}
