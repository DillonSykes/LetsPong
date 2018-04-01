package dsykes.letspong;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
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
 * Created by jaydo on 3/17/2018.
 */

public class CreateNewMatchInDatabase extends AsyncTask<String,String,String> {
    Context context;
    public CreateNewMatchInDatabase(Context ctx){
        context = ctx;
    }
    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
    @Override
    protected String doInBackground(String... params) {


        String type = params[0];
        String login_url = "http://ec2-34-229-88-91.compute-1.amazonaws.com/newMatch.php";

        if(type.equals("newMatch")){
            try {
                String player1UID = params[1];
                String player2UID = params[2];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("player1UID","UTF-8")+"="+URLEncoder.encode(player1UID,"UTF-8")+"&"
                        + URLEncoder.encode("player2UID","UTF-8")+"="+URLEncoder.encode(player2UID,"UTF-8");
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
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());

                String s = getStringFromInputStream(in);
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
        //alertDialog = new AlertDialog.Builder(context).create();
        //alertDialog.setTitle("Loginin Status");
    }

    @Override
    protected void onPostExecute(String result) {
        //alertDialog.setMessage(result);
        //alertDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }
}
