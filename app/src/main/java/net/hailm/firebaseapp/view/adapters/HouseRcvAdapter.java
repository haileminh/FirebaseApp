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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.model.dbmodels.CommentModel;
import net.hailm.firebaseapp.model.dbmodels.HouseBranchModel;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;

import java.util.Collections;
import java.util.Comparator;
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
        @BindView(R.id.txt_distance)
        TextView txtDistance;
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

        // get houseBranchs, show data address, distance
//        List<HouseBranchModel> houseBranchModelList = houseModel.getHouseBranchModelList();
//        class sortHouseBranch implements Comparator<HouseBranchModel> {
//
//            @Override
//            public int compare(HouseBranchModel o1, HouseBranchModel o2) {
//                return Double.compare(o1.getDistance(), o2.getDistance());
//            }
//        }
//        Collections.sort(houseBranchModelList, new sortHouseBranch());

        if (houseModel.getHouseBranchModelList().size() > 0) {
            HouseBranchModel houseBranchModelTemp = houseModel.getHouseBranchModelList().get(0);
            for (HouseBranchModel houseBranchModel : houseModel.getHouseBranchModelList()) {
                if (houseBranchModelTemp.getDistance() > houseBranchModel.getDistance()) {
                    houseBranchModelTemp = houseBranchModel;
                }
            }

            holder.txtAddress.setText(houseBranchModelTemp.getAddress());
            String distance = String.valueOf(String.format("%.2f", houseBranchModelTemp.getDistance())) + context.getString(R.string.km);
            holder.txtDistance.setText(distance);
        }
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
        final long ONE_MEGABYTE = 1024 * 1024;
        mStorageAvatar.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return houseModelList.size();
    }

}
