package in.hoptec.smartconnect.adapters;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
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


public class BoxesAdapter extends RecyclerView.Adapter<BoxesAdapter.CustomViewHolder> {
    private List<Dummy> feedItemList;
    private Context mContext;

    public BoxesAdapter(Context context, List<Dummy> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_box,viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder cv, final int i) {

        final int pos= cv.getAdapterPosition();
        final Dummy item=feedItemList.get(pos);

        cv.base.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(pos,item);
            }
        });
        final Animation press= AnimationUtils.loadAnimation(mContext,R.anim.rec_zoom_in);
        final Animation release= AnimationUtils.loadAnimation(mContext,R.anim.rec_zoom_nomal);


        cv.textView.setText(""+item.rs.SSID);
        cv.btm_t0.setText(""+item.rs.level);
        cv.btm_t1.setText(""+item.rs.capabilities);
        cv.btm_t2.setText(""+item.rs.frequency);

        cv.base.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        cv.base.startAnimation(press);
                        break;
                    case MotionEvent.ACTION_UP:

                        cv.base.startAnimation(release);

                    case MotionEvent.ACTION_CANCEL:

                        cv.base.startAnimation(release);



                        break;
                    default:
                        break;
                }



                return false;
            }
        });

       // cv.root.getLayoutParams().height = utl.getRandomIntInRange(250,75);


        cv.base.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {



                final Drawable drawable = cv.del.getDrawable();

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

        TextView textView,btm_t0,btm_t1,btm_t2;
        ImageView del,icon;
        View base;
        LinearLayout root;


        public CustomViewHolder(View v) {
            super(v);
            
            base=v;
            textView=(TextView) base.findViewById(R.id.name);
            btm_t2=(TextView) base.findViewById(R.id.btm_t2);
            btm_t1=(TextView) base.findViewById(R.id.btm_t1);
            btm_t0=(TextView) base.findViewById(R.id.btm_t0);

            root=(LinearLayout) base.findViewById(R.id.root);
            del=(ImageView) base.findViewById(R.id.del);
            icon=(ImageView) base.findViewById(R.id.icon);
            
            

        }
    }


    public void click(int pos, Dummy cat)
    {





    }


    public void clickLong(int pos)
    {


    }

    public static class Dummy extends Box
    {
        String data="TEST";
        ScanResult rs;
        public Dummy(int i)
        {

            boxData=new BoxMeta();
            boxData.id=""+i;

        }
        public Dummy(ScanResult i)
        {

            rs=i;

        }

        public Dummy( )
        {}

        public String getData(int i)
        {
            return "Bucket No "+i;
        }
        
    }






}

