package ttc.project.absenonline.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ttc.project.absenonline.R;

/**
 * Created by Fikry-PC on 12/6/2017.
 */

public class ParticipantViewHolder extends RecyclerView.ViewHolder {
    public TextView participant_name, participant_email, participant_in;
    public ParticipantViewHolder(View itemView) {
        super(itemView);
        participant_name = itemView.findViewById(R.id.participant_name);
        participant_email = itemView.findViewById(R.id.participant_email);
        participant_in = itemView.findViewById(R.id.participant_time_in);
    }
}
