package com.sangmee.eyegottttt.CSSapi;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

public class APIExamTTS {

    private static String TAG = "APIExamTTS";

    public static void main(String[] args) {
        String clientId = "ssbj4qersa";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "beB114Rs5Hc5LyCKxrIZu22D55jlHYVVRSr23O2V";//애플리케이션 클라이언트 시크릿값";
        try {
            String text = URLEncoder.encode(args[0], "UTF-8");
            String apiURL = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            // post request
            String postParams = "speaker=mijin&speed=0&text=" + text;
            System.out.println(postParams);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];


                File dir = new File(Environment.getExternalStorageDirectory() + "/", "NCP");


                if (!dir.exists()) {
                    dir.mkdirs();
                }


                String tempname = "csstemp";
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "NCP/" + tempname + ".mp3");
                f.createNewFile();
                OutputStream outputStream = new FileOutputStream(f);
                while ((read = is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                is.close();


                String pathToFile = Environment.getExternalStorageDirectory() + File.separator + "NCP/" + tempname + ".mp3";
                MediaPlayer audioPlay = new MediaPlayer();
                audioPlay.setDataSource(pathToFile);
                audioPlay.prepare();
                audioPlay.start();


            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


}