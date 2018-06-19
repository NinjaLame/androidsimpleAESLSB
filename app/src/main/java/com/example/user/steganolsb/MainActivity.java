package com.example.user.steganolsb;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;


public class MainActivity extends AppCompatActivity {
    TextView cTeks;
    EditText pTeks, kTeks;
    String str = "Hello World";
    String teks;
    Image image;
    String uri;
    String pathFile;
    String nameFile;
    private ArrayList<String> filePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.text = (TextView)findViewById(R.id.Uri);
        setContentView(R.layout.activity_main);
        pTeks = (EditText) findViewById(R.id.pText);
        kTeks = (EditText) findViewById(R.id.kText);
        cTeks = (TextView) findViewById(R.id.chiperText);
    }

    public void galery(View view) {
        DialogProperties properties = new DialogProperties();

        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File("/");
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        FilePickerDialog dialog = new FilePickerDialog(MainActivity.this,properties);
        dialog.setTitle("Select a File");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                Toast.makeText(MainActivity.this, files[0], Toast.LENGTH_SHORT).show();
                uri = files[0];
                //files is the array of the paths of files selected by the Application User.
                String[] name = files[0].split("/");
                String path="";
                for(int i = 0; i<name.length-1;i++){
                    path = path+name[i]+"/";
                }

                pathFile = path;
                nameFile = name[name.length-1];
            }
        });

        dialog.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void outLSB(View view){
        Bitmap myBitmap = BitmapFactory.decodeFile(uri);
        myBitmap = myBitmap.copy( Bitmap.Config.ARGB_8888 , true);
//        ProgressDialog.show(this, "Loading", "Wait while pixels changes...");
        LSB t = new LSB(myBitmap);
        myBitmap = t.embeding(teks);
//        Toast.makeText(this, te.length(), Toast.LENGTH_SHORT).show();
        String FilePath = pathFile;

        try {
            File f = new File(FilePath, "2"+nameFile+".png");
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
            byte[] bitmapdata = bos.toByteArray();
            try {
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                //Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
            }catch (FileNotFoundException e){
                Toast.makeText(this, "Gagal create file", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "gagal baca file", Toast.LENGTH_SHORT).show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void extractLSB(View view) {
        Bitmap myBitmap = BitmapFactory.decodeFile(uri);
        myBitmap = myBitmap.copy( Bitmap.Config.ARGB_8888 , true);
//        String str=new String();
//        for(int i=0;i<8;i++){
//            str = str+Integer.toString(Color.blue(myBitmap.getPixel(0,i))%2);
//        }
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
//        ProgressDialog.show(this, "Loading", "Wait while pixels changes...");
        LSB t = new LSB(myBitmap);
//        myBitmap = t.embeding("hello");
//        String ln = Integer.toString(t.getLength());
        pTeks.setText(t.extract());
        Toast.makeText(this, t.extract() , Toast.LENGTH_SHORT).show();
    }

    public void encryptAES(View view) {
        String plain = pTeks.getText().toString();
        String key = kTeks.getText().toString();
        AES128 aes = new AES128(plain,key);
        aes.generateRoundKey();
        aes.enkripsi();

        teks = aes.getPesan();
        cTeks.setText(teks);
        Toast.makeText(this, teks, Toast.LENGTH_SHORT).show();
    }

    public void decryptAES(View view) {
        String plain = pTeks.getText().toString();
        String key = kTeks.getText().toString();
        AES128 aes = new AES128(plain,key);
        aes.generateRoundKey();
        aes.dekripsi();

        teks = aes.getPesan();
        cTeks.setText(teks);
        Toast.makeText(this, teks, Toast.LENGTH_SHORT).show();
    }
}
