package com.xl.gccpage;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

//    //复制文件
//    private static void copyFiles(String filename, String newfilename) {
//
//        File file = new File(filename);
//        int start = 0;
//        String endtext = null;
//        if (filename.equals(newfilename)) //同一个文件
//        {
//            char[] bhte_newfilename = newfilename.toCharArray();
//            //如果是文件夹
//            if (file.isDirectory()) {
//                newfilename = newfilename + "_new";
//            }
//            //如果是文件
//            else {
//                //取后缀
//                start = Str.strrchr(newfilename, '.');
//
//                if (start > 0) {
//                    endtext = newfilename.substring(start);
//                    newfilename = newfilename.substring(0, start) + "_new" + endtext;
//                } else {
//                    newfilename = newfilename + "_new";
//                }
//            }
//
//            if (new File(newfilename).exists()) {
////                toast(getString(R.string.toast_file_name_conflict));
//                return;
//            }
//        }
//        if (file.isFile()) {
//            copyFile(filename, newfilename);
//        } else {
//            copyFolder(filename, newfilename);
//        }
//    }

    /** 
          *  复制单个文件 
          *  @param  oldPath  String  原文件路径  如：c:/fqf.txt 
          *  @param  newPath  String  复制后路径  如：f:/fqf.txt 
          *  @return  boolean 
          */
    public static void copyFile(String oldPath,String newPath)
    {
        copyFile(new File(oldPath),new File(newPath));
    }

    public static void copyFile(File oldFile,File newFile)
    {
        if(oldFile.getAbsolutePath().equals(newFile.getAbsolutePath()))return;
        try
        {
//           int  bytesum  =  0; 
            int byteread = 0;
            //File oldfile = new File(oldPath);
            if(oldFile.isFile() )
            {//文件存在时 
                InputStream inStream=new FileInputStream(oldFile);//读入原文件 
                FileOutputStream fs = new FileOutputStream(newFile);
                byte[] buffer = new byte[1444];
//               int  length; 
                while((byteread=inStream.read(buffer))!=-1)
                {
//                   bytesum  +=  byteread;  //字节数  文件大小 
//                   System.out.println(bytesum); 
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.flush();
                fs.close();
            }
        }
        catch(Exception e)
        {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /** 
          *  复制整个文件夹内容 
          *  @param  oldPath  String  原文件路径  如：c:/fqf 
          *  @param  newPath  String  复制后路径  如：f:/fqf/ff 
          *  @return  boolean 
          */
    public static void copyFolder(String oldPath,String newPath)
    {

        try
        {

            Log.e("copyFolder", ""+oldPath+" "+newPath);
            new File(newPath).mkdirs();//如果文件夹不存在  则建立新文件夹 
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for(int i = 0; i<file.length; i++)
            {
                if(oldPath.endsWith(File.separator))
                {
                    temp=new File(oldPath+file[i]);
                }
                else
                {
                    temp=new File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile())
                {
                    FileInputStream input= new FileInputStream(temp);
                    FileOutputStream output=new FileOutputStream(newPath+"/"+
                            (temp.getName()).toString());
                    byte[] b = new byte[1024*5];
                    int len;
                    while((len=input.read(b))!=-1)
                    {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory())
                {//如果是子文件夹 

                    copyFolder(oldPath+"/"+file[i], newPath+"/"+file[i]);
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    //获取sd卡
    public static String getSDPath()
    {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if(sdCardExist)
        {
            sdDir=Environment.getExternalStorageDirectory();//获取sd卡目录
        }
        else
        {
            return null;
        }
        return sdDir.getPath();
    }

    //获取文件base64码
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.NO_WRAP);
    }

    public static String readText(File file,String encoding) throws IOException
    {
        String content = "";
        //	File file = new File(path);

        if(file.isFile())
        {
            FileInputStream input= new FileInputStream(file);

            byte [] buf=new byte[input.available()];
            input.read(buf);
            input.close();
            content = new String(buf,encoding);
        }
        return content;
    }

    public static String writeText(File file,String encoding,String text) throws IOException
    {
        String content = "";
        //	File file = new File(path);


        FileOutputStream input= new FileOutputStream(file);


        input.write(text.getBytes(encoding));
        input.close();

        return text;
    }

}
