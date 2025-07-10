package com.local.amjbc.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

 
public class JSONParser {
 
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
 
    public JSONParser() {
 
    }
    // by making HTTP POST or GET mehtod
    public JSONObject  nmakeHttpRequest(String urlString, String method,
                                        List<Map.Entry<String, String>> params) {

        HttpURLConnection conn = null;
        InputStream is = null;
        StringBuilder response = new StringBuilder();
        JSONObject jsonObject = null;

        try {
            URL url;

            if ("GET".equalsIgnoreCase(method)) {
                String paramString = getQuery(params);
                url = new URL(urlString + "?" + paramString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            } else if ("POST".equalsIgnoreCase(method)) {
                url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();
            } else {
                throw new UnsupportedOperationException("Unsupported request method: " + method);
            }

            conn.connect();
            is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            jsonObject = new JSONObject(response.toString());

        } catch (Exception e) {
            Log.e("JSONParser", "Error making HTTP request: " + e.getMessage(), e);
        } finally {
            if (is != null) try { is.close(); } catch (Exception ignored) {}
            if (conn != null) conn.disconnect();
        }

        return jsonObject;
    }

    private String getQuery(List<Map.Entry<String, String>> params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}


//
//        try {
//
//            if(method == "POST"){
//
//                DefaultHttpClient httpClient = new DefaultHttpClient();
//                HttpPost httpPost = new HttpPost(url);
//
//                httpPost.setEntity(new UrlEncodedFormEntity(params));
//                HttpResponse httpResponse = httpClient.execute(httpPost);
//                HttpEntity httpEntity = httpResponse.getEntity();
//                is = httpEntity.getContent();
//
//            }else if(method == "GET"){
//
//                DefaultHttpClient httpClient = new DefaultHttpClient();
//                httpClient.setre
//                String paramString = URLEncodedUtils.format(params, "utf-8");
//                url += "?" + paramString;
//                HttpGet httpGet = new HttpGet(url);
//                HttpResponse httpResponse = httpClient.execute(httpGet);
//                HttpEntity httpEntity = httpResponse.getEntity();
//                is = httpEntity.getContent();
//            }
//
//        } catch (UnsupportedEncodingException e) {
//
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//
//            e.printStackTrace();
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//
//            while ((line = reader.readLine()) != null) {
//                sb.append(line + "\n");
//            }
//            is.close();
//            json = sb.toString();
//        } catch (Exception e) {
//            Log.e("Buffer Error", "Error converting result " + e.toString());
//        }
//
//        try {
//            jObj = new JSONObject(json);
//        } catch (JSONException e) {
//            Log.e("JSON Parser", "Error parsing data " + e.toString());
//        }
//
//        return jObj;
//
//    }
//}