package com.cho.ksoo.makeplaypiano;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MusicListActivity extends AppCompatActivity implements MusicViewAdapter.OnItemLongSelectedListener
        , MusicViewAdapter.OnItemClickListener{

    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    RecyclerView recyclerView;
    //MusicViewAdapter  mAdapter;

    String[] music_id;
    String[] music_title;
    String[] music_descr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = (RecyclerView) findViewById(R.id.rvMusicList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 악보 List 보이기
        final MusicViewAdapter mAdapter = new MusicViewAdapter(this, recyclerView, this,this);
        //mAdapter = new MusicViewAdapter(this, recyclerView, this,this);
        recyclerView.setAdapter(mAdapter);

        // 악보 List 가져오기 - OPEN + User ID별
        // 악보 List 가져오기
        int vCount = 0;

        for ( int inx = 0; inx < ((PianoApp)this.getApplicationContext()).musicList.length; inx++)
        {
            String v_music_id = ((PianoApp)this.getApplicationContext()).musicList[inx].getMusicID();

            if (!v_music_id.equals("")) {
                vCount++;
            }
        }

        music_id = new String[vCount];
        music_title = new String[vCount];
        music_descr = new String[vCount];

        for ( int inx = 0; inx < vCount; inx++)
        {
            music_id[inx] =  ((PianoApp)this.getApplicationContext()).musicList[inx].getMusicID();
            music_title[inx] = ((PianoApp)this.getApplicationContext()).musicList[inx].getMusicTitle();
            music_descr[inx] =  ((PianoApp)this.getApplicationContext()).musicList[inx].getMusicDescr();
        }

        mAdapter.setData(music_id, music_title, music_descr);

        // -------------------------------
        // 기본 구분선 추가
        // -------------------------------
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(),new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // -------------------------------
        // 아이템간 공백 추가
        // -------------------------------
        RecyclerDecoration spaceDecoration = new RecyclerDecoration(20);
        recyclerView.addItemDecoration(spaceDecoration);

        /*
        // -----------------------------------------------------------------
        // 악보 선택 버튼
        // 이벤트 리스너 인터페이스를 implements하는 이벤트 리스너 클래스 생성하기
        // -----------------------------------------------------------------
        Button btnConfirm = (Button) findViewById(R.id.btnListConfirm) ;

        btnConfirm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] v_music_arr = mAdapter.getSelectedItem();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("music_id",v_music_arr[0]);
                setResult(0,resultIntent);
                finish();
            }
        });

        */

    }

    // -----------------------------------------------------------------
    // Option Menu 보이기 - onCreateOptionsMenu
    // -----------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_list, menu);
        //menu.findItem(R.id.menu_list_clear).setVisible(true);
        //menu.findItem(R.id.menu_list_select).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    // -----------------------------------------------------------------
    // Option Menu 선택하기 - onOptionsItemSelected
    // -----------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int curId = item.getItemId();

        switch (curId) {
            case R.id.menu_list_clear:
                //mAdapter.clearSelectedItem();
                break;

            case R.id.menu_list_select:
//                String[] v_music_arr = mAdapter.getSelectedItem();
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("music_id",v_music_arr[0]);
//                setResult(0,resultIntent);
//                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(View v, int position) {
        MusicViewAdapter.MusicViewHolder viewHolder = (MusicViewAdapter.MusicViewHolder)recyclerView.findViewHolderForAdapterPosition(position);
        Toast.makeText(this, viewHolder.music_id.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongSelected(View v, int position) {
        MusicViewAdapter.MusicViewHolder viewHolder = (MusicViewAdapter.MusicViewHolder)recyclerView.findViewHolderForAdapterPosition(position);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("music_id",viewHolder.music_id.getText().toString());
        setResult(0,resultIntent);
        finish();

        Toast.makeText(this, position + " long clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("music_id","0");
        setResult(0,resultIntent);
        finish();
    }

}
