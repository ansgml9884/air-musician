package com.example.mediapipemultihandstrackingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mediapipemultihandstrackingapp.R;
import com.example.mediapipemultihandstrackingapp.model.RecordMediaModel;

import java.util.ArrayList;
import java.util.List;

public class MyMusicListAdapter extends RecyclerView.Adapter<MyMusicListAdapter.ViewHolder> {

    private OnItemClickListener mListener = null ;
    private List<RecordMediaModel> mData = null ;


    public interface OnItemClickListener{
        void onPlayClick(View v, int pos);
        void onDeleteClick(View v, int pos);
        void onShareClick(View v, int pos);
    }


    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    // 생성자에서 데이터 리스트 객체를 전달받음.
    public MyMusicListAdapter(List<RecordMediaModel> list) { mData = list ; }
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

            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onPlayClick(v, pos) ;

                        }
                    }
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onDeleteClick(v, pos);
                            mData.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            notifyItemRangeChanged(getAdapterPosition(), mData.size());
                        }
                    }
                }
            });

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onShareClick(v, pos) ;
                            ;
                        }
                    }
                }
            });
        }
    }

    public void changeItem(ArrayList<RecordMediaModel> newData){
        mData = newData;
        notifyDataSetChanged();
    }

}
