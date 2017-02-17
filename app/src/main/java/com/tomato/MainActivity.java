package com.tomato;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tomato.downloader.DownloadCallback;
import com.tomato.downloader.FileDownloadClient;
import com.tomato.downloader.FileDownloadRequest;
import com.tomato.downloader.FileInfo;
import com.tomato.plugindownloader.DownloadListener;
import com.tomato.plugindownloader.DownloaderExecutor;
import com.tomato.plugindownloader.FileRequest;
import com.tomato.shine.DownloadClient;
import com.tomato.shine.DownloadTaskInfo;
import com.tomato.shine.ErrorType;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener/*,DownloadListener*/, com.tomato.shine.DownloadCallback{

    private Button mBtnStart;
    private TextView mTv;
    private DownloaderExecutor downloaderExecutor;
    private String path = Environment.getExternalStorageDirectory().getPath() + "/tomato/";
    private String url = "http://speed.myzone.cn/pc_elive_1.1.rar";
    private String aurl = "http://s17.mogucdn.com/new1/v1/bmisc/73eab2358df2e856847d7116670e5305/142833944464.zip";
    DownloadClient client = new DownloadClient(this);
    DownloadTaskInfo info1;
    DownloadTaskInfo info2;
    DownloadTaskInfo info3;
    private ProgressBar mPbFirst;
    private ProgressBar mPbSecond;
    private ProgressBar mPbThird;

    private TextView mTvFirstTitle;
    private TextView mTvSecondTitle;
    private TextView mTvThirdTitle;

    private Button mBtnFirstStart;
    private Button mBtnFirstPause;
    private Button mBtnFirstResume;
    private Button mBtnSecondStart;
    private Button mBtnSecondPause;
    private Button mBtnSecondResume;
    private Button mBtnThirdStart;
    private Button mBtnThirdPause;
    private Button mBtnThirdResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initView();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView(){
//        mBtnStart = (Button) findViewById(R.id.download_btn_start);
//        mTv = (TextView) findViewById(R.id.download_tv);
//        mBtnStart.setOnClickListener(this);
//        findViewById(R.id.download_btn_pasue).setOnClickListener(this);
//        findViewById(R.id.download_btn_resume).setOnClickListener(this);
        mPbFirst = (ProgressBar) findViewById(R.id.download_progress_first);
        mPbSecond = (ProgressBar) findViewById(R.id.download_progress_second);
        mPbThird = (ProgressBar) findViewById(R.id.download_progress_third);

        mTvFirstTitle = (TextView) findViewById(R.id.download_title_first);
        mTvSecondTitle = (TextView) findViewById(R.id.download_title_second);
        mTvThirdTitle = (TextView) findViewById(R.id.download_title_third);

        mBtnFirstStart = (Button) findViewById(R.id.download_btn_first_start);
        mBtnFirstPause = (Button) findViewById(R.id.download_btn_first_pause);
        mBtnFirstResume = (Button) findViewById(R.id.download_btn_first_resume);

        mBtnSecondStart =(Button) findViewById(R.id.download_btn_second_start);
        mBtnSecondPause =(Button) findViewById(R.id.download_btn_second_pause);
        mBtnSecondResume =(Button) findViewById(R.id.download_btn_second_resume);

        mBtnThirdStart = (Button) findViewById(R.id.download_btn_third_start);
        mBtnThirdPause = (Button) findViewById(R.id.download_btn_third_pause);
        mBtnThirdResume = (Button) findViewById(R.id.download_btn_third_resume);

        mBtnFirstStart.setOnClickListener(this);
        mBtnFirstPause.setOnClickListener(this);
        mBtnFirstResume.setOnClickListener(this);

        mBtnSecondStart.setOnClickListener(this);
        mBtnSecondPause.setOnClickListener(this);
        mBtnSecondResume.setOnClickListener(this);

        mBtnThirdStart.setOnClickListener(this);
        mBtnThirdPause.setOnClickListener(this);
        mBtnThirdResume.setOnClickListener(this);

        DecimalFormat format = new DecimalFormat("##.##");
        mTvFirstTitle.setText(format.format(50/100f));
        mTvSecondTitle.setText(format.format(12120/100f));
        mTvThirdTitle.setText(format.format(3/1000f));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.download_btn_first_start:
//                downloadPlugin(info1);
                Intent intent = new Intent(this,TomotoService.class);
                intent.setAction("com.tomoto.shine.TOMOTO_SERVICE");
                startService(intent);
                break;
            case R.id.download_btn_first_pause:
                client.pauseFileDownlaod(info1);
                break;
            case R.id.download_btn_first_resume:
                client.resumeFileDownload(info1);
                break;
            case R.id.download_btn_second_start:

                downloadPlugin(info2);
                break;
            case R.id.download_btn_second_pause:
                client.pauseFileDownlaod(info2);
                break;
            case R.id.download_btn_second_resume:
                client.resumeFileDownload(info2);
                break;
            case R.id.download_btn_third_start:
                downloadPlugin(info3);
                break;
            case R.id.download_btn_third_pause:
                client.pauseFileDownlaod(info3);
                break;
            case R.id.download_btn_third_resume:
                client.resumeFileDownload(info3);
                break;
        }

    }


    private void initData(){

        info1 = new DownloadTaskInfo(url,path+"info1.zip","info1");
        info2 = new DownloadTaskInfo(aurl,path+"info2.zip","info2");
        info3 = new DownloadTaskInfo(aurl,path+"info3.zip","info3");

    }

    public void downloadPlugin(DownloadTaskInfo info){
        downloaderExecutor = DownloaderExecutor.getInstance();

        client.getFile(info,this);

    }

    @Override
    public void onDownloadUpdate(String fileId, float percent, long current, long total) {
        /*String content = mTv.getText().toString() + "\n";
        mTv.setText(content+fileId + "onUpdateProgress:"+current+"/"+total);*/
        if(!TextUtils.isEmpty(fileId)){
            int mPer = (int)(percent * 100);
            if(fileId.equals(info1.getFileId())){
                mPbFirst.setProgress(mPer);
            }else if(fileId.equals(info2.getFileId())){
                mPbSecond.setProgress(mPer);
            }else if(fileId.equals(info3.getFileId())){
                mPbThird.setProgress(mPer);
            }
        }
    }

    @Override
    public void onDownloadComplete(String fileId, String filePath) {
        /*String content = mTv.getText().toString() + "\n";
        mTv.setText(content+fileId + "onFinishDownload");*/
        if(!TextUtils.isEmpty(fileId)){
            if(fileId.equals(info1.getFileId())){
                mTvFirstTitle.setText("Complete");
            }else if(fileId.equals(info2.getFileId())){
                mTvSecondTitle.setText("Complete");
            }else if(fileId.equals(info3.getFileId())){
                mTvThirdTitle.setText("Complete");
            }
        }
    }

    @Override
    public void onDownloadFail(String fileId, ErrorType error) {
    /*    String content = mTv.getText().toString() + "\n";
        mTv.setText(content+fileId+ "onFailDownload");*/
        if(!TextUtils.isEmpty(fileId)){
            if(fileId.equals(info1.getFileId())){
                mTvFirstTitle.setText("Fail");
            }else if(fileId.equals(info2.getFileId())){
                mTvSecondTitle.setText("Fail");
            }else if(fileId.equals(info3.getFileId())){
                mTvThirdTitle.setText("Fail");
            }
        }
    }
}
