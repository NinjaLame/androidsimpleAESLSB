package com.example.user.steganolsb;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

/*
* author: Riris Bayu Asrori
* NIM: A11.2015.09136
* */

public class LSB {
    Bitmap imageBitmap;
    int Length;
    int[] pixels;
    //constructor
    public LSB(Bitmap imgBmp){
        this.imageBitmap = imgBmp;
        int[] pixel = new int[imageBitmap.getWidth()*imageBitmap.getHeight()];
        int x=0;
        for(int i=0;i<imageBitmap.getWidth();i++){
            for(int j=0; j<imageBitmap.getHeight(); j++,x++) {
                int s = imageBitmap.getPixel(i,j);
                pixel[x] = Color.blue(s)%2;
            }
        }
        pixels = pixel;
    }

    //embed text to image
    @RequiresApi(api = Build.VERSION_CODES.N)
    public  Bitmap embeding(String plaintext){

        imageBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int[] binarray = this.toBinary(plaintext);
        int x =0;
        for(int i=0;i<imageBitmap.getWidth();i++){
            for(int j=0; j<imageBitmap.getHeight() && x<binarray.length; j++,x++) {
                int px = imageBitmap.getPixel(i,j);
                int alpha = Color.alpha(px);
                int red = Color.red(px);
                int green = Color.green(px);
                int blue = Color.blue(px);
                if(blue%2!=binarray[x]){
                    int temp = blue-1;
                    blue = temp;
                }

                int argb = Color.argb(alpha,red,green,blue);
                imageBitmap.setPixel(i,j, argb);

            }
        }
        return imageBitmap;
    }

    public int getLength(){
        return textLength(pixels);
    }
    //extract the lsb
    public String extract(){
        String plainText=binarytoString(pixels);
        return plainText;
    }

    //change bbinary to int
    public int bin2int(int bin[]){
        int n=0;
        int[] lookup={128,64,32,16,8,4,2,1};

        for(int i=0;i<bin.length;i++){
            if(bin[i]==1){
                n=lookup[i]+n;
            }else{
                n=0+n;
            }
        }
        return n;
    }
    //convert array of binary to string
    public String binarytoString(int[] bin){
        String text="";
        int size = textLength(bin);
        String[] n = new String[(size+1)];
        int x = 0;
        int z = 0;
        for(int i=0; i< (size+1)*8 ;i+=8,x++){
            String temp = "";
            for(int j=i;j<i+8 || z<size*8;j++,z++){
                temp += Integer.toString(bin[j]);
            }
            n[x]=temp;
        }
        for(int i=1;i<n.length;i++){
            int charCode = Integer.parseInt(n[i], 2);
            text += new Character((char)charCode).toString();

        }

        return text;
    }
    //convert number to binary 8bit
    public int[] int2bin(int n){
        int[] lookup={128,64,32,16,8,4,2,1};
        int[] bin=new int[lookup.length];
        for(int i=0;i<lookup.length;i++){
            if(n<lookup[i]){
                bin[i]=0;
            }else{
                bin[i]=1;
                n=n%lookup[i];
            }
            System.out.print(Integer.toString(bin[i]));
        }
        System.out.println("");

        return bin;
    }
    //convert string to binary
    @RequiresApi(api = Build.VERSION_CODES.N)
    public int[] toBinary(String str){
        int[] bin = new int[(str.length()*8)+8];
        int i,j,k; // iterasi;
        int[] lenInBin = int2bin(str.length());
        for(i=0;i<8;i++){
            bin[i]=lenInBin[i];
        }
        for(i=0;i<str.length();i++){
            int[] temp = int2bin((int)str.charAt(i));
            for(j=(i+1)*8,k=0;k<8;j++,k++){
                bin[j]=temp[k];

            }
        }
        return bin;

    }
    //get textLength from binarry array
    public int textLength(int[] bin ){
        int[] temp=new int[8];
        for(int i=0;i<8;i++){
            temp[i] = bin[i];
        }
        return bin2int(temp);
    }

}
