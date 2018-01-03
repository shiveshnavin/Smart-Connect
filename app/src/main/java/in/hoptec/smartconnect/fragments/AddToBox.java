package in.hoptec.smartconnect.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.hoptec.smartconnect.Home;
import in.hoptec.smartconnect.R;
import in.hoptec.smartconnect.adapters.BoxesAdapter;
import in.hoptec.smartconnect.adapters.BoxesAdapterH;
import in.hoptec.smartconnect.database.Box;
import in.hoptec.smartconnect.database.BoxMeta;
import in.hoptec.smartconnect.utils.GenricCallback;
import in.hoptec.smartconnect.utils.Transact;
import in.hoptec.smartconnect.utl;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddToBox extends Fragment {

    public AddToBox() {
    }


    private RecyclerView boxesRec,boxesAddedRec;

    private RecyclerView.Adapter boxAdapter,boxAddAdapter;
    private RecyclerView.LayoutManager mLayoutManager,mLayoutManager2;



    public Context ctx;
    public Activity act;

    ImageView del,add;
    EditText box_name;

    Transact activityCommunicator;
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
         activityCommunicator =(Transact)getActivity();
    }

    View view;
    ArrayList<BoxesAdapter.Dummy> box_list,box_added;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_add, container, false);

        ctx=getContext();
        act=getActivity();

        box_name=(EditText)view.findViewById(R.id.box_name);
        add=(ImageView)view.findViewById(R.id.add);
        del=(ImageView)view.findViewById(R.id.del);

        boxesRec =(RecyclerView)view.findViewById(R.id.boxes);
        boxesAddedRec =(RecyclerView)view.findViewById(R.id.boxes_added);
        mLayoutManager = new LinearLayoutManager(ctx);
        mLayoutManager2 = new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false);





        box_name.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    //do here your stuff f

                   // add.setImageResource(R.drawable.avd_plus_to_chk);
                    utl.animate_avd(add);

                    String bx=box_name.getText().toString();
                    BoxesAdapter.Dummy dm=new BoxesAdapter.Dummy();
                    dm.boxData =new BoxMeta();
                    dm.boxData.id=bx;
                    box_added.add(dm);

                    boxAddAdapter.notifyItemInserted(boxAddAdapter.getItemCount()-1);
                   // boxAddAdapter.notifyDataSetChanged();

                    box_name.setText("");

                    return true;
                }
                return false;

            }

        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 utl.animate_avd(add);

                String bx=box_name.getText().toString();
                BoxesAdapter.Dummy cat=new BoxesAdapter.Dummy();
                cat.boxData =new BoxMeta();
                cat.boxData.id=bx;


                box_added.add(cat);
                boxAddAdapter.notifyItemInserted(box_added.size()-1);
                boxesAddedRec.smoothScrollToPosition(box_added.size()-1);
                boxAdapter.notifyDataSetChanged();




                box_name.setText("");

            }
        });






        box_list =new ArrayList<>();

        int i=0;
        do {
            box_list.add(new BoxesAdapter.Dummy(i));
        } while (i++<10);


        box_added =new ArrayList<>();



        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utl.animate_avd(del);
                utl.snack(act, "Discard changes and exit ?", "EXIT", new GenricCallback() {
                    @Override
                    public void onStart() {


                        activityCommunicator.transactToState(Home.States.HOME);
                    }

                    @Override
                    public void onDo(Object obj) {

                    }

                    @Override
                    public void onDo(Object obj, Object obj2) {

                    }

                    @Override
                    public void onDone(Object obj) {

                    }
                });
            }
        });
        // Initialize a new instance of RecyclerView Adapter instance
        boxAdapter = new BoxesAdapter(ctx, box_list){

            @Override
            public void click(final int pos,final  BoxesAdapter.Dummy cat) {
                super.click(pos,cat);


                utl.l("REMOVING : "+utl.js.toJson(box_list.get(pos)));

                box_added.add(cat);
                boxAddAdapter.notifyItemInserted(box_added.size()-1);
                boxesAddedRec.smoothScrollToPosition(box_added.size()-1);
                boxAdapter.notifyDataSetChanged();


                box_list.remove(cat);
                notifyDataSetChanged();
                notifyItemRemoved(pos);



            }


        };


        boxAddAdapter = new BoxesAdapterH(ctx, box_added){

            @Override
            public void click(final int pos,final  BoxesAdapter.Dummy cat) {
                super.click(pos,cat);


                box_list.add(cat);
                boxAdapter.notifyItemInserted(box_list.size()-1);
                boxesRec.smoothScrollToPosition(box_list.size()-1);
                boxAdapter.notifyDataSetChanged();


                box_added.remove(cat);
                notifyDataSetChanged();
                notifyItemRemoved(pos);



            }


        };
        boxesAddedRec.setLayoutManager(mLayoutManager2);
        boxesRec.setLayoutManager(mLayoutManager);


        LandingAnimator animator = new LandingAnimator(new OvershootInterpolator(1f));
        boxesRec.setItemAnimator(animator);
        SlideInBottomAnimationAdapter alphaAdapter = new SlideInBottomAnimationAdapter(boxAdapter);
        alphaAdapter.setDuration(1000);
        boxesRec.setNestedScrollingEnabled(false);
        boxesRec.setAdapter(alphaAdapter);

        boxesAddedRec.setItemAnimator(animator);
        SlideInBottomAnimationAdapter alphaAdapter2 = new SlideInBottomAnimationAdapter(boxAddAdapter);
        alphaAdapter2.setDuration(1000);
        boxesAddedRec.setNestedScrollingEnabled(false);
        boxesAddedRec.setAdapter(alphaAdapter2);


        return view;
    }















}
