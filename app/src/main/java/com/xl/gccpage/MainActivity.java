package com.xl.gccpage;

import android.Manifest;
import android.app.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.view.View.*;
import android.content.res.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.*;
import android.net.*;

import com.xl.game.tool.FileProvider;

public class MainActivity extends Activity implements OnClickListener, GetInfoListener, Runnable {
    private static final String TAG = "MainActivity";
    public static final int DLG_CPU_ERROR = 301;
    private static int REQUEST_PERMISSION = 1020;
    private String error;

    @Override
    public void run() {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("gcc.zip已解压到" + folder + "目录下，打开手机CAPP即可安装。如果没有弹出安装提示,请升级手机CAPP")
                .setPositiveButton("立即安装", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                shareFileFromCAPP(MainActivity.this,folder+File.separator+"gcc.zip");
                            }
                        })
                .create();
        dialog.show();
        if(error!=null){
            Toast.makeText(this,""+error, Toast.LENGTH_LONG).show();
        }
    }

    /*
     * @Author Sun Ruichuan
     *
     * */

    public ArrayList<HashMap<String, Object>> getItems(Context context) {

        PackageManager pckMan = context.getPackageManager();
        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

        List<PackageInfo> packageInfo = pckMan.getInstalledPackages(0);

        for (PackageInfo pInfo : packageInfo) {

            HashMap<String, Object> item = new HashMap<String, Object>();

            item.put("appimage", pInfo.applicationInfo.loadIcon(pckMan));
            item.put("packageName", pInfo.packageName);
            item.put("versionCode", pInfo.versionCode);
            item.put("versionName", pInfo.versionName);
            item.put("appName", pInfo.applicationInfo.loadLabel(pckMan).toString());

            items.add(item);

        }

        return items;
    }

    //获取指定包名的应用版本号
    public long getVersionCode(String packagename){
        PackageManager pckMan = getPackageManager();

        List<PackageInfo> packageInfo = pckMan.getInstalledPackages(0);

        for (PackageInfo pInfo : packageInfo) {

            HashMap<String, Object> item = new HashMap<String, Object>();

            item.put("appimage", pInfo.applicationInfo.loadIcon(pckMan));
            item.put("packageName", pInfo.packageName);
            item.put("versionCode", pInfo.versionCode);
            item.put("versionName", pInfo.versionName);
            item.put("appName", pInfo.applicationInfo.loadLabel(pckMan).toString());
            if(packagename.equals(pInfo.packageName)){
                return pInfo.versionCode;
            }
        }

        return -1;
    }


    @Override
    public void onGetText(String text) {

    }

    Handler handler;

    @Override
    public void onClick(final View p1) {
        switch (p1.getId()) {
            case R.id.btn_install:
                dialog = ProgressDialog.show(p1.getContext(), "", "请稍候...");
                String dir = edit_dir.getText().toString();

                folder = getSDPath() + File.separator + dir;
                new Thread() {
                    public void run() {
                        unZipAssets(gcc_zip_name, folder);
                        handler.post(MainActivity.this);
                    }
                }.start();
break;
            case R.id.btn_uninstall:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("application/zip");
                share.putExtra("action","ungcc");
                share.setPackage("com.xl.capp");
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try{
                    startActivity(share);
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }


        }
    }

    Button btn_slzw_install;
    Button btn_uninstall;
    EditText edit_dir;
    //对话框 请稍候
    Dialog dialog;
    //解压到的文件名
    String folder;
    String gcc_zip_name;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btn_slzw_install = (Button) findViewById(R.id.btn_install);
        edit_dir = (EditText) findViewById(R.id.edit_dir);
        btn_uninstall = (Button)findViewById(R.id.btn_uninstall);

        btn_slzw_install.setOnClickListener(this);
        btn_uninstall.setOnClickListener(this);
        handler = new Handler();
        String cpu = Build.CPU_ABI;
        if (cpu != null)
            if (cpu.indexOf("arm") < 0 && cpu.indexOf("x86")<0) {
                showDialog(DLG_CPU_ERROR);

            }
        if(cpu.indexOf("arm64")>=0 && getVersionCode("com.xl.capp")>=42){
            gcc_zip_name = "gcc_aarch64.zip";
        }
        else if(cpu.indexOf("arm")>=0){
            gcc_zip_name = "gcc.zip";
        }
        else if(cpu.indexOf("x86_64")>=0){
            gcc_zip_name = "gcc_i686.zip";
            showDialog(DLG_CPU_ERROR);
        }
        else if(cpu.indexOf("x86")>=0){
            gcc_zip_name = "gcc_i686.zip";
        }
        requestPermission();
        Log.i(TAG, "onCreate: 手机C版本"+getVersionCode("com.xl.capp"));
    }

    @Override
    protected void onResume() {

        super.onResume();
		/*
		String url = getTextFromAssets(this,"url");
		if(url!=null)
			if(url.startsWith("http:") || url.startsWith("https:"))
			{
				new GetInfo(url,this).start();
				//Toast.makeText(this,url,0).show();
			}
			*/

    }


    public static String getTextFromAssets(Context context, String assetspath) {
        String r0_String;
        String r1_String = "";
        AssetManager assets = context.getResources().getAssets();
        try {
            InputStream input = assets.open(assetspath);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            r0_String = new String(buffer, "UTF-8");
            input.close();
            return r0_String;
        } catch (IOException r0_IOException) {
            r0_String = r1_String;
        }


        return r0_String;

    }


    //获取sd卡
    public String getSDPath() {
        if(Build.VERSION.SDK_INT <= 28 || getVersionCode("com.xl.capp")<42){
            File sdDir = null;
            boolean sdCardExist =
                    Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory();//获取sd卡目录
            } else {
                return getCacheDir().getAbsolutePath();
            }
            return sdDir.getPath();
        }
        else {
            return getCacheDir().getAbsolutePath();
        }
    }

    //解压apk并安装
    public void unZipAssets(String assersFileName, String folder) {
        AssetManager assets = getAssets();
        try {
            //获取assets资源目录下的himarket.mp3,实际上是himarket.apk,为了避免被编译压缩，修改后缀名。
            InputStream stream = assets.open(assersFileName);
            if (stream == null) {
                //Log.v(TAG,"no file");
                return;
            }


            File f = new File(folder);
            if (!f.exists()) {
                f.mkdirs();
            }

            File file = new File(f, "gcc.zip");
            //创建apk文件
            file.createNewFile();
            //将资源中的文件重写到sdcard中
            //<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
            writeStreamToFile(stream, file);
            //安装apk
            //<uses-permission android:name="android.permission.INSTALL_PACKAGES" />
            //installApk(file);
        } catch (IOException e) {

            e.printStackTrace();
            error = e.getMessage();
        }
    }

    private void writeStreamToFile(InputStream stream, File file) {
        try {
            //
            OutputStream output = null;
            try {
                output = new FileOutputStream(file);
            } catch (FileNotFoundException e1) {

                e1.printStackTrace();
            }
            try {
                try {
                    final byte[] buffer = new byte[2048 * 10];
                    int read;

                    while ((read = stream.read(buffer)) != -1)
                        output.write(buffer, 0, read);

                    output.flush();
                } finally {
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void installApk(File apkfile) {
        //Log.v(TAG,apkPath);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(apkfile),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DLG_CPU_ERROR) {
            return new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("当前系统cpu类型(" + Build.CPU_ABI + ")" + "与当前gcc" +
							"不兼容，如果你有相应的gcc编译器请联系我们，请下载对应的gcc进行安装，或反馈问题。")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
        }
        return super.onCreateDialog(id);
    }

    void requestPermission() {
        //检测单个权限是否已经设置
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            android.util.Log.i(TAG, "requestPermission: 权限申请成功");
//如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回
// false。如果设备规范禁止应用具有该权限，此方法也会返回 false。
            //
            //作者：peter_RD_nj
            //链接：<a href='https://www.jianshu.com/p/2fe4fb3e8ce0'>https://www.jianshu
			// .com/p/2fe4fb3e8ce0</a>
            //来源：简书
            //著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don’t ask again
			// 选项，此方法将返回 false。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                android.util.Log.i(TAG, "requestPermission: 弹出提示");
				/*
				new AlertDialog.Builder(this).setTitle("缺少权限")
						.setMessage("请进入设置，授予相关权限")
						.setPositiveButton("设置", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Intent intent = new Intent(Settings
								.ACTION_APPLICATION_DETAILS_SETTINGS);
								Uri uri = Uri.fromParts("package", getApplication().getPackageName
								(), null);
								intent.setData(uri);
								try {
									startActivity(intent);
								} catch (Exception e) {
									e.printStackTrace();
								}
//————————————————
//				版权声明：本文为CSDN博主「Get_Better」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
//				原文链接：https://blog.csdn.net/Get_Better/article/details/86039148
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {

							}
						}).create().show();
*/

                ConfirmationDialogFragment.newInstance(R.string.sdcard_permission_confirmation,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION, R.string.sdcard_permission_not_granted)
                        .show(getFragmentManager(), "dialog");


            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ConfirmationDialogFragment.newInstance(R.string.sdcard_permission_confirmation,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION, R.string.sdcard_permission_not_granted)
                        .show(getFragmentManager(), "dialog");
            }
            //申请
            else {
                android.util.Log.i(TAG, "requestPermission: 申请权限");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);


            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            }

            int result = grantResults[i];
            android.util.Log.i(TAG, "onRequestPermissionsResult: 权限申请 code=" + requestCode + " permission=" + permission + " result=" + result);
        }
    }

    public void shareFileFromCAPP(Context context, String fileName) {
        File file = new File(fileName);
//        Intent launchIntent = context.getPackageManager()
//                .getLaunchIntentForPackage(context.getPackageName());
        if(getVersionCode("com.xl.capp")>=42){
            if (null != file && file.exists()) {
                Intent share = new Intent(Intent.ACTION_SEND);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri contentUri  = FileProvider.getUriForFile(
                            context, context.getPackageName() + ".fileprovider",
                            file
                    );
                    share.putExtra(Intent.EXTRA_STREAM, contentUri);
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                }
                share.setType("application/zip"); //此处可发送多种文件
                share.setAction(Intent.ACTION_SEND);
                share.putExtra("action","gcc");
                share.setPackage("com.xl.capp");
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(Intent.createChooser(share, "分享文件"));
            } else {
                Toast.makeText(context, "打开手机C失败", Toast.LENGTH_SHORT).show();
            }
        }else
            {
            Intent launchIntent = context.getPackageManager()
                    .getLaunchIntentForPackage("com.xl.capp");
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

            context.startActivity(launchIntent);
        }

    }

}
