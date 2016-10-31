package com.tomato;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tomato.download.DownloadCallback;
import com.tomato.download.FileDownloadClient;
import com.tomato.download.FileDownloadRequest;
import com.tomato.download.FileInfo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBtnStart;
    private TextView mTv;
    private String path = Environment.getExternalStorageDirectory().getPath() + "/tomato/";
    private String url = "http://speed.myzone.cn/pc_elive_1.1.rar";
    private String aurl = "http://s17.mogucdn.com/new1/v1/bmisc/73eab2358df2e856847d7116670e5305/142833944464.zip";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView(){
        mBtnStart = (Button) findViewById(R.id.download_btn_start);
        mTv = (TextView) findViewById(R.id.download_tv);
        mBtnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == mBtnStart.getId()){
            downloadTest();
        }
    }

    private void downloadTest(){
        FileDownloadClient client = FileDownloadClient.getInstance();
        FileInfo info = new FileInfo();
        info.setTag("downloadFileTest");
        info.setUrl(aurl);
        info.setFileName("miTest");
        info.setPat(path);
        FileDownloadRequest request = new FileDownloadRequest(info, new DownloadCallback() {
            @Override
            public void onDownloadUpdate(FileInfo info, long currentLength, long totalLength) {

            }

            @Override
            public void onDownloadComplete(FileInfo info) {

            }

            @Override
            public void onDownloadFail(FileInfo info, int errorType) {

            }

            @Override
            public void onDownloadSuccess(FileInfo info) {
                Toast.makeText(MainActivity.this,"Download Complete:",Toast.LENGTH_SHORT).show();
            }
        });
        client.getDownloadFile(request);
    }

}
