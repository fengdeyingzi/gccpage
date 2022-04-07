package com.xl.gccpage;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.xl.gccpage.adapter.UploadItemAdapter;

import java.io.File;
import java.util.ArrayList;

public class LogUploadActivity extends AppCompatActivity {
    ArrayList<UploadItemAdapter.UploadDataItem> list= null;
    UploadItemAdapter adapter = null;
    View btn_upload;
    RecyclerView list_recy;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(bindLayout());

        initView();
        doBusiness(this);
    }


    public int bindLayout() {
        return R.layout.layout_logupload;
    }


    public void initView() {
//        setSupportActionBar(findViewById(R.id.toolbar));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_upload = findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                uploadFiles();
            }
        });
        list_recy = findViewById(R.id.list_recy);
        list_recy.setLayoutManager(new  LinearLayoutManager(this));
        init();
    }


    public void doBusiness(Context mContext) {
        setTitle("Log日志");
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        getWindow().setAttributes(lp);

    }

    void toast(String text){
        Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void uploadFiles(){
        String reInfo = null;

        ArrayList<File> list_file = new ArrayList<File>();
        ArrayList<UploadItemAdapter.UploadDataItem> list_check = adapter.getCheckedList();
        for(UploadItemAdapter.UploadDataItem item : list_check){
            list_file.add(item.file);
        }

        if(list_file.size() == 0){
            toast("请选择文件");
            return;
        }

        for(File  item : list_file){
            shareFile(this,item.getPath());
        }

    }

    public void shareFile(Context context, String fileName) {
        File file = new File(fileName);
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri  = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        file
                );
                share.putExtra(Intent.EXTRA_STREAM, contentUri);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
            share.setType("application/vnd.ms-excel"); //此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "分享文件"));
        } else {
            Toast.makeText(context, "分享文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    //判断是否要启动log
    public static boolean isEnableLog(Context context){
        ArrayList<File> list_file = new ArrayList<File>();
        File dir = context.getCacheDir();
//        ArrayList<UploadItemAdapter.UploadDataItem> list = new ArrayList();
        File[] files = dir.listFiles();
        for(File file:files){
            if(file.isFile() && file.getName().endsWith(".log")||file.getName().endsWith(".txt")){
                list_file.add(file);
//                list.add(new UploadItemAdapter(context).new UploadDataItem(file, file.getName(), false));
            }
        }
        if(list_file.size() != 0){
           return true;

        }
        return false;
    }

    private void init(){
        list = new ArrayList();
        File dir = getApplicationContext().getCacheDir();
        File[] files = dir.listFiles();
//        Toast.makeText(this,"文件数"+files.length,Toast.LENGTH_SHORT).show();
        ArrayList<File> list_file = new ArrayList<File>();
        for(File file:files){
            Log.i("LogUploadActivity", "init: "+file.getPath());
            if(file.isFile() && file.getName().endsWith(".log") || file.getName().endsWith(".txt")){
                list_file.add(file);
                list.add(new UploadItemAdapter(this).new UploadDataItem(file, file.getName(), false));
            }
        }

        adapter = new UploadItemAdapter(this);
        adapter.updateData(list);
        list_recy.setAdapter(adapter);
        if(list_file.size() == 0){
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adapter.getItemCount()>0){
            ArrayList<UploadItemAdapter.UploadDataItem> list_item = adapter.getList();
            for(UploadItemAdapter.UploadDataItem item:list_item){
                if(item.file.isFile()){
                    try{
                        item.file.delete();
                    }catch (SecurityException e){
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}