package net.hailm.firebaseapp.model.dbmodels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by hai.lm on 15/04/2018.
 */

public class CommentModel implements Parcelable {
    private String commentId;
    private String title;
    private String contents;
    private long likeNumber;
    private double score;
    private String uid;
    private Users users;
    private List<String> listCommentImages;

    public CommentModel() {
    }

    protected CommentModel(Parcel in) {
        commentId = in.readString();
        title = in.readString();
        contents = in.readString();
        likeNumber = in.readLong();
        score = in.readDouble();
        uid = in.readString();
        listCommentImages = in.createStringArrayList();
        users = in.readParcelable(Users.class.getClassLoader());
    }

    public static final Creator<CommentModel> CREATOR = new Creator<CommentModel>() {
        @Override
        public CommentModel createFromParcel(Parcel in) {
            return new CommentModel(in);
        }

        @Override
        public CommentModel[] newArray(int size) {
            return new CommentModel[size];
        }
    };

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUid() {
        return uid;
    }

    public List<String> getListCommentImages() {
        return listCommentImages;
    }

    public void setListCommentImages(List<String> listCommentImages) {
        this.listCommentImages = listCommentImages;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public long getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(long likeNumber) {
        this.likeNumber = likeNumber;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(commentId);
        dest.writeString(title);
        dest.writeString(contents);
        dest.writeLong(likeNumber);
        dest.writeDouble(score);
        dest.writeString(uid);
        dest.writeStringList(listCommentImages);
        dest.writeParcelable(users,flags);
    }
}
