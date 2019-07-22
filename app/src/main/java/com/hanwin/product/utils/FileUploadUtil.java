package com.hanwin.product.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.hanwin.product.common.BaseApplication;
import com.hanwin.product.common.model.BaseRespMsg;
import com.hanwin.product.home.bean.ImageResultBean;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class FileUploadUtil {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 25 * 1000;   //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    private static UploadListener uploadListener;

    public FileUploadUtil() {
    }

    /**
     * android上传文件到服务器
     *
     * @param file       需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public static String uploadFile(File file, String RequestURL, String userName,String...str) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            if (file != null) {
                System.out.println(file.getName());
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png  
                 */
                System.out.println(file.getName());
                if(str != null && str.length > 0){
                    sb.append("Content-Disposition: form-data; name=\"subDoc\"; filename=\"" + file.getName() + "\"" + LINE_END);
                }else{
                    sb.append("Content-Disposition: form-data; name=\"image\"; filename=\"" + file.getName() + "\"" + LINE_END);
                }
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                //报文参数需要单独拼
                StringBuffer sbff = new StringBuffer();
                sbff.append("--");
                sbff.append(BOUNDARY);
                sbff.append("\r\n");
                sbff.append("Content-Disposition: form-data; name=\"userName\"" + "\r\n\r\n");
                sbff.append(userName);
                sbff.append("\r\n");
                dos.write(sbff.toString().getBytes("utf-8"));

                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                dos.close();
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流  
                 */
                int res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);
                if (res == 200) {
                    Log.e(TAG, "request success");
                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                    Log.e(TAG, "result : " + result);
                } else {
                    Log.e(TAG, "request error");
                }
            }
        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson mGson = new Gson();
        ImageResultBean respMsg = mGson.fromJson(result, ImageResultBean.class);
        uploadListener.uploadListener(respMsg);
        return result;
    }

    /**
     * 参数和图片一起上传
     *
     * @param file
     * @param RequestURL
     * @param params
     * @return
     */
    public static String uploadFile1(File file, String RequestURL, Map<String, String> params) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //内容类型

        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

//            if(file!=null) {
            /**
             * 当文件不为空，把文件包装并且上传
             */
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */
                if(RequestURL.contains("signPost")){
                    //实名认证的上传图片
                    sb.append("Content-Disposition: form-data; name=\"image\"; filename=\"" + file.getName() + "\"" + LINE_END);
                }else{
                    //个人资料保存的残疾证
                    sb.append("Content-Disposition: form-data; name=\"disableImg\"; filename=\"" + file.getName() + "\"" + LINE_END);
                }
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
            }
            //报文参数需要单独拼
            StringBuilder sbff = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sbff.append(PREFIX);
                sbff.append(BOUNDARY);
                sbff.append(LINE_END);
                sbff.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
                sbff.append(LINE_END);
                sbff.append(entry.getValue());
                sbff.append(LINE_END);
            }
            dos.write(sbff.toString().getBytes("utf-8"));

            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();
            dos.close();
            /**
             * 获取响应码  200=成功
             * 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            Log.e(TAG, "response code: ======== " + res);
            if (res == 200) {
                Log.e(TAG, "request success");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream input = conn.getInputStream();
                int ss;
                byte[] buffer = new byte[1024];
                while ((ss = input.read(buffer)) != -1) {
                    baos.write(buffer, 0, ss);
                }
                baos.close();
                byte[] bytess = baos.toByteArray();
                result = new String(bytess, "utf-8");
                Log.e(TAG, "result : " + result);
            } else {
                Log.e(TAG, "request error");
            }
//            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson mGson = new Gson();
        BaseRespMsg respMsg = mGson.fromJson(result, BaseRespMsg.class);
        uploadListener.uploadListener1(respMsg);
        return result;
    }


    public interface UploadListener {
        void uploadListener(ImageResultBean respMsg);

        void uploadListener1(BaseRespMsg respMsg);
    }

    public void setUploadListener(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }
}