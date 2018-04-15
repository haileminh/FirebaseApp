package net.hailm.firebaseapp.view.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hai.lm on 14/04/2018.
 */

public class HouseRcvAdapter extends RecyclerView.Adapter<HouseRcvAdapter.ViewHolder> {
    private List<HouseModel> houseModelList;
    private Context context;
    private LayoutInflater inflater;
    private StorageReference mStorageImage;

    public HouseRcvAdapter(List<HouseModel> houseModelList, Context context) {
        this.houseModelList = houseModelList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_landlord)
        TextView txtLandlord;
        @BindView(R.id.txt_address)
        TextView txtAddress;
        @BindView(R.id.txt_price)
        TextView txtPrice;
        @BindView(R.id.txt_acreage)
        TextView txtAcreage;
        @BindView(R.id.txt_tel)
        TextView txtTel;
        @BindView(R.id.btn_contact)
        Button btnContact;
        @BindView(R.id.img_image)
        ImageView imgHouseImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public HouseRcvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_house, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final HouseRcvAdapter.ViewHolder holder, int position) {
        HouseModel houseModel = houseModelList.get(position);

        String landlord = houseModel.getLandlord();
        holder.txtLandlord.setText(landlord);
        String price = context.getResources().getString(R.string.gia_phong) + String.valueOf(houseModel.getPrice());
        holder.txtPrice.setText(price);
        String acreage = context.getResources().getString(R.string.dien_tich) + String.valueOf(houseModel.getAcreage());
        holder.txtAcreage.setText(acreage);
        String tel = context.getResources().getString(R.string.so_dien_thoai) + houseModel.getTel();
        holder.txtTel.setText(tel);

        if (houseModel.getQuantity() > 0) {
            holder.btnContact.setVisibility(View.VISIBLE);
        }

        if (houseModel.getHouseImages().size() > 0) {
            mStorageImage = FirebaseStorage.getInstance().getReference()
                    .child(Constants.IMAGES)
                    .child(houseModel.getHouseImages().get(0));
            final long ONE_MEGABYTE = 1024 * 1024;
            mStorageImage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.imgHouseImage.setImageBitmap(bitmap);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return houseModelList.size();
    }

}
