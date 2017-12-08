package ttc.project.absenonline.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import ttc.project.absenonline.adapter.ActivityFirebaseRecyclerAdapter;
import ttc.project.absenonline.adapter.ScheduleFirebaseRecyclerAdapter;
import ttc.project.absenonline.utils.DateUtils;
import ttc.project.absenonline.utils.FirebaseUtils;
import ttc.project.absenonline.R;
import ttc.project.absenonline.activity.SearchActivity;
import ttc.project.absenonline.model.ActivitySchedule;
import ttc.project.absenonline.model.Schedule;
import ttc.project.absenonline.viewholder.ActivityViewHolder;
import ttc.project.absenonline.viewholder.ScheduleViewHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyScheduleFragment extends Fragment {

    @BindView(R.id.rv_schedules)
    RecyclerView rv_schedules;
    @BindView(R.id.fab_search)
    FloatingActionButton fab_search;
    private GoogleApiClient mClient;
    private Snackbar s;
    @BindView(R.id.rootView) View rootView;

    FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder> fireAdapter;

    public MyScheduleFragment() {
        // Required empty public constructor
    }

    public void setGoogleApiClient(GoogleApiClient client){
        this.mClient = client;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_schedule, container, false);
        ButterKnife.bind(this, view);
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        Query query = FirebaseUtils.getDatabaseReference().child(getString(R.string.node_user_schedule))
                .child(FirebaseUtils.getCurrentUser().getUid());
        fireAdapter = new ScheduleFirebaseRecyclerAdapter(
                getContext(),
                mClient,
                s,
                rootView,
                Schedule.class,
                R.layout.activity_item,
                ScheduleViewHolder.class,
                query
        );
        rv_schedules.setLayoutManager(linearLayoutManager);
        rv_schedules.setAdapter(fireAdapter);
        return view;
    }

}
