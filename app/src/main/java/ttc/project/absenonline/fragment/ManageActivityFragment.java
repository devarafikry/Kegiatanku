package ttc.project.absenonline.fragment;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import ttc.project.absenonline.activity.DetailParticipantActivity;
import ttc.project.absenonline.activity.EditActivity;
import ttc.project.absenonline.adapter.ActivityFirebaseRecyclerAdapter;
import ttc.project.absenonline.utils.DateUtils;
import ttc.project.absenonline.utils.FirebaseUtils;
import ttc.project.absenonline.R;
import ttc.project.absenonline.activity.AddActivity;
import ttc.project.absenonline.model.ActivitySchedule;
import ttc.project.absenonline.utils.SnackbarUtils;
import ttc.project.absenonline.viewholder.ActivityViewHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManageActivityFragment extends Fragment {

    @BindView(R.id.fab_add) FloatingActionButton fab_add;
    @BindView(R.id.rv_activities)
    RecyclerView rv_activities;
    @BindView(R.id.rootView) View rootView;

    Snackbar s;
    private GoogleApiClient mClient;

    FirebaseRecyclerAdapter<ActivitySchedule, ActivityViewHolder> fireAdapter;

    public ManageActivityFragment() {
        // Required empty public constructor
    }

    public void setGoogleApiClient(GoogleApiClient client){
        this.mClient = client;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_manage_activity, container, false);
        ButterKnife.bind(this, view);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        Query query = FirebaseUtils.getDatabaseReference().child(getString(R.string.node_user_activity))
                .child(FirebaseUtils.getCurrentUser().getUid());
        fireAdapter = new ActivityFirebaseRecyclerAdapter(
                getContext(),
                mClient,
                s,
                rootView,
                ActivitySchedule.class,
                R.layout.activity_item,
                ActivityViewHolder.class,
                query
        );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv_activities.setAdapter(fireAdapter);
        rv_activities.setLayoutManager(linearLayoutManager);
        return view;
    }

}
