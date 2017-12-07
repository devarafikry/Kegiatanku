package ttc.project.absenonline.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
        fireAdapter = new FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder>(
                Schedule.class,
                R.layout.activity_item,
                ScheduleViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final ScheduleViewHolder viewHolder, Schedule model, int position) {
                String activityId = model.getActivityId();
                FirebaseUtils.getDatabaseReference().child(getString(R.string.node_activity))
                        .child(activityId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ActivitySchedule activity = dataSnapshot.getValue(ActivitySchedule.class);
                                if(activity != null){
                                    viewHolder.tv_name.setText(activity.getName());
                                    viewHolder.tv_date.setText(DateUtils.getFriendlyDate(
                                            getContext(), activity.getDate()
                                    ));
                                    viewHolder.tv_time.setText(DateUtils.getFriendlyTimeStartAndEnd(
                                            activity.getTime_start(),
                                            activity.getTime_end()
                                    ));
                                    Places.GeoDataApi.getPlaceById(mClient, activity.getPlace_id())
                                            .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                                @Override
                                                public void onResult(PlaceBuffer places) {
                                                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                                        final Place myPlace = places.get(0);
                                                        viewHolder.tv_place.setText(myPlace.getAddress());
                                                        Log.i("ManageActivityFragment", "Place found: " + myPlace.getName());
                                                    } else {
                                                        Log.e("ManageActivityFragment", "Place not found");
                                                    }
                                                    places.release();
                                                }
                                            });
                                } else{
                                    FirebaseUtils.getDatabaseReference()
                                            .child(getString(R.string.node_user_schedule))
                                            .child(FirebaseUtils.getCurrentUser().getUid())
                                            .child(dataSnapshot.getKey())
                                            .removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };
        rv_schedules.setLayoutManager(linearLayoutManager);
        rv_schedules.setAdapter(fireAdapter);
        return view;
    }

}
