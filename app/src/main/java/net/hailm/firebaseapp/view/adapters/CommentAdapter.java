package net.hailm.firebaseapp.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.model.dbmodels.CommentModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<CommentModel> commentModelList;

    public CommentAdapter(Context context, List<CommentModel> commentModelList) {
        this.context = context;
        this.commentModelList = commentModelList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_layout_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder, int position) {
        CommentModel commentModel = commentModelList.get(position);

        holder.txtTitle.setText(commentModel.getTitle());
        holder.txtContents.setText(commentModel.getContents());
        holder.txtScore.setText(String.valueOf(commentModel.getScore()));
        setAvatarComment(holder.imgAvatar, commentModel.getUsers().getAvatar());
    }

    @Override
    public int getItemCount() {
        int totalComment = commentModelList.size();
        if (totalComment > 3) {
            return 3;
        } else {
            return totalComment;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TextView txtContents;
        TextView txtScore;
        CircleImageView imgAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title_comment_detail);
            txtContents = itemView.findViewById(R.id.txt_detail_comment_detail);
            txtScore = itemView.findViewById(R.id.txt_score_detail);
            imgAvatar = itemView.findViewById(R.id.img_avatar_comment_detail);
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
        LogUtils.d("Url img comment: " + mStorageAvatar);
        Glide.with(context).using(new FirebaseImageLoader()).load(mStorageAvatar).into(imageView);

    }
}
