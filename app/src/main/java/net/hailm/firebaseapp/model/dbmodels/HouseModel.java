package net.hailm.firebaseapp.model.dbmodels;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hai.lm on 13/04/2018.
 */

public class HouseModel implements Parcelable {
    private String landlord;
    private String tel;
    private String houseId;
    private String uid;
    private String updateDate;
    private long acreage;
    private long price;
    private long quantity;
    private String videoIntro;
    private String contents;
    private long likeNumber;
    private List<String> totalLikeNumber;
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

    protected HouseModel(Parcel in) {
        landlord = in.readString();
        tel = in.readString();
        houseId = in.readString();
        uid = in.readString();
        updateDate = in.readString();
        acreage = in.readLong();
        price = in.readLong();
        quantity = in.readLong();
        videoIntro = in.readString();
        contents = in.readString();
        likeNumber = in.readLong();
        totalLikeNumber = in.createStringArrayList();
        utility = in.createStringArrayList();
        houseImages = in.createStringArrayList();
        commentModelList = new ArrayList<>();
        in.readTypedList(commentModelList, CommentModel.CREATOR);
    }

    public static final Creator<HouseModel> CREATOR = new Creator<HouseModel>() {
        @Override
        public HouseModel createFromParcel(Parcel in) {
            return new HouseModel(in);
        }

        @Override
        public HouseModel[] newArray(int size) {
            return new HouseModel[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
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

    public List<String> getTotalLikeNumber() {
        return totalLikeNumber;
    }

    public void setTotalLikeNumber(List<String> totalLikeNumber) {
        this.totalLikeNumber = totalLikeNumber;
    }

    public List<String> getUtility() {
        return utility;
    }

    public void setUtility(List<String> utility) {
        this.utility = utility;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(landlord);
        dest.writeString(tel);
        dest.writeString(houseId);
        dest.writeString(uid);
        dest.writeString(updateDate);
        dest.writeLong(acreage);
        dest.writeLong(price);
        dest.writeLong(quantity);
        dest.writeString(videoIntro);
        dest.writeString(contents);
        dest.writeLong(likeNumber);
        dest.writeStringList(totalLikeNumber);
        dest.writeStringList(utility);
        dest.writeStringList(houseImages);
        dest.writeTypedList(commentModelList);
    }
}
