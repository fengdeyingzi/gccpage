package com.xl.gccpage.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.xl.gccpage.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/*
① 创建一个继承RecyclerView.Adapter<VH>的Adapter类
② 创建一个继承RecyclerView.ViewHolder的静态内部类
③ 在Adapter中实现3个方法：
   onCreateViewHolder()
   onBindViewHolder()
   getItemCount()
*/
public class ShareFileItemAdapter extends RecyclerView.Adapter<ShareFileItemAdapter.MyViewHolder>{
    private Context context;
    private JSONObject json_shareFile;
    private View inflater;
    private OnItemClickListener onItemClickListener;
    //构造方法，传入数据
    public ShareFileItemAdapter(Context context, JSONObject json_shareFile){
        this.context = context;
        this.json_shareFile = json_shareFile;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建ViewHolder，返回每一项的布局
        inflater = LayoutInflater.from(context).inflate(R.layout.list_item_sharefile,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(inflater);
        return myViewHolder;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //将数据和控件绑定
        try{
            JSONArray jsonArray = json_shareFile.getJSONArray("data");
            JSONObject json_item = jsonArray.getJSONObject(position);
            final String name = json_item.getString("name");
            String road = json_item.getString("road");
            String size = json_item.getString("size");
            holder.text_filename.setText(name);
            holder.text_size.setText(size);
            holder.layout_root.setClickable(true);

            holder.layout_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Share", "onClick: ");
                    if(onItemClickListener!=null){
                        onItemClickListener.onItemClickListener(name, position);
                    }
                }
            });
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        //返回Item总条数
        JSONArray jsonArray = null;
        try {
            jsonArray = json_shareFile.getJSONArray("data");
            return jsonArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //内部类，绑定控件
    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout_root;
        ImageView img_type;
        TextView text_filename;
        TextView text_size;
        public MyViewHolder(View itemView) {
            super(itemView);
            layout_root = (LinearLayout)itemView;
            text_filename = (TextView) itemView.findViewById(R.id.text_filename);
            text_size = itemView.findViewById(R.id.text_size);
            img_type = itemView.findViewById(R.id.img_type);
        }
    }

    public interface OnItemClickListener{
        public void onItemClickListener(String name, int index);
    }
}
