package ttc.project.absenonline.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import ttc.project.absenonline.R;
import ttc.project.absenonline.model.Participant;
import ttc.project.absenonline.utils.DateUtils;
import ttc.project.absenonline.utils.FirebaseUtils;
import ttc.project.absenonline.viewholder.ParticipantViewHolder;

public class DetailParticipantActivity extends AppCompatActivity {

    @BindView(R.id.rv_participants)
    RecyclerView rv_participants;
    @BindView(R.id.title)
    TextView tv_title;
    public static final String EXTRA_ACTIVITY_KEY = "activityKey";
    public static final String EXTRA_TITLE_KEY = "titleKey";

    FirebaseRecyclerAdapter<Participant, ParticipantViewHolder> fireAdapter;
    String activityKey,title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_participant);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attendance");
        activityKey = getIntent().getStringExtra(EXTRA_ACTIVITY_KEY);
        title = getIntent().getStringExtra(EXTRA_TITLE_KEY);
        if(activityKey != null && title != null){
            tv_title.setText(title);
            Query query = FirebaseUtils.getDatabaseReference()
                    .child(getString(R.string.node_attendance))
                    .child(activityKey);
            fireAdapter = new FirebaseRecyclerAdapter<Participant, ParticipantViewHolder>(
                    Participant.class,
                    R.layout.participant_item,
                    ParticipantViewHolder.class,
                    query
            ) {
                @Override
                protected void populateViewHolder(ParticipantViewHolder viewHolder, Participant participant, int position) {
                    viewHolder.participant_name.setText(participant.getName());
                    viewHolder.participant_email.setText(participant.getEmail());
                    viewHolder.participant_in.setText(DateUtils.getFriendlyTime(participant.getDate_in()));
                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            rv_participants.setLayoutManager(linearLayoutManager);
            rv_participants.setAdapter(fireAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}
