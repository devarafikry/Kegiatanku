package ttc.project.absenonline.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ttc.project.absenonline.R;
import ttc.project.absenonline.activity.DetailParticipantActivity;
import ttc.project.absenonline.activity.EditActivity;
import ttc.project.absenonline.model.ActivitySchedule;
import ttc.project.absenonline.model.Schedule;
import ttc.project.absenonline.utils.DateUtils;
import ttc.project.absenonline.utils.FirebaseUtils;
import ttc.project.absenonline.utils.SnackbarUtils;
import ttc.project.absenonline.viewholder.ActivityViewHolder;
import ttc.project.absenonline.viewholder.ScheduleViewHolder;

/**
 * Created by Fikry-PC on 12/8/2017.
 */

public class ScheduleFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Schedule, ScheduleViewHolder> {

    Context mContext;
    Snackbar s;
    View rootView;
    GoogleApiClient mClient;
    public ScheduleFirebaseRecyclerAdapter(
            Context context,
            GoogleApiClient client,
            Snackbar snackbar,
            View rootView,
            Class<Schedule> modelClass,
            int modelLayout,
            Class<ScheduleViewHolder> viewHolderClass,
            Query query) {
        super(modelClass, modelLayout, viewHolderClass, query);
        this.mContext = context;
        this.mClient = client;
        this.s = snackbar;
        this.rootView = rootView;
    }

    @Override
    protected void populateViewHolder(final ScheduleViewHolder viewHolder, final Schedule activity, int position) {
        String activityId = activity.getActivityId();
        FirebaseUtils.getDatabaseReference().child(mContext.getString(R.string.node_activity))
                .child(activityId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ActivitySchedule activity = dataSnapshot.getValue(ActivitySchedule.class);
                        if(activity != null){
                            viewHolder.tv_name.setText(activity.getName());
                            viewHolder.tv_date.setText(DateUtils.getFriendlyDate(
                                    mContext, activity.getDate()
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
                                    .child(mContext.getString(R.string.node_user_schedule))
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
}
