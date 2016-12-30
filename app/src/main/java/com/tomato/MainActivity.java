package com.tomato;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tomato.downloader.DownloadCallback;
import com.tomato.downloader.FileDownloadClient;
import com.tomato.downloader.FileDownloadRequest;
import com.tomato.downloader.FileInfo;
import com.tomato.plugindownloader.DownloadListener;
import com.tomato.plugindownloader.DownloaderExecutor;
import com.tomato.plugindownloader.FileRequest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,DownloadListener{

    private Button mBtnStart;
    private TextView mTv;
    private DownloaderExecutor downloaderExecutor;
    private String path = Environment.getExternalStorageDirectory().getPath() + "/tomato/";
    private String url = "http://speed.myzone.cn/pc_elive_1.1.rar";
    private String aurl = "http://s17.mogucdn.com/new1/v1/bmisc/73eab2358df2e856847d7116670e5305/142833944464.zip";
    FileRequest request1;

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
        findViewById(R.id.download_btn_pasue).setOnClickListener(this);
        findViewById(R.id.download_btn_resume).setOnClickListener(this);

        request1 = new FileRequest();
        request1.url = url;
        request1.fileName = "request1";
        request1.path = path;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == mBtnStart.getId()){
          /*  downloadTest();
            mBtnStart.setEnabled(false);*/
            downloadPlugin();
        }else if(v.getId() == R.id.download_btn_pasue){
            if(downloaderExecutor != null){
                downloaderExecutor.pauseRequest(request1);
            }
        }else if(v.getId() == R.id.download_btn_resume){
            downloaderExecutor.resumeRequest(request1);
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

                mTv.setText("Download:"+currentLength+"/"+totalLength);
            }

            @Override
            public void onDownloadComplete(FileInfo info) {

                mBtnStart.setEnabled(true);
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

    public void downloadPlugin(){
        downloaderExecutor = DownloaderExecutor.getInstance();

        FileRequest request2 = new FileRequest();
        request2.url = aurl;
        request2.fileName = "request2";
        request2.path = path;
        FileRequest request3 = new FileRequest();
        request3.url = aurl;
        request3.fileName = "request3";
        request3.path = path;
        FileRequest request4 = new FileRequest();
        request4.url = aurl;
        request4.fileName = "request4";
        request4.path = path;

        downloaderExecutor.downloadFile(request1,this);
//        downloaderExecutor.downloadFile(request2,this);
//        downloaderExecutor.downloadFile(request3,this);
//        downloaderExecutor.downloadFile(request4,this);

    }

    @Override
    public void onStartDownload(FileRequest request) {

        String content = mTv.getText().toString() + "\n";
        mTv.setText(content+request.fileName + "onStartDownload");
    }

    @Override
    public void onPauseDownload(FileRequest request) {
        String content = mTv.getText().toString() + "\n";
        mTv.setText(content+request.fileName + "onPauseDownload");
    }

    @Override
    public void onSuccessDownload(FileRequest request) {
        String content = mTv.getText().toString() + "\n";
        mTv.setText(content+request.fileName + "onSuccessDownload");
    }

    @Override
    public void onFinishDownload(FileRequest request) {
        String content = mTv.getText().toString() + "\n";
        mTv.setText(content+request.fileName + "onFinishDownload");
    }

    @Override
    public void onCancelDownload(FileRequest request) {
        String content = mTv.getText().toString() + "\n";
        mTv.setText(content+request.fileName + "onCancelDownload");
    }

    @Override
    public void onFailDownload(FileRequest request, int code, String msg) {
        String content = mTv.getText().toString() + "\n";
        mTv.setText(content+request.fileName + "onFailDownload");
    }

    @Override
    public void onUpdateProgress(FileRequest request, long current, long total) {
        String content = mTv.getText().toString() + "\n";
        mTv.setText(content+request.fileName + "onUpdateProgress:"+current+"/"+total);
    }
}
