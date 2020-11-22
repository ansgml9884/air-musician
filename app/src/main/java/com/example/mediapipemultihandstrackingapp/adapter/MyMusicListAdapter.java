package com.example.mediapipemultihandstrackingapp.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mediapipemultihandstrackingapp.R;
import com.example.mediapipemultihandstrackingapp.model.RecordVideoModel;

import java.util.List;

public class MyMusicListAdapter extends RecyclerView.Adapter<MyMusicListAdapter.ViewHolder> {

    private List<RecordVideoModel> mData = null ;


    public interface ItemClickListener{
        public void onClick(View view, int pos);
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView videoName;
        TextView videoDate;
        TextView videoDuration;
        ImageView videoThumnail;
        ImageButton playBtn;
        ImageButton deleteBtn;
        ImageButton shareBtn;

        ViewHolder(View itemView) {
            super(itemView) ;
            videoName = itemView.findViewById(R.id.video_name);
            videoDate = itemView.findViewById(R.id.video_date);
            videoDuration = itemView.findViewById(R.id.video_duration);
            videoThumnail = itemView.findViewById(R.id.video_thumbnail);
            playBtn = itemView.findViewById(R.id.videoPlay);
            deleteBtn = itemView.findViewById(R.id.videoDelete);
            shareBtn = itemView.findViewById(R.id.videoShare);
        }
    }


    // 생성자에서 데이터 리스트 객체를 전달받음.
    public MyMusicListAdapter(List<RecordVideoModel> list) { mData = list ; }
    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public MyMusicListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.mymusic_list_item, parent, false) ;
        MyMusicListAdapter.ViewHolder vh = new MyMusicListAdapter.ViewHolder(view) ;
        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(MyMusicListAdapter.ViewHolder holder, int position) {
        holder.videoName.setText(mData.get(position).getName());
        holder.videoDate.setText(String.valueOf(mData.get(position).getDate()));
        holder.videoDuration.setText(mData.get(position).getDuration());
        holder.videoThumnail.setImageBitmap(mData.get(position).getThumbnail());
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

}
