package in.hoptec.smartconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.hoptec.smartconnect.R;


public class GRecyclerAdapter extends RecyclerView.Adapter<GRecyclerAdapter.CustomViewHolder> {
    private List<Dummy> feedItemList;
    private Context mContext;

    public GRecyclerAdapter(Context context, List<Dummy> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_box, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {

        final Dummy item=feedItemList.get(i);
        
        customViewHolder.textView.setText(Html.fromHtml(item.getData(i)));
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder
    {

        TextView textView;
        View base;


        public CustomViewHolder(View v) {
            super(v);
            
            base=v;
            textView=(TextView) base.findViewById(R.id.name);

            
            

        }
    }


    public class Dummy
    {
        String data="TEST";
        
        public String getData(int i)
        {
            return "Data : "+i;
        }
        
    }






}

