package net.hailm.firebaseapp.view.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;


public class PhotoVpgAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;
    private HouseModel houseModel;

    public PhotoVpgAdapter(Context context, HouseModel houseModel) {
        this.context = context;
        this.houseModel = houseModel;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return houseModel.getBitmapList().size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        // Kiểm tra obj có phải view hay ko
        // Nếu là view thì ms đưa lên ViewPager
        return object != null && object instanceof View && object == view;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.item_photo, container, false);

        // Anh xa
        ImageView imgPhoto = view.findViewById(R.id.img_photo);

        // Fill data
        imgPhoto.setImageBitmap(houseModel.getBitmapList().get(position));

        // Dinh view len Viewpager
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}


