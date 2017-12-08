package ttc.project.absenonline.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ttc.project.absenonline.R;
import ttc.project.absenonline.fragment.DatePickerFragment;
import ttc.project.absenonline.fragment.TimePickerFragment;
import ttc.project.absenonline.model.ActivitySchedule;
import ttc.project.absenonline.utils.DateUtils;
import ttc.project.absenonline.utils.FirebaseUtils;
import ttc.project.absenonline.utils.SnackbarUtils;

public class EditActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, ResultCallback<PlaceBuffer> {
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;
    public static final String EXTRA_ACTIVITY_ID = "activityId";
    String activity_name_value;
    int activity_participant_count_value;
    Long activity_date_value;
    Long activity_start_time_value;
    Long activity_end_time_value;
    Place selectedPlace;
    String activityId;

    @BindView(R.id.rootView) View rootView;
    @BindView(R.id.activity_date_pick)
    ImageView activity_date_pick;
    @BindView(R.id.activity_start_pick)
    ImageView activity_start_pick;
    @BindView(R.id.activity_end_pick)
    ImageView activity_end_pick;
    @BindView(R.id.activity_name)
    TextView activity_name;
    @BindView(R.id.activity_participant_count)
    TextView activity_participant_count;
    @BindView(R.id.activity_place)
    TextView activity_place;
    @BindView(R.id.activity_date_friendly)
    TextView activity_date_friendly;
    @BindView(R.id.activity_start_time_friendly)
    TextView activity_start_time_friendly;
    @BindView(R.id.activity_end_time_friendly)
    TextView activity_end_time_friendly;
    @BindView(R.id.btn_pick_place)
    Button btn_pick_place;

    TimePickerDialog.OnTimeSetListener startTimeListener;
    TimePickerDialog.OnTimeSetListener endTimeListener;
    Snackbar s;
    private GoogleApiClient mClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Buat Kegiatan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mClient.connect();
        activityId = getIntent().getStringExtra(EXTRA_ACTIVITY_ID);
        if(activityId != null){
            FirebaseUtils.getDatabaseReference()
                    .child(getString(R.string.node_user_activity))
                    .child(FirebaseUtils.getCurrentUser().getUid())
                    .child(activityId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ActivitySchedule activity = dataSnapshot.getValue(ActivitySchedule.class);
                            activity_name.setText(activity.getName());
                            activity_participant_count.setText(String.valueOf(activity.getParticipant_count()));
                            activity_date_friendly.setText(DateUtils.getFriendlyDate(EditActivity.this, activity.getDate()));
                            activity_start_time_friendly.setText(DateUtils.getFriendlyTime(activity.getTime_start()));
                            activity_end_time_friendly.setText(DateUtils.getFriendlyTime(activity.getTime_end()));

                            activity_name_value = activity.getName();
                            activity_participant_count_value = activity.getParticipant_count();
                            activity_date_value = activity.getDate();
                            activity_start_time_value = activity.getTime_start();
                            activity_end_time_value = activity.getTime_end();
                            Places.GeoDataApi.getPlaceById(mClient, activity.getPlace_id())
                                    .setResultCallback(EditActivity.this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        startTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int minutes = minute + (hourOfDay*60);
                Log.d("Minutes",minutes+" minutes");
                activity_start_time_value =
                        TimeUnit.MINUTES.toMillis(minutes);
                activity_start_time_friendly.setText(DateUtils.getFriendlyTime(
                        activity_start_time_value)
                );
            }
        };
        endTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int minutes = minute + (hourOfDay*60);
                Log.d("Minutes",minutes+" minutes");
                activity_end_time_value =
                        TimeUnit.MINUTES.toMillis(minutes);
                activity_end_time_friendly.setText(DateUtils.getFriendlyTime(
                        activity_end_time_value)
                );
            }
        };
        activity_date_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setOnDateSetListener(EditActivity.this);
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        activity_start_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.setOnDateSetListener(startTimeListener);
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        activity_end_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.setOnDateSetListener(endTimeListener);
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        btn_pick_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.saveItem){
            saveSchedule();
        } else if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    private void saveSchedule() {
        String text_name = activity_name.getText().toString();
        String text_count = activity_participant_count.getText().toString();
        if(!TextUtils.isEmpty(text_name) &&
                !TextUtils.isEmpty(text_count) &&
                activity_date_value != null &&
                activity_start_time_value != null &&
                activity_end_time_value != null &&
                selectedPlace != null){
            activity_name_value = text_name;
            activity_participant_count_value = Integer.valueOf(text_count);
            if(activity_start_time_value < activity_end_time_value){
                saveToFirebaseDatabase();
            } else{
                SnackbarUtils.showSnackbar(
                        rootView,
                        s,
                        "Waktu selesai harus berada setelah waktu mulai",
                        Snackbar.LENGTH_LONG);
            }
        }
        else{
            SnackbarUtils.showSnackbar(
                    rootView,
                    s,
                    "Tolong isi terlebih dahulu semua field yang ada",
                    Snackbar.LENGTH_LONG);
        }
    }

    private void saveToFirebaseDatabase() {
        final String uniqueId = activityId;
        final ActivitySchedule activitySchedule = new ActivitySchedule(
                activity_name_value,
                selectedPlace.getId(),
                uniqueId,
                activity_participant_count_value,
                activity_date_value,
                activity_start_time_value,
                activity_end_time_value
        );
        FirebaseUtils.getDatabaseReference()
                .child(getString(R.string.node_user_activity))
                .child(FirebaseUtils.getCurrentUser().getUid())
                .child(uniqueId)
                .setValue(activitySchedule)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                                AddActivity.this.finish();
                        FirebaseUtils.getDatabaseReference()
                                .child(getString(R.string.node_activity))
                                .child(uniqueId)
                                .setValue(activitySchedule)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        EditActivity.this.finish();
                                    }
                                });
                    }
                });
    }

    public void addPlace() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Mohon maaf", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            // Start a new Activity for the Place Picker API, this will trigger {@code #onActivityResult}
            // when a place is selected or with the user cancels.
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(this);
            startActivityForResult(i, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            if (place == null) {
                Log.i(TAG, "No place selected");
                return;
            }
            activity_place.setText(place.getAddress().toString());
            this.selectedPlace = place;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        activity_date_friendly.setText(day+"/"+month+"/"+year);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        this.activity_date_value = calendar.getTimeInMillis();
    }

    @Override
    public void onResult(@NonNull PlaceBuffer places) {
        if (places.getStatus().isSuccess() && places.getCount() > 0) {
            final Place myPlace = places.get(0);
            activity_place.setText(myPlace.getAddress());
            selectedPlace = myPlace;
            Log.i("ManageActivityFragment", "Place found: " + myPlace.getName());
        } else {
            Log.e("ManageActivityFragment", "Place not found");
        }
        places.release();
    }
}
