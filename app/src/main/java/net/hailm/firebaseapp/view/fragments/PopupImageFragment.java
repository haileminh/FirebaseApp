package net.hailm.firebaseapp.view.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.listener.PopupImageCallback;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class PopupImageFragment extends DialogFragment implements View.OnClickListener {
    private static final int REQUEST_CODE_CAMERA_APP = 1000;
    private static final int REQUEST_CODE_IMAGES = 1002;
    private static final int REQUEST_CODE_PERMISSION_CAMERA_APP = 1003;
    private TextView txtChupAnh;
    private TextView txtChonTuAlbum;
    private PopupImageCallback popupImageCallback;

    private Uri imageUri;

    public PopupImageFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_popup_get_image, container, false);

        popupImageCallback = (PopupImageCallback) getParentFragment();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        txtChupAnh = rootView.findViewById(R.id.txt_chup_anh);
        txtChonTuAlbum = rootView.findViewById(R.id.txt_chon_tu_album);
        txtChupAnh.setOnClickListener(this);
        txtChonTuAlbum.setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_chup_anh:
                Toast.makeText(getActivity(), "Chụp ảnh", Toast.LENGTH_SHORT).show();
                requesPermisions();

                break;
            case R.id.txt_chon_tu_album:
                Toast.makeText(getActivity(), "Chọn ảnh từ album", Toast.LENGTH_SHORT).show();
                selectImage();
                break;
            default:
                break;
        }
    }

    private void requesPermisions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (isPermissionGranted(Manifest.permission.CAMERA)) {
                showCamera();
            } else {
                String[] permissions = new String[]{
                        Manifest.permission.CAMERA
                };
                ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CODE_PERMISSION_CAMERA_APP);
            }
        } else {
            showCamera();
        }
    }

    private Boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void selectImage() {
        Intent imageIntent = new Intent(Intent.ACTION_PICK);
        imageIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(imageIntent, "Select Picture"), REQUEST_CODE_IMAGES);
    }

    private void showCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, REQUEST_CODE_CAMERA_APP);

//        Intent cameraAppIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraAppIntent, REQUEST_CODE_CAMERA_APP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_APP:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getActivity().getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getActivity().getContentResolver();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        popupImageCallback.onButtonClick(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_CODE_IMAGES:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.getData() != null) {

//                        Bitmap image = (Bitmap) data.getExtras().get("data"); // mac dinh
//                        btnImage.setImageBitmap(image);

                        try {
                            Uri uri = data.getData();
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                            popupImageCallback.onButtonClick(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CAMERA_APP:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    showCamera();
                } else {
                    Toast.makeText(getActivity(), "Bạn cần cấp quyền để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
