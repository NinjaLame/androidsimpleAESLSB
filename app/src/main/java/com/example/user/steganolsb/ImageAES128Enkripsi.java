package com.example.user.steganolsb;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class ImageAES128Enkripsi {
    Bitmap imageBitmap;
    int Length;
    private List<Integer> pixels= new ArrayList<Integer>();
    private List<Integer> pixels_chiper= new ArrayList<Integer>();

    private String kunc;
    private String[][][] pesanHEX=null;
    private String[][] kunciHEX = new String[4][4];
    private String[][][] roundKey = new String[11][4][4];
    private int width;
    private int height;
    private int totalPixel;
    private int sisaBagi;
    private int jumlahBlokPesan;
    //constructor
    public ImageAES128Enkripsi(Bitmap imgBmp,String kunci){
        this.imageBitmap = imgBmp;

        width=imgBmp.getWidth();
        height=imgBmp.getHeight();
        totalPixel=width*height;
        sisaBagi=totalPixel%16;
        //mengubah kunci menjadi sepanjang plainmessage(gambar)
        char[] kunciArr= kunci.toCharArray();
        String kunciP= new String();

        for(int i=0;i<totalPixel;){
            int j;
            for(i=0, j=0;j<kunciArr.length&&i<totalPixel;j++){
                kunciP+=(kunciArr[j]);
                i++;
            }
        }

        this.kunc=kunciP;

        for(int i=0;i<imageBitmap.getWidth();i++){
            for(int j=0; j<imageBitmap.getHeight(); j++) {
                int s = imageBitmap.getPixel(i,j);
                int blue = Color.blue(s);
                pixels.add(blue);
            }
        }
        //pixel akan ditambah dengan 0 apabila bukan kelipatan 16
//        pixels=tambahPixel(pixels);

        jumlahBlokPesan=pixels.size()/16;
       // pesanHEX=pesanToMatrixHex(pixels);

//        kunciHEX = stringToMatrixHex(kunci);
    }





}
