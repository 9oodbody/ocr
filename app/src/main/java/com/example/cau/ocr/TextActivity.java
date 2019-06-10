package com.example.cau.ocr;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
public class TextActivity extends AppCompatActivity {
    ImageView image_input;
    Bitmap bitmap;
    TessBaseAPI tessBaseAPI;
    TextView textview;
    private String m_strOcrResult;
    Bitmap image_output;
    ImageView imageout;

    Mat image;
    Mat m_matRoi;

    int imgWidth;
    int imgheight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        AssetManager assetmgr = this.getAssets();
        textview=findViewById(R.id.TextView);
        image_input = findViewById(R.id.imageViewInput);
        imageout = findViewById(R.id.imageViewOutput);
        InputStream inputStream = null;
        //Intent intent=getIntent();
        //Bitmap bm = (Bitmap)intent.getParcelableExtra("photo");

        try{
            inputStream = assetmgr.open("page.jpg");
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            //finish();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        if(inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        image_input.setImageBitmap(bitmap);
        tessBaseAPI = new TessBaseAPI();
        String dir = getFilesDir() + "/tesseract";
        if(checkLanguageFile(dir+"/tessdata"))
            tessBaseAPI.init(dir, "kor");

        usecv(bitmap);
    }


    boolean checkLanguageFile(String dir)
    {
        File file = new File(dir);
        if(!file.exists() && file.mkdirs())
            createFiles(dir);
        else if(file.exists()){
            String filePath = dir + "/eng.traineddata";
            File langDataFile = new File(filePath);
            if(!langDataFile.exists())
                createFiles(dir);
        }
        return true;
    }

    private void createFiles(String dir)
    {
        AssetManager assetMgr = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = assetMgr.open("kor.traineddata");

            String destFile = dir + "/kor.traineddata";

            outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    void usecv(Bitmap bitmap)
    {



         //Utils.bitmapToMat(bitmap ,image);
/*
        imgWidth= bitmap.getWidth();
        imgheight= bitmap.getHeight();

        Rect mRectRoi = new Rect(imgWidth/5+2,10,(imgWidth/5)-9,imgheight-10);
        m_matRoi = image.submat(mRectRoi);
        Imgproc.cvtColor(m_matRoi, m_matRoi, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.cvtColor(m_matRoi, m_matRoi, Imgproc.COLOR_GRAY2RGBA);
        m_matRoi.copyTo(image.submat(mRectRoi));
        m_matRoi = image;
*/
        //m_matRoi = image;
        //Utils.matToBitmap(m_matRoi,image_output);

        imageout.setImageBitmap(bitmap);

        new AsyncTess().execute(bitmap);


    }

    private class AsyncTess extends AsyncTask<Bitmap, Integer, String> {
        @Override
        protected String doInBackground(Bitmap... mRelativeParams) {
            tessBaseAPI.setImage(mRelativeParams[0]);
            return tessBaseAPI.getUTF8Text();
        }


        protected void onPostExecute(String result) {


            textview.setText(result);

        }
    }
}

