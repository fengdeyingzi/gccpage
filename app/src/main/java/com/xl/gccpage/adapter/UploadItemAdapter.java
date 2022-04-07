package com.xl.gccpage.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.xl.gccpage.FileUtil;
import com.xl.gccpage.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UploadItemAdapter extends RecyclerView.Adapter<UploadItemAdapter.UploadViewHolder> {
    //    var text_carnum:String? = null
//    var text_type:String? = null
//    var text_state:String? = null
    public class UploadDataItem {
        public File file;
        public String text_name;
        public boolean isChecked;
        public UploadDataItem(File file,String text_name,boolean isChecked){
            this.file = file;
            this.text_name = text_name;
            this.isChecked = isChecked;
        }
    }

    //自定义ViewHolder,包含item的所有界面元素
    public class UploadViewHolder extends RecyclerView.ViewHolder {
        TextView text_name = null;
        CheckBox check_item = null;
//        LinearLayout layout_head = null;
        View btn_delete = null;

        UploadViewHolder(View itemView) {
            super(itemView);
            text_name = itemView.findViewById(R.id.text_item);
//            layout_head = itemView.findViewById(R.id.layout_head);
            check_item = itemView.findViewById(R.id.check_item);
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }

    ArrayList<UploadDataItem> list_data = null;
    AdapterView.OnItemClickListener onItemClickListener = null;

    public UploadItemAdapter(Context context) {
        list_data = new ArrayList();
    }

    public void updateData(ArrayList<UploadDataItem> list) {
        this.list_data = list;
        notifyDataSetChanged();
    }

    public void deleteItem(UploadDataItem item) {
        this.list_data.remove(item);
        item.file.delete();
        notifyDataSetChanged();
    }

    @Override
    public UploadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_upload, parent, false);

        UploadViewHolder holder = new UploadViewHolder(view);
        return holder;
    }

    //    fun setOnItemClickListener(listener:OnItemClickListener){
//        this.onItemClickListener = listener
//    }
    @Override
    public void onBindViewHolder(final UploadViewHolder holder, final int position) {

        holder.text_name.setText(list_data.get(position).text_name);
        holder.check_item.setChecked((list_data.get(position).isChecked));

        holder.check_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                list_data.get(position).isChecked = (isChecked);
            }
        });


        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(list_data.get(position));
            }
        });
        holder.text_name.setEnabled(true);
        holder.text_name.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle(holder.text_name.getText())
                            .setMessage(FileUtil.readText(list_data.get(position).file,"UTF-8"))
                            .setNegativeButton("确定",null).create().show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public ArrayList<UploadDataItem> getCheckedList() {
        ArrayList<UploadDataItem> list = new ArrayList<UploadDataItem>();
        for (int i = 0; i < list_data.size(); i++) {
            UploadDataItem item = list_data.get(i);
            if (item.isChecked) {
                list.add(item);
            }

        }
        return list;
    }

    public ArrayList<UploadDataItem> getList(){
        return list_data;
    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }
}








