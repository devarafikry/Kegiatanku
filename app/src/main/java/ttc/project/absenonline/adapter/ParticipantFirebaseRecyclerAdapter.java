package ttc.project.absenonline.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

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

import ttc.project.absenonline.R;
import ttc.project.absenonline.model.ActivitySchedule;
import ttc.project.absenonline.model.Participant;
import ttc.project.absenonline.model.Schedule;
import ttc.project.absenonline.utils.DateUtils;
import ttc.project.absenonline.utils.FirebaseUtils;
import ttc.project.absenonline.viewholder.ParticipantViewHolder;
import ttc.project.absenonline.viewholder.ScheduleViewHolder;

/**
 * Created by Fikry-PC on 12/8/2017.
 */

public class ParticipantFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Participant, ParticipantViewHolder> {

    public ParticipantFirebaseRecyclerAdapter(
            Class<Participant> modelClass,
            int modelLayout,
            Class<ParticipantViewHolder> viewHolderClass,
            Query query) {
        super(modelClass, modelLayout, viewHolderClass, query);
    }

    @Override
    protected void populateViewHolder(final ParticipantViewHolder viewHolder, final Participant participant, int position) {
        viewHolder.participant_name.setText(participant.getName());
        viewHolder.participant_email.setText(participant.getEmail());
        viewHolder.participant_in.setText(DateUtils.getFriendlyTime(participant.getDate_in()));
    }
}
