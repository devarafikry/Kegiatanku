package ttc.project.absenonline.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import ttc.project.absenonline.model.ActivitySchedule;
import ttc.project.absenonline.utils.DateUtils;
import ttc.project.absenonline.utils.FirebaseUtils;
import ttc.project.absenonline.R;
import ttc.project.absenonline.model.Schedule;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.edt_id)
    EditText edt_id;
    @BindView(R.id.btn_search)
    Button btn_search;
    @BindView(R.id.btn_add)
    Button btn_add;
    @BindView(R.id.containerCard)
    View containerCard;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_date)
    TextView tv_date;
    @BindView(R.id.tv_place)
    TextView tv_place;
    @BindView(R.id.status)
    TextView tv_status;

    GoogleApiClient mClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cari Kegiatan");
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUtils.getDatabaseReference()
                        .child(getString(R.string.node_activity))
                        .child(edt_id.getText().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() != null){
                                    tv_status.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                    tv_status.setVisibility(View.VISIBLE);
                                    tv_status.setText("Data ditemukan!");
                                    containerCard.setVisibility(View.VISIBLE);

                                    final ActivitySchedule activity = dataSnapshot.getValue(ActivitySchedule.class);
                                    tv_name.setText(activity.getName());
                                    tv_date.setText(DateUtils.getFriendlyDate(
                                            SearchActivity.this, activity.getDate()
                                    ));
                                    tv_time.setText(DateUtils.getFriendlyTimeStartAndEnd(
                                            activity.getTime_start(),
                                            activity.getTime_end()
                                    ));
                                    Places.GeoDataApi.getPlaceById(mClient, activity.getPlace_id())
                                            .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                                @Override
                                                public void onResult(PlaceBuffer places) {
                                                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                                        final Place myPlace = places.get(0);
                                                        tv_place.setText(myPlace.getAddress());
                                                        Log.i("ManageActivityFragment", "Place found: " + myPlace.getName());
                                                    } else {
                                                        Log.e("ManageActivityFragment", "Place not found");
                                                    }
                                                    places.release();
                                                }
                                            });
                                    FirebaseUtils.getDatabaseReference().child(getString(R.string.node_user_schedule))
                                            .child(FirebaseUtils.getCurrentUser().getUid())
                                            .child(dataSnapshot.getKey())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(final DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.getValue() == null){
                                                        btn_add.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                Schedule schedule = new Schedule(dataSnapshot.getKey());
                                                                FirebaseUtils.getDatabaseReference().child(getString(R.string.node_user_schedule))
                                                                        .child(FirebaseUtils.getCurrentUser().getUid())
                                                                        .child(dataSnapshot.getKey())
                                                                        .setValue(schedule)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                SearchActivity.this.finish();
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                        btn_add.setText("Tambahkan Kegiatan");
                                                    } else{
                                                        btn_add.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                            }
                                                        });
                                                        btn_add.setText("Anda sudah terdaftar pada kegiatan ini");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                } else{
                                    tv_status.setVisibility(View.VISIBLE);
                                    tv_status.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                    tv_status.setText("Mohon maaf data tidak ditemukan.");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}
