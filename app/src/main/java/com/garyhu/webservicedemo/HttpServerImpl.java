package com.garyhu.webservicedemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import android.text.TextUtils;
import android.util.Log;

/**
 *
 * @author lixm
 *
 */
public class HttpServerImpl extends NanoHTTPD {

	//  http://172.22.158.31:8080/getFileList?dirPath=/sdcard
	//  http://172.22.158.31:8080/getFile?fileName=/sdcard/FaceFingerMatch_AD
	
    public static final int DEFAULT_SERVER_PORT = 8080;
    public static final String TAG = "lixm";

    private static final String REQUEST_ROOT = "/";
    private static final String REQUEST_TEST = "/test";
    private static final String REQUEST_ACTION_GET_FILE = "/getFile";
    private static final String REQUEST_ACTION_GET_FILE_LIST = "/getFileList";

    public HttpServerImpl() {
        super(DEFAULT_SERVER_PORT);
    }

    @Override
    public Response serve(IHTTPSession session) {
    	String strUri = session.getUri();
    	String method = session.getMethod().name();
        Log.d(TAG,"Response serve uri = " + strUri + ", method = " + method);

        
        if(REQUEST_ROOT.equals(strUri)) {   // 根目录
            return responseRootPage(session);
        }else if(REQUEST_TEST.equals(strUri)){    // 返回给调用端json串
        	return responseJson();
        }else if(REQUEST_ACTION_GET_FILE_LIST.equals(strUri)){    // 获取文件列表
        	Map<String,String> params = session.getParms();

        	String dirPath = params.get("dirPath");
        	if(!TextUtils.isEmpty(dirPath)){
        		return responseFileList(session,dirPath);
        	}        	
        }else if(REQUEST_ACTION_GET_FILE.equals(strUri)){ // 下载文件
        	Map<String,String> params = session.getParms();
        	// 下载的文件名称
        	String fileName = params.get("fileName");
        	
        	
        	File file = new File(fileName);
        	if(file.exists()){
        		if(file.isDirectory()){
        			return responseFileList(session,fileName);
        		}else{
        			return responseFileStream(session,fileName);
        		}
        	}        	
        }
        return response404(session);
    }

    private Response responseRootPage(IHTTPSession session) {

        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("这是胡伟伟的测试 \n");
        builder.append("</body></html>\n");
        //return Response.newFixedLengthResponse(Status.OK, "application/octet-stream", builder.toString());
        return Response.newFixedLengthResponse(builder.toString());
    }

    /**
     * 返回给调用端LOG日志文件
     * @param session
     * @return
     */
    private Response responseFileStream(IHTTPSession session,String filePath) {
    	Log.d("lixm", "responseFileStream() ,fileName = " + filePath);
        try {
            FileInputStream fis = new FileInputStream(filePath);
            //application/octet-stream
            return Response.newChunkedResponse(Status.OK, "application/octet-stream", fis);
        }
        catch (FileNotFoundException e) {        
            Log.d("lixm", "responseFileStream FileNotFoundException :" ,e);
            return response404(session);
        }
    }

    /**
     * 
     * @param session http请求
     * @param dirPath 文件夹路径名称
     * @return
     */
    private Response responseFileList(IHTTPSession session,String dirPath) {
    	Log.d("lixm", "responseFileList() , dirPath = " + dirPath);
    	List <String> fileList = FileUtils.getFilePaths(dirPath, false);
    	StringBuilder sb = new StringBuilder();
    	for(String filePath : fileList){
    		sb.append("<a href=" + REQUEST_ACTION_GET_FILE + "?fileName=" + filePath + ">" + filePath + "</a>" + "<br>");
    	}
    	return Response.newFixedLengthResponse(sb.toString());
    }  

    /**
     * 调用的路径出错
     * @param session
     * @param url
     * @return
     */
    private Response response404(IHTTPSession session) {
    	String url = session.getUri();
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");        
        builder.append("Sorry, Can't Found "+url + " !");        
        builder.append("</body></html>\n");
        return Response.newFixedLengthResponse(builder.toString());
    }

    /**
     * 返回给调用端json字符串
     * @return
     */
    private Response responseJson(){
    	return Response.newFixedLengthResponse("调用成功");
    }
}
