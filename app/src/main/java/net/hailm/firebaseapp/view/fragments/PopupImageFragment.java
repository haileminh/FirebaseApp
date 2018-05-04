package net.hailm.firebaseapp.view.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.listener.PopupImageCallback;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class PopupImageFragment extends DialogFragment implements View.OnClickListener {
    private static final int REQUEST_CODE_PERMISSION_CAMERA = 1000;
    private static final int REQUEST_CODE_CAMERA = 1001;
    private static final int REQUEST_CODE_IMAGES = 1002;
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

        txtChupAnh = rootView.findViewById(R.id.txt_chup_anh);
        txtChonTuAlbum = rootView.findViewById(R.id.txt_chon_tu_album);
        txtChupAnh.setOnClickListener(this);
        txtChonTuAlbum.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popupImageCallback = (PopupImageCallback) getTargetFragment();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionGranted(Manifest.permission.CAMERA)) {
                openCamera();
            } else {
                String[] permissions = new String[]{
                        Manifest.permission.CAMERA
                };
                ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CODE_PERMISSION_CAMERA);
            }
        } else {
            openCamera();
        }
    }

    private boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void openCamera() {
        Intent iOpenCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(iOpenCamera, REQUEST_CODE_CAMERA);
    }

    private void selectImage() {
        Intent imageIntent = new Intent(Intent.ACTION_PICK);
        imageIntent.setType("image/*");
        startActivityForResult(imageIntent, REQUEST_CODE_IMAGES);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.chua_cap_quyen), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK && data != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    popupImageCallback.onButtonClick(bitmap);
                }
                break;
            case REQUEST_CODE_IMAGES:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    try {
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        popupImageCallback.onButtonClick(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}
