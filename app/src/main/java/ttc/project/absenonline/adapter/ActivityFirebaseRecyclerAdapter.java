package ttc.project.absenonline.adapter;

import android.arch.lifecycle.LifecycleOwner;
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
import com.firebase.ui.database.ObservableSnapshotArray;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Query;

import ttc.project.absenonline.R;
import ttc.project.absenonline.activity.DetailParticipantActivity;
import ttc.project.absenonline.activity.EditActivity;
import ttc.project.absenonline.model.ActivitySchedule;
import ttc.project.absenonline.utils.DateUtils;
import ttc.project.absenonline.utils.FirebaseUtils;
import ttc.project.absenonline.utils.SnackbarUtils;
import ttc.project.absenonline.viewholder.ActivityViewHolder;

/**
 * Created by Fikry-PC on 12/8/2017.
 */

public class ActivityFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<ActivitySchedule, ActivityViewHolder> {

    Context mContext;
    Snackbar s;
    View rootView;
    GoogleApiClient mClient;
    public ActivityFirebaseRecyclerAdapter(
            Context context,
            GoogleApiClient client,
            Snackbar snackbar,
            View rootView,
            Class<ActivitySchedule> modelClass,
            int modelLayout,
            Class<ActivityViewHolder> viewHolderClass,
            Query query) {
        super(modelClass, modelLayout, viewHolderClass, query);
        this.mContext = context;
        this.mClient = client;
        this.s = snackbar;
        this.rootView = rootView;
    }

    @Override
    protected void populateViewHolder(final ActivityViewHolder viewHolder, final ActivitySchedule activity, int position) {
        viewHolder.tv_name.setText(activity.getName());
        viewHolder.btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("KegiatanId", activity.getKey());
                clipboard.setPrimaryClip(clip);

                SnackbarUtils.showSnackbar(
                        rootView,
                        s,
                        "Id kegiatan telah di copy, silahkan berikan id kepada calon peserta",
                        Snackbar.LENGTH_LONG
                );
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailParticipantActivity.class);
                intent.putExtra(DetailParticipantActivity.EXTRA_ACTIVITY_KEY, activity.getKey());
                intent.putExtra(DetailParticipantActivity.EXTRA_TITLE_KEY, activity.getName());
                mContext.startActivity(intent);
            }
        });
        viewHolder.tv_date.setText(DateUtils.getFriendlyDate(
                mContext, activity.getDate()
        ));
        viewHolder.tv_time.setText(DateUtils.getFriendlyTimeStartAndEnd(
                activity.getTime_start(),
                activity.getTime_end()
        ));
        viewHolder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditActivity.class);
                intent.putExtra(EditActivity.EXTRA_ACTIVITY_ID, activity.getKey());
                mContext.startActivity(intent);
            }
        });
        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Hapus kegiatan "+activity.getName()+" ?");
                builder.setMessage("Semua data termasuk peserta kegiatan akan dihapus");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        FirebaseUtils.getDatabaseReference()
                                .child(mContext.getString(R.string.node_user_activity))
                                .child(FirebaseUtils.getCurrentUser().getUid())
                                .child(activity.getKey())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseUtils.getDatabaseReference()
                                                .child(mContext.getString(R.string.node_activity))
                                                .child(activity.getKey())
                                                .removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FirebaseUtils.getDatabaseReference()
                                                                .child(mContext.getString(R.string.node_schedule))
                                                                .child(activity.getKey())
                                                                .removeValue()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        SnackbarUtils.showSnackbar(
                                                                                rootView,
                                                                                s,
                                                                                "Kegiatan berhasil dihapus",
                                                                                Snackbar.LENGTH_LONG
                                                                        );
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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
    }
}
