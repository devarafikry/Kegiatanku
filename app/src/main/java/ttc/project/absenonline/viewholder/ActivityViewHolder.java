package ttc.project.absenonline.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ttc.project.absenonline.R;

/**
 * Created by Fikry-PC on 12/5/2017.
 */

public class ActivityViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_name, tv_time, tv_place,tv_date;
    public ImageView btn_delete, btn_edit, btn_copy;
    public ActivityViewHolder(View itemView) {
        super(itemView);
        tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        tv_place = (TextView) itemView.findViewById(R.id.tv_place);
        tv_date = (TextView) itemView.findViewById(R.id.tv_date);

        btn_delete = (ImageView) itemView.findViewById(R.id.btn_delete);
        btn_edit = (ImageView) itemView.findViewById(R.id.btn_edit);
        btn_copy = (ImageView) itemView.findViewById(R.id.btn_copy);
    }
}
