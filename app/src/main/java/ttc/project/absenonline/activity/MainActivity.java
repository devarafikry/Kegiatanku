package ttc.project.absenonline.activity;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import ttc.project.absenonline.Geofencing;
import ttc.project.absenonline.R;
import ttc.project.absenonline.fragment.ManageActivityFragment;
import ttc.project.absenonline.fragment.MyScheduleFragment;
import ttc.project.absenonline.model.ActivitySchedule;
import ttc.project.absenonline.model.Schedule;
import ttc.project.absenonline.utils.FirebaseUtils;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ChildEventListener{
    public static final String EXTRA_FROM_PENDING_INTENT = "fromPendingIntent";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.content_view)
    FrameLayout frame_view;
    TextView user_email;

    public static final Long DIFFERENCE_1_DAY_TIMEMILLIS = 86400000l;
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;
    private GoogleApiClient mClient;
    private Geofencing mGeofencing;
    private boolean mIsEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Jadwal Saya");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        View header = navigationView.getHeaderView(0);
        user_email = (TextView) header.findViewById(R.id.user_email);
        user_email.setText(FirebaseUtils.getCurrentUser().getEmail());
        toggle.syncState();
        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        MyScheduleFragment myScheduleFragment = new MyScheduleFragment();
        myScheduleFragment.setGoogleApiClient(mClient);
        ft.replace(R.id.content_view, myScheduleFragment);
        ft.commit();

        navigationView.setCheckedItem(R.id.nav_my_schedule);
        navigationView.setNavigationItemSelectedListener(this);

        mGeofencing = new Geofencing(this, mClient);

        FirebaseUtils.getDatabaseReference()
                .child(getString(R.string.node_user_schedule))
                .child(FirebaseUtils.getCurrentUser().getUid())
                .addChildEventListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.nav_add_schedule){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ManageActivityFragment manageActivityFragment = new ManageActivityFragment();
            manageActivityFragment.setGoogleApiClient(mClient);
            ft.replace(R.id.content_view, manageActivityFragment);
            ft.commit();
            getSupportActionBar().setTitle("Kelola Kegiatan");
        } else if(item.getItemId() == R.id.nav_my_schedule){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            MyScheduleFragment myScheduleFragment = new MyScheduleFragment();
            myScheduleFragment.setGoogleApiClient(mClient);
            ft.replace(R.id.content_view, myScheduleFragment);
            ft.commit();
            getSupportActionBar().setTitle("Jadwal Saya");
        } else if(item.getItemId() == R.id.nav_log_out){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        drawer.closeDrawers();
        return true;
    }



    @Override
    public void onResume() {
        super.onResume();

        // Initialize location permissions checkbox
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {

        }

    }

    // COMPLETED (2) Implement onRingerPermissionsClicked to launch ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS

    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_FINE_LOCATION);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //trying to update geofence from database
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Schedule schedule = dataSnapshot.getValue(Schedule.class);
        FirebaseUtils.getDatabaseReference()
                .child(getString(R.string.node_activity))
                .child(schedule.getActivityId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!= null){
                            ActivitySchedule activity = dataSnapshot.getValue(ActivitySchedule.class);
                            if(Calendar.getInstance().getTimeInMillis()
                                    - activity.getDate() < DIFFERENCE_1_DAY_TIMEMILLIS){
                                mGeofencing.addPlaceToGeofenceList(activity, mClient);
                            }
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

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
