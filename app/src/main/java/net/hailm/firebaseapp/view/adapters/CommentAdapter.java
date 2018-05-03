package net.hailm.firebaseapp.view.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.CommentApdaterCallback;
import net.hailm.firebaseapp.model.dbmodels.CommentModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<CommentModel> commentModelList;
    private CommentApdaterCallback callback;

    public CommentAdapter(Context context, List<CommentModel> commentModelList, CommentApdaterCallback callback) {
        this.context = context;
        this.commentModelList = commentModelList;
        inflater = LayoutInflater.from(context);
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_layout_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder, int position) {
        final CommentModel commentModel = commentModelList.get(position);

//        if (commentModel.getUsers().getName() != null) {
//            holder.txtTitle.setText(commentModel.getUsers().getName());
//        } else {
//            holder.txtTitle.setText(commentModel.getTitle());
//        }
//        holder.txtContents.setText(commentModel.getContents());
//        holder.txtScore.setText(String.valueOf(commentModel.getScore()));
//        setAvatarComment(holder.imgAvatar, commentModel.getUsers().getAvatar());

        String url = "https://pikmail.herokuapp.com/" + commentModel.getEmail();

        if (commentModel.getUsers() != null) {
            holder.txtTitle.setText(commentModel.getUsers().getName());
            holder.txtContents.setText(commentModel.getContents());
            holder.txtScore.setText(String.valueOf(commentModel.getScore()));
            setAvatarComment(holder.imgAvatar, commentModel.getUsers().getAvatar());
        } else {
            holder.txtTitle.setText(commentModel.getTitle());
            holder.txtContents.setText(commentModel.getContents());
            holder.txtScore.setText(String.valueOf(commentModel.getScore()));
            Glide.with(context).load(url).into(holder.imgAvatar);
        }

        holder.llComment.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                callback.onLongItemClick(commentModel);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        int totalComment = commentModelList.size();
        if (totalComment > 10) {
            return 10;
        } else {
            return totalComment;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TextView txtContents;
        TextView txtScore;
        CircleImageView imgAvatar;
        LinearLayout llComment;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title_comment_detail);
            txtContents = itemView.findViewById(R.id.txt_detail_comment_detail);
            txtScore = itemView.findViewById(R.id.txt_score_detail);
            imgAvatar = itemView.findViewById(R.id.img_avatar_comment_detail);
            llComment = itemView.findViewById(R.id.ll_comment);
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
