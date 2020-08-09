package com.cho.ksoo.makeplaypiano;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.support.annotation.NonNull;

public class MusicViewAdapter extends RecyclerView.Adapter<MusicViewAdapter.MusicViewHolder>{

    public interface OnItemLongSelectedListener {
        void onItemLongSelected(View v, int position);
    }

    public interface OnItemClickListener {
        void onItemSelected(View v, int position);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemLongSelectedListener mLongListener = null;
    private OnItemClickListener mListener = null ;

    // 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public void setOnItemLongSelectedListener(OnItemLongSelectedListener listener) {
        this.mLongListener = listener ;
    }

    Context mContext;
    RecyclerView recyclerView;

    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);

    private String[] music_id, music_title, music_descr;
    MusicViewHolder musicHolder ;


    public MusicViewAdapter(Context context
            , RecyclerView recyclerView
            , OnItemClickListener listener
            , OnItemLongSelectedListener longListener) {
        this.mContext = context;
        this.mListener = listener;
        this.mLongListener = longListener;
        this.recyclerView = recyclerView;
    }

    public void setData(String[] music_id,
                        String[] music_title,
                        String[] music_descr) {

        this.music_id = music_id;
        this.music_title = music_title;
        this.music_descr = music_descr;
        notifyDataSetChanged();
    }

    public class MusicViewHolder extends  RecyclerView.ViewHolder{

        public TextView music_id, music_title, music_descr;

        public MusicViewHolder(@NonNull View view){

            super(view);

            this.music_id = view.findViewById(R.id.tvMisucID);
            this.music_title = view.findViewById(R.id.tvMusicTitle);
            this.music_descr = view.findViewById(R.id.tvMusicDescr);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {

                        if (mListener != null) {
                            toggleItemSelected(position);
                            Log.d("ksoocho", "position = " + position);
                        }

                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        if (mLongListener != null) {
                            mLongListener.onItemLongSelected(v, position);
                        }
                    }

                    return false;
                }
            });

        }
    }

    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_list_view, parent, false);
        musicHolder = new MusicViewHolder(holderView);

        return musicHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder musicHolder, int i) {

        musicHolder.music_id.setText(this.music_id[i]);
        musicHolder.music_title.setText(this.music_title[i]);
        musicHolder.music_descr.setText(this.music_descr[i]);

        if (isItemSelected(i)) {
            musicHolder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            musicHolder.itemView.setBackgroundColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        return music_id.length;
    }

    /**
     * Method Name : isItemSelected
     * Description : RecyclerView에서 해당 목록이 선택되었는지 확인
     */
    public boolean isItemSelected(int position) {
        return mSelectedItems.get(position, false);
    }

    /**
     * Method Name : toggleItemSelected
     * Description : RecyclerView에서 선택한 목록 선택/해제 Toggle
     */
    public void toggleItemSelected(int position) {

        if (mSelectedItems.get(position, false) == true) {
            mSelectedItems.delete(position);
            notifyItemChanged(position);
        } else {
            mSelectedItems.put(position, true);
            notifyItemChanged(position);
        }
    }

    /**
     * Method Name : clearSelectedItem
     * Description : RecyclerView에서 선택한 목록을 전체 해제한다.
     */
    public void clearSelectedItem() {
        int position;

        for (int i = 0; i < mSelectedItems.size(); i++) {
            position = mSelectedItems.keyAt(i);
            mSelectedItems.put(position, false);
            notifyItemChanged(position);
        }

        mSelectedItems.clear();
    }

    /**
     * Method Name : getSelectedItem
     * Description : RecyclerView에서 선택한 목록 List
     * Memo        : 최대목록 ??
     */
    public String[] getSelectedItem() {
        int position;

        String[] vMusicID = new String[50];

        int vCount = 0;

        for (int i = 0; i < mSelectedItems.size(); i++) {
            position = mSelectedItems.keyAt(i);

            if ( isItemSelected(position))
            {
                vMusicID[vCount] = music_id[position];
                vCount++;
            }
        }

        return vMusicID;
    }

}
