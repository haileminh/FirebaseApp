package net.hailm.firebaseapp.model.dbmodels;

import java.util.List;

/**
 * Created by hai.lm on 15/04/2018.
 */

public class CommentModel {
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
}
