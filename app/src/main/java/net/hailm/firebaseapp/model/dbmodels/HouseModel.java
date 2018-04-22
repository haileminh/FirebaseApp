package net.hailm.firebaseapp.model.dbmodels;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hai.lm on 13/04/2018.
 */

public class HouseModel implements Serializable {
    private String landlord;
    private String tel;
    private String houseId;
    private long acreage;
    private long price;
    private long quantity;
    private String videoIntro;
    private String contents;
    private long likeNumber;
    private List<String> utility;
    private List<String> houseImages;
    private List<CommentModel> commentModelList;
    private AddressModel addressModel;
    private List<Bitmap> bitmapList;

    public HouseModel() {
    }

    public HouseModel(String contents, long likeNumber) {
        this.contents = contents;
        this.likeNumber = likeNumber;
    }

    public List<Bitmap> getBitmapList() {
        return bitmapList;
    }

    public void setBitmapList(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    public AddressModel getAddressModel() {
        return addressModel;
    }

    public void setAddressModel(AddressModel addressModel) {
        this.addressModel = addressModel;
    }

    public List<CommentModel> getCommentModelList() {
        return commentModelList;
    }

    public void setCommentModelList(List<CommentModel> commentModelList) {
        this.commentModelList = commentModelList;
    }

    public List<String> getHouseImages() {
        return houseImages;
    }

    public void setHouseImages(List<String> houseImages) {
        this.houseImages = houseImages;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getLandlord() {
        return landlord;
    }

    public void setLandlord(String landlord) {
        this.landlord = landlord;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public long getAcreage() {
        return acreage;
    }

    public void setAcreage(long acreage) {
        this.acreage = acreage;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getVideoIntro() {
        return videoIntro;
    }

    public void setVideoIntro(String videoIntro) {
        this.videoIntro = videoIntro;
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

    public List<String> getUtility() {
        return utility;
    }

    public void setUtility(List<String> utility) {
        this.utility = utility;
    }

}
