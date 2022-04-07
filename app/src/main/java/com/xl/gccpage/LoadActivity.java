package com.xl.gccpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class LoadActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//                Intent intent = new Intent(this,MainActivity.class);
//       startActivity(intent);
                Intent intent2 = new Intent(this,LogUploadActivity.class);
        startActivity(intent2);
        finish();
    }
}
