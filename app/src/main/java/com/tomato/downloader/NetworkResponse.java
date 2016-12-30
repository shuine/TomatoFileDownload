package com.tomato.downloader;

import java.net.HttpURLConnection;
import java.util.Map;

/**
 * @author yeshuxin on 16-10-26.
 */

public class NetworkResponse {

    public final int status_code;
    public final Map<String,String> header;
    public final long network_time;
    public final boolean notModify;

    public NetworkResponse(Map<String,String> header){
        this(HttpURLConnection.HTTP_OK,header,false);
    }
    public NetworkResponse(int status,Map<String,String> header,boolean modify){

        this(status,header,0,modify);
    }
    public NetworkResponse(int status,Map<String,String> header,long time,boolean modify){
        this.status_code = status;
        this.header = header;
        this.network_time = time;
        this.notModify = modify;
    }
}
