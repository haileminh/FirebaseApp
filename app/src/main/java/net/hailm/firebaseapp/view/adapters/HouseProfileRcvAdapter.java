package net.hailm.firebaseapp.view.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.utils.DateUtils;
import net.hailm.firebaseapp.utils.DialogUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class HouseProfileRcvAdapter extends RecyclerView.Adapter<HouseProfileRcvAdapter.ViewHolder> {
    private List<HouseModel> houseModelList;
    private Context context;
    private LayoutInflater inflater;
    private StorageReference mStorageImage;

    public HouseProfileRcvAdapter(List<HouseModel> houseModelList, Context context) {
        this.houseModelList = houseModelList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_house_by_id)
        ImageView imgHouseById;
        @BindView(R.id.txt_address)
        TextView txtAddress;
        @BindView(R.id.txt_price)
        TextView txtPrices;
        @BindView(R.id.txt_acreage)
        TextView txtAcrage;
        @BindView(R.id.txt_update_date)
        TextView txtUpdatedate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_house_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        HouseModel houseModel = houseModelList.get(position);

        if (houseModel.getHouseImages().size() > 0) {
            mStorageImage = FirebaseStorage.getInstance().getReference()
                    .child(Constants.IMAGES)
                    .child(houseModel.getHouseImages().get(0));
            Glide.with(context).using(new FirebaseImageLoader()).load(mStorageImage).into(holder.imgHouseById);
        }

        holder.txtAddress.setText(houseModel.getAddressModel().getAddress());

        NumberFormat formatPrice = new DecimalFormat("$##,###,###");
        String price = context.getResources().getString(R.string.gia_phong) + " " + formatPrice.format(houseModel.getPrice()) + " " + context.getString(R.string.dong);
        holder.txtPrices.setText(price);

        String acreage = context.getResources().getString(R.string.dien_tich) + " " + String.valueOf(houseModel.getAcreage()) + " " + context.getString(R.string.m2);
        holder.txtAcrage.setText(acreage);

        Date date = getDate(houseModel);
        String updateDate = context.getString(R.string.ngay) + " " + DateUtils.getDay(date) + " "
                + context.getString(R.string.thang) + " " + DateUtils.getMonth(date) + " "
                + context.getString(R.string.nam) + " " + DateUtils.getYear(date) + " "
                + context.getString(R.string.luc) + " " + DateUtils.getTime(date);
        holder.txtUpdatedate.setText(updateDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Sửa", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogUtils.showAlertDialog(context, context.getString(R.string.ban_co_muon_xoa_nha_tro), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case BUTTON_POSITIVE:
                                dialog.dismiss();
                                break;
                            case BUTTON_NEGATIVE:
                                Toast.makeText(context, "Xóa chứ sao ko", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                });
                return true;
            }
        });
    }

    /**
     * getDateTime
     *
     * @param houseModel
     * @return
     */
    private Date getDate(HouseModel houseModel) {
        String updateDate = houseModel.getUpdateDate();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm 'at' dd.MM.yyyy");
        try {
            Date date = format.parse(updateDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return houseModelList.size();
    }
}
