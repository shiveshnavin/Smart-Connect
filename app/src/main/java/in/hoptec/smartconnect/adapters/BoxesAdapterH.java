package in.hoptec.smartconnect.adapters;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import in.hoptec.smartconnect.R;
import in.hoptec.smartconnect.database.Box;
import in.hoptec.smartconnect.database.BoxMeta;


public class BoxesAdapterH extends  RecyclerView.Adapter<BoxesAdapterH.CustomViewHolder> {
    private List<BoxesAdapter.Dummy> feedItemList;
    private Context mContext;

    public BoxesAdapterH(Context context, List<BoxesAdapter.Dummy> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_boxh,viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {

        final int pos=customViewHolder.getAdapterPosition();
        final BoxesAdapter.Dummy item=feedItemList.get(pos);
        customViewHolder.textView.setText(Html.fromHtml(item.boxData.id));
        customViewHolder.base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(pos,item);
            }
        });
        final Animation press= AnimationUtils.loadAnimation(mContext,R.anim.rec_zoom_in);
        final Animation release= AnimationUtils.loadAnimation(mContext,R.anim.rec_zoom_nomal);

        customViewHolder.base.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        customViewHolder.base.startAnimation(press);
                        break;
                    case MotionEvent.ACTION_UP:

                        customViewHolder.base.startAnimation(release);

                    case MotionEvent.ACTION_CANCEL:

                        customViewHolder.base.startAnimation(release);



                        break;
                    default:
                        break;
                }



                return false;
            }
        });

       // customViewHolder.root.getLayoutParams().height = utl.getRandomIntInRange(250,75);


        customViewHolder.base.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {



                final Drawable drawable = customViewHolder.del.getDrawable();

                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                }


                clickLong(pos);
                return true;
            }
        });
    }



    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder
    {

        TextView textView;
        ImageView del,icon;
        View base;
        LinearLayout root;


        public CustomViewHolder(View v) {
            super(v);
            
            base=v;
            textView=(TextView) base.findViewById(R.id.name);
            root=(LinearLayout) base.findViewById(R.id.root);
            del=(ImageView) base.findViewById(R.id.del);
            icon=(ImageView) base.findViewById(R.id.icon);
            
            

        }
    }

    public void click(int pos, BoxesAdapter.Dummy cat)
    {





    }


    public void clickLong(int pos)
    {


    }






}

