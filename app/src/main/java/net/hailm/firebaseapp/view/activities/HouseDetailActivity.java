package net.hailm.firebaseapp.view.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.base.BaseActivity;

public class HouseDetailActivity extends BaseActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_house_detail);
        initToolbar();
        initializeComponents();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Lê Minh Hải");
    }

    private void initializeComponents() {
    }
}
