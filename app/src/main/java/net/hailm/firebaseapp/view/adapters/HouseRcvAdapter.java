package net.hailm.firebaseapp.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.HouseRcvAdapterCallback;
import net.hailm.firebaseapp.model.dbmodels.CommentModel;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.utils.DateUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by hai.lm on 14/04/2018.
 */

public class HouseRcvAdapter extends RecyclerView.Adapter<HouseRcvAdapter.ViewHolder> {
    private List<HouseModel> houseModelList;
    private Context context;
    private LayoutInflater inflater;
    private StorageReference mStorageImage;
    private HouseRcvAdapterCallback mCallback;

    public HouseRcvAdapter(List<HouseModel> houseModelList, Context context, HouseRcvAdapterCallback callback) {
        this.houseModelList = houseModelList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mCallback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_house)
        LinearLayout llHouse;
        @BindView(R.id.txt_landlord)
        TextView txtLandlord;
        @BindView(R.id.txt_update_date)
        TextView txtUpdateDate;
        @BindView(R.id.txt_address)
        TextView txtAddress;
        @BindView(R.id.txt_distance)
        TextView txtDistance;
        @BindView(R.id.txt_price)
        TextView txtPrice;
        @BindView(R.id.txt_acreage)
        TextView txtAcreage;
        @BindView(R.id.btn_contact)
        Button btnContact;
        @BindView(R.id.img_image)
        ImageView imgHouseImage;
        @BindView(R.id.ll_comment1)
        LinearLayout llComment1;
        @BindView(R.id.ll_comment2)
        LinearLayout llComment2;
        @BindView(R.id.txt_score)
        TextView txtScore;
        @BindView(R.id.txt_score2)
        TextView txtScore2;
        @BindView(R.id.txt_score_medium)
        TextView txtScoreMedium;
        @BindView(R.id.txt_total_comment)
        TextView txtTotalComment;
        @BindView(R.id.txt_total_images)
        TextView getTxtTotalImages;
        @BindView(R.id.img_avatar_comment)
        CircleImageView imgAvatar;
        @BindView(R.id.txt_title_comment)
        TextView txtTitleComment;
        @BindView(R.id.txt_detail_comment)
        TextView txtDetailComment;
        @BindView(R.id.img_avatar_comment2)
        CircleImageView imgAvatar2;
        @BindView(R.id.txt_title_comment2)
        TextView txtTitleComment2;
        @BindView(R.id.txt_detail_comment2)
        TextView txtDetailComment2;

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

        final HouseModel houseModel = houseModelList.get(position);
//        if (houseModel.getAddressModel().getDistance() < 1) {
        showData(holder, position);
//        }

        holder.llHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
                mCallback.onItemCLick(houseModel);
            }
        });

        holder.btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onBtnClick(houseModel.getTel());
            }
        });
    }

    private void showData(final ViewHolder holder, int position) {
        LogUtils.d("List: " + houseModelList.size());

        HouseModel houseModel = houseModelList.get(position);

        String landlord = houseModel.getLandlord();
        holder.txtLandlord.setText(landlord);

        Date date = getDate(houseModel);
        String updateDate = context.getString(R.string.ngay) + " " + DateUtils.getDay(date) + " "
                + context.getString(R.string.thang) + " " + DateUtils.getMonth(date) + " "
                + context.getString(R.string.nam) + " " + DateUtils.getYear(date) + " "
                + context.getString(R.string.luc) + " " + DateUtils.getTime(date);
        holder.txtUpdateDate.setText(updateDate);

        NumberFormat formatPrice = new DecimalFormat("$##,###,###");
        String price = context.getResources().getString(R.string.gia_phong) + " " + formatPrice.format(houseModel.getPrice()) + " " + context.getString(R.string.dong);
        holder.txtPrice.setText(price);

        String acreage = context.getResources().getString(R.string.dien_tich) + " " + String.valueOf(houseModel.getAcreage()) + " " + context.getString(R.string.m2);
        holder.txtAcreage.setText(acreage);
//        String tel = context.getResources().getString(R.string.so_dien_thoai) + houseModel.getTel();
//        holder.txtTel.setText(tel);

        if (houseModel.getQuantity() > 0) {
            holder.btnContact.setVisibility(View.VISIBLE);
        }

        if (houseModel.getHouseImages().size() > 0) {
            mStorageImage = FirebaseStorage.getInstance().getReference()
                    .child(Constants.IMAGES)
                    .child(houseModel.getHouseImages().get(0));
            Glide.with(context).using(new FirebaseImageLoader()).load(mStorageImage).into(holder.imgHouseImage);
        }

//        if (houseModel.getBitmapList().size() > 0) {
//            holder.imgHouseImage.setImageBitmap(houseModel.getBitmapList().get(0));
//        }


        // Fill data comment house
        if (houseModel.getCommentModelList().size() > 0) {
            CommentModel commentModel = houseModel.getCommentModelList().get(0);
            holder.txtTitleComment.setText(commentModel.getTitle());
            holder.txtScore.setText(String.valueOf(commentModel.getScore()));
            holder.txtDetailComment.setText(commentModel.getContents());
            setAvatarComment(holder.imgAvatar, commentModel.getUsers().getAvatar());
            if (houseModel.getCommentModelList().size() >= 2) {
                CommentModel commentMode2 = houseModel.getCommentModelList().get(1);
                holder.txtTitleComment2.setText(commentMode2.getTitle());
                holder.txtDetailComment2.setText(commentMode2.getContents());
                holder.txtScore2.setText(String.valueOf(commentMode2.getScore()));
                setAvatarComment(holder.imgAvatar2, commentMode2.getUsers().getAvatar());
            }
            holder.txtTotalComment.setText(String.valueOf(houseModel.getCommentModelList().size()));

            int totalImageComment = 0;
            double totalScore = 0;
            double totalScoreMedium = 0;
            for (CommentModel values : houseModel.getCommentModelList()) {
                totalImageComment += values.getListCommentImages().size();
                totalScore += values.getScore();
            }

            totalScoreMedium = totalScore / (houseModel.getCommentModelList().size());
            holder.txtScoreMedium.setText(String.format("%.1f", totalScoreMedium));

            if (totalImageComment > 0) {
                holder.getTxtTotalImages.setText(String.valueOf(totalImageComment));
            } else {
                holder.getTxtTotalImages.setText("0");
            }
        } else {
            holder.llComment1.setVisibility(View.GONE);
            holder.llComment2.setVisibility(View.GONE);
            holder.txtTotalComment.setText("0");
            holder.getTxtTotalImages.setText("0");
        }

        String address = context.getString(R.string.dia_chi) + " " + houseModel.getAddressModel().getAddress();
        holder.txtAddress.setText(address);
        String distance = String.valueOf(String.format("%.2f", houseModel.getAddressModel().getDistance())) + context.getString(R.string.km);
        holder.txtDistance.setText(distance);
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

    /**
     * set avatar comment
     *
     * @param imageView
     * @param url
     */
    private void setAvatarComment(final CircleImageView imageView, String url) {
        StorageReference mStorageAvatar = FirebaseStorage.getInstance().getReference()
                .child(Constants.MEMBERS)
                .child(url);

        LogUtils.d("Url: " + mStorageAvatar);
        Glide.with(context).using(new FirebaseImageLoader()).load(mStorageAvatar).into(imageView);

    }

    @Override
    public int getItemCount() {
        return houseModelList.size();
    }

}
