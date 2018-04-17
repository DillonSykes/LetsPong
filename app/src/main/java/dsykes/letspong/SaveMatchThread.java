package dsykes.letspong;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by jaydo on 2/15/2018.
 */

public class SaveMatchThread extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;
    NewGame newGame;
    SaveMatchThread(Context ctx){
        context = ctx;
    }
    @Override
    protected String doInBackground(String... params) {


        String type = params[0];
        String save_match_url = "http://ec2-34-229-88-91.compute-1.amazonaws.com/saveMatch.php";

        if(type.equals("SingleGame")){
            try {
                String currUID = params[1];
                String oppUID = params[2];
                String matchID = params[3];
                String currScore = params[4];
                String oppScore = params[5];
                String winner = params[6];
                String winnerUID = params[7];
                String loserUID = params[8];
                URL url = new URL(save_match_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("currUID","UTF-8")+"="+URLEncoder.encode(currUID,"UTF-8")+"&"
                        + URLEncoder.encode("oppUID","UTF-8")+"="+URLEncoder.encode(oppUID,"UTF-8")+"&"
                        + URLEncoder.encode("matchID","UTF-8")+"="+URLEncoder.encode(matchID,"UTF-8")+"&"
                        + URLEncoder.encode("currScore","UTF-8")+"="+URLEncoder.encode(currScore,"UTF-8")+"&"
                        + URLEncoder.encode("oppScore","UTF-8")+"="+URLEncoder.encode(oppScore,"UTF-8")+"&"
                        + URLEncoder.encode("matchType","UTF-8")+"="+URLEncoder.encode(type,"UTF-8")+"&"
                        + URLEncoder.encode("winner","UTF-8")+"="+URLEncoder.encode(winner,"UTF-8")+"&"
                        + URLEncoder.encode("winnerUID","UTF-8")+"="+URLEncoder.encode(winnerUID,"UTF-8")+"&"
                        + URLEncoder.encode("loserUID","UTF-8")+"="+URLEncoder.encode(loserUID,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line = bufferedReader.readLine())!=null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Loginin Status");
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
