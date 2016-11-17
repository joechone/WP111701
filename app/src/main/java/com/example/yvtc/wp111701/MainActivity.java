package com.example.yvtc.wp111701;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    TextView tv;
    ProgressBar pb;
    ProgressDialog pd;
    MyTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView2);
        img = (ImageView) findViewById(R.id.imageView);
        pb = (ProgressBar) findViewById(R.id.progressBar5);
        pd = new ProgressDialog(MainActivity.this);
        pd.setTitle("下載中...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCanceledOnTouchOutside(false);
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.cancel(false);
            }
        });
    }
    public void click1(View v)
    {
        if(et.getText().toString().trim()!="" ) {
            task = new MyTask();
            task.execute("http://www.drodd.com/images14/flower26.jpg");
            pb.setVisibility(View.VISIBLE);
            tv.setVisibility(View.VISIBLE);
            img.setVisibility(View.INVISIBLE);
        }
        else {


        }
    }
    class MyTask extends AsyncTask<String, Integer, Bitmap>
    {
        private Bitmap bitmap = null;
        private InputStream inputStream = null;
        private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        @Override
        protected Bitmap doInBackground(String... params) {


            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                inputStream = conn.getInputStream();
                double fullSize = conn.getContentLength(); // 總長度
                byte[] buffer = new byte[64]; // buffer ( 每次讀取長度 )
                int readSize = 0; // 當下讀取長度
                int readAllSize = 0;
                double sum = 0;
                while ((readSize = inputStream.read(buffer)) != -1)
                {
                    outputStream.write(buffer, 0, readSize);
                    readAllSize += readSize;
                    sum = (readAllSize / fullSize) * 100; // 累計讀取進度
                    publishProgress((int)sum);
                    // Message message = handler.obtainMessage(1, sum);
                    // handler.sendMessage(message);

                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] result = outputStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
            return bitmap;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tv.setText(String.valueOf(values[0])+"%");
            pb.setProgress(values[0]);
            pd.setProgress(values[0]);
            if(values[0]==100) {
                pb.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.INVISIBLE);
                img.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            pd.dismiss();
            img.setImageBitmap(bitmap);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }
    }
}
