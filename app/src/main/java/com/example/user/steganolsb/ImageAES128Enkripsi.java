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
    private List<Integer> pixels= new ArrayList<Integer>();//blue
    private List<Integer> pixels_chiper= new ArrayList<Integer>();//blue

    private List<Integer> pixels_red= new ArrayList<Integer>();
    private List<Integer> pixels_green= new ArrayList<Integer>();
    private List<Integer> getPixels_chiper_green= new ArrayList<Integer>();
    private List<Integer> pixels_chiper_red= new ArrayList<Integer>();

    private String kunc;
    private String[][][] pesanHEX=null;//blue
    private String[][][] pesanHEXRed=null;
    private String[][][] pesanHEXGreen=null;
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
        this.kunc=kunci;
        width=imgBmp.getWidth();
        height=imgBmp.getHeight();
        totalPixel=width*height;
        sisaBagi=totalPixel%16;

        for(int i=0;i<imageBitmap.getWidth();i++){
            for(int j=0; j<imageBitmap.getHeight(); j++) {
                int s = imageBitmap.getPixel(i,j);
                int blue = Color.blue(s);
                int red = Color.red(s);
                int green = Color.green(s);
                pixels.add(blue);
                pixels_red.add(red);
                pixels_green.add(green);
            }
        }
        //pixel akan ditambah dengan 0 apabila bukan kelipatan 16
        pixels=tambahPixel(pixels);
        pixels_red=tambahPixel(pixels_red);
        pixels_green=tambahPixel(pixels_green);

        jumlahBlokPesan=pixels.size()/16;
        pesanHEX=pesanToMatrixHex(pixels);
        pesanHEXGreen=pesanToMatrixHex(pixels_green);
        pesanHEXRed=pesanToMatrixHex(pixels_red);

        kunciHEX = stringToMatrixHex(kunci);
    }

    static final String LOOKUPMC02[] = {
            "00","02","04","06","08","0A","0C","0E","10","12","14","16","18","1A","1C","1E",
            "20","22","24","26","28","2A","2C","2E","30","32","34","36","38","3A","3C","3E",
            "40","42","44","46","48","4A","4C","4E","50","52","54","56","58","5A","5C","5E",
            "60","62","64","66","68","6A","6C","6E","70","72","74","76","78","7A","7C","7E",
            "80","82","84","86","88","8A","8C","8E","90","92","94","96","98","9A","9C","9E",
            "A0","A2","A4","A6","A8","AA","AC","AE","B0","B2","B4","B6","B8","BA","BC","BE",
            "C0","C2","C4","C6","C8","CA","CC","CE","D0","D2","D4","D6","D8","DA","DC","DE",
            "E0","E2","E4","E6","E8","EA","EC","EE","F0","F2","F4","F6","F8","FA","FC","FE",
            "1B","19","1F","1D","13","11","17","15","0B","09","0F","0D","03","01","07","05",
            "3B","39","3F","3D","33","31","37","35","2B","29","2F","2D","23","21","27","25",
            "5B","59","5F","5D","53","51","57","55","4B","49","4F","4D","43","41","47","45",
            "7B","79","7F","7D","73","71","77","75","6B","69","6F","6D","63","61","67","65",
            "9B","99","9F","9D","93","91","97","95","8B","89","8F","8D","83","81","87","85",
            "BB","B9","BF","BD","B3","B1","B7","B5","AB","A9","AF","AD","A3","A1","A7","A5",
            "DB","D9","DF","DD","D3","D1","D7","D5","CB","C9","CF","CD","C3","C1","C7","C5",
            "FB","F9","FF","FD","F3","F1","F7","F5","EB","E9","EF","ED","E3","E1","E7","E5"
    };
    static final String LOOKUPMC03[] = {
            "00","03","06","05","0C","0F","0A","09","18","1B","1E","1D","14","17","12","11",
            "30","33","36","35","3C","3F","3A","39","28","2B","2E","2D","24","27","22","21",
            "60","63","66","65","6C","6F","6A","69","78","7B","7E","7D","74","77","72","71",
            "50","53","56","55","5C","5F","5A","59","48","4B","4E","4D","44","47","42","41",
            "C0","C3","C6","C5","CC","CF","CA","C9","D8","DB","DE","DD","D4","D7","D2","D1",
            "F0","F3","F6","F5","FC","FF","FA","F9","E8","EB","EE","ED","E4","E7","E2","E1",
            "A0","A3","A6","A5","AC","AF","AA","A9","B8","BB","BE","BD","B4","B7","B2","B1",
            "90","93","96","95","9C","9F","9A","99","88","8B","8E","8D","84","87","82","81",
            "9B","98","9D","9E","97","94","91","92","83","80","85","86","8F","8C","89","8A",
            "AB","A8","AD","AE","A7","A4","A1","A2","B3","B0","B5","B6","BF","BC","B9","BA",
            "FB","F8","FD","FE","F7","F4","F1","F2","E3","E0","E5","E6","EF","EC","E9","EA",
            "CB","C8","CD","CE","C7","C4","C1","C2","D3","D0","D5","D6","DF","DC","D9","DA",
            "5B","58","5D","5E","57","54","51","52","43","40","45","46","4F","4C","49","4A",
            "6B","68","6D","6E","67","64","61","62","73","70","75","76","7F","7C","79","7A",
            "3B","38","3D","3E","37","34","31","32","23","20","25","26","2F","2C","29","2A",
            "0B","08","0D","0E","07","04","01","02","13","10","15","16","1F","1C","19","1A"
    };
    static final String LOOKUPMC09[] = {
            "00","09","12","1b","24","2d","36","3f","48","41","5a","53","6c","65","7e","77",
            "90","99","82","8b","b4","bd","a6","af","d8","d1","ca","c3","fc","f5","ee","e7",
            "3b","32","29","20","1f","16","0d","04","73","7a","61","68","57","5e","45","4c",
            "ab","a2","b9","b0","8f","86","9d","94","e3","ea","f1","f8","c7","ce","d5","dc",
            "76","7f","64","6d","52","5b","40","49","3e","37","2c","25","1a","13","08","01",
            "e6","ef","f4","fd","c2","cb","d0","d9","ae","a7","bc","b5","8a","83","98","91",
            "4d","44","5f","56","69","60","7b","72","05","0c","17","1e","21","28","33","3a",
            "dd","d4","cf","c6","f9","f0","eb","e2","95","9c","87","8e","b1","b8","a3","aa",
            "ec","e5","fe","f7","c8","c1","da","d3","a4","ad","b6","bf","80","89","92","9b",
            "7c","75","6e","67","58","51","4a","43","34","3d","26","2f","10","19","02","0b",
            "d7","de","c5","cc","f3","fa","e1","e8","9f","96","8d","84","bb","b2","a9","a0",
            "47","4e","55","5c","63","6a","71","78","0f","06","1d","14","2b","22","39","30",
            "9a","93","88","81","be","b7","ac","a5","d2","db","c0","c9","f6","ff","e4","ed",
            "0a","03","18","11","2e","27","3c","35","42","4b","50","59","66","6f","74","7d",
            "a1","a8","b3","ba","85","8c","97","9e","e9","e0","fb","f2","cd","c4","df","d6",
            "31","38","23","2a","15","1c","07","0e","79","70","6b","62","5d","54","4f","46"
    };
    static final String LOOKUPMC11[] = {
            "00","0b","16","1d","2c","27","3a","31","58","53","4e","45","74","7f","62","69",
            "b0","bb","a6","ad","9c","97","8a","81","e8","e3","fe","f5","c4","cf","d2","d9",
            "7b","70","6d","66","57","5c","41","4a","23","28","35","3e","0f","04","19","12",
            "cb","c0","dd","d6","e7","ec","f1","fa","93","98","85","8e","bf","b4","a9","a2",
            "f6","fd","e0","eb","da","d1","cc","c7","ae","a5","b8","b3","82","89","94","9f",
            "46","4d","50","5b","6a","61","7c","77","1e","15","08","03","32","39","24","2f",
            "8d","86","9b","90","a1","aa","b7","bc","d5","de","c3","c8","f9","f2","ef","e4",
            "3d","36","2b","20","11","1a","07","0c","65","6e","73","78","49","42","5f","54",
            "f7","fc","e1","ea","db","d0","cd","c6","af","a4","b9","b2","83","88","95","9e",
            "47","4c","51","5a","6b","60","7d","76","1f","14","09","02","33","38","25","2e",
            "8c","87","9a","91","a0","ab","b6","bd","d4","df","c2","c9","f8","f3","ee","e5",
            "3c","37","2a","21","10","1b","06","0d","64","6f","72","79","48","43","5e","55",
            "01","0a","17","1c","2d","26","3b","30","59","52","4f","44","75","7e","63","68",
            "b1","ba","a7","ac","9d","96","8b","80","e9","e2","ff","f4","c5","ce","d3","d8",
            "7a","71","6c","67","56","5d","40","4b","22","29","34","3f","0e","05","18","13",
            "ca","c1","dc","d7","e6","ed","f0","fb","92","99","84","8f","be","b5","a8","a3"
    };
    static final String LOOKUPMC13[] = {
            "00","0d","1a","17","34","39","2e","23","68","65","72","7f","5c","51","46","4b",
            "d0","dd","ca","c7","e4","e9","fe","f3","b8","b5","a2","af","8c","81","96","9b",
            "bb","b6","a1","ac","8f","82","95","98","d3","de","c9","c4","e7","ea","fd","f0",
            "6b","66","71","7c","5f","52","45","48","03","0e","19","14","37","3a","2d","20",
            "6d","60","77","7a","59","54","43","4e","05","08","1f","12","31","3c","2b","26",
            "bd","b0","a7","aa","89","84","93","9e","d5","d8","cf","c2","e1","ec","fb","f6",
            "d6","db","cc","c1","e2","ef","f8","f5","be","b3","a4","a9","8a","87","90","9d",
            "06","0b","1c","11","32","3f","28","25","6e","63","74","79","5a","57","40","4d",
            "da","d7","c0","cd","ee","e3","f4","f9","b2","bf","a8","a5","86","8b","9c","91",
            "0a","07","10","1d","3e","33","24","29","62","6f","78","75","56","5b","4c","41",
            "61","6c","7b","76","55","58","4f","42","09","04","13","1e","3d","30","27","2a",
            "b1","bc","ab","a6","85","88","9f","92","d9","d4","c3","ce","ed","e0","f7","fa",
            "b7","ba","ad","a0","83","8e","99","94","df","d2","c5","c8","eb","e6","f1","fc",
            "67","6a","7d","70","53","5e","49","44","0f","02","15","18","3b","36","21","2c",
            "0c","01","16","1b","38","35","22","2f","64","69","7e","73","50","5d","4a","47",
            "dc","d1","c6","cb","e8","e5","f2","ff","b4","b9","ae","a3","80","8d","9a","97"
    };
    static final String LOOKUPMC14[] = {
            "00","0e","1c","12","38","36","24","2a","70","7e","6c","62","48","46","54","5a",
            "e0","ee","fc","f2","d8","d6","c4","ca","90","9e","8c","82","a8","a6","b4","ba",
            "db","d5","c7","c9","e3","ed","ff","f1","ab","a5","b7","b9","93","9d","8f","81",
            "3b","35","27","29","03","0d","1f","11","4b","45","57","59","73","7d","6f","61",
            "ad","a3","b1","bf","95","9b","89","87","dd","d3","c1","cf","e5","eb","f9","f7",
            "4d","43","51","5f","75","7b","69","67","3d","33","21","2f","05","0b","19","17",
            "76","78","6a","64","4e","40","52","5c","06","08","1a","14","3e","30","22","2c",
            "96","98","8a","84","ae","a0","b2","bc","e6","e8","fa","f4","de","d0","c2","cc",
            "41","4f","5d","53","79","77","65","6b","31","3f","2d","23","09","07","15","1b",
            "a1","af","bd","b3","99","97","85","8b","d1","df","cd","c3","e9","e7","f5","fb",
            "9a","94","86","88","a2","ac","be","b0","ea","e4","f6","f8","d2","dc","ce","c0",
            "7a","74","66","68","42","4c","5e","50","0a","04","16","18","32","3c","2e","20",
            "ec","e2","f0","fe","d4","da","c8","c6","9c","92","80","8e","a4","aa","b8","b6",
            "0c","02","10","1e","34","3a","28","26","7c","72","60","6e","44","4a","58","56",
            "37","39","2b","25","0f","01","13","1d","47","49","5b","55","7f","71","63","6d",
            "d7","d9","cb","c5","ef","e1","f3","fd","a7","a9","bb","b5","9f","91","83","8d"
    };
    static final String[][] SBOX =  {
            {"63","7C","77","7B","F2","6B","6F","C5","30","01","67","2B","FE","D7","AB","76"},
            {"CA","82","C9","7D","FA","59","47","F0","AD","D4","A2","AF","9C","A4","72","C0"},
            {"B7","FD","93","26","36","3F","F7","CC","34","A5","E5","F1","71","D8","31","15"},
            {"04","C7","23","C3","18","96","05","9A","07","12","80","E2","EB","27","B2","75"},
            {"09","83","2C","1A","1B","6E","5A","A0","52","3B","D6","B3","29","E3","2F","84"},
            {"53","D1","00","ED","20","FC","B1","5B","6A","CB","BE","39","4A","4C","58","CF"},
            {"D0","EF","AA","FB","43","4D","33","85","45","F9","02","7F","50","3C","9F","A8"},
            {"51","A3","40","8F","92","9D","38","F5","BC","B6","DA","21","10","FF","F3","D2"},
            {"CD","0C","13","EC","5F","97","44","17","C4","A7","7E","3D","64","5D","19","73"},
            {"60","81","4F","DC","22","2A","90","88","46","EE","B8","14","DE","5E","0B","DB"},
            {"E0","32","3A","0A","49","06","24","5C","C2","D3","AC","62","91","95","E4","79"},
            {"E7","C8","37","6D","8D","D5","4E","A9","6C","56","F4","EA","65","7A","AE","08"},
            {"BA","78","25","2E","1C","A6","B4","C6","E8","DD","74","1F","4B","BD","8B","8A"},
            {"70","3E","B5","66","48","03","F6","0E","61","35","57","B9","86","C1","1D","9E"},
            {"E1","F8","98","11","69","D9","8E","94","9B","1E","87","E9","CE","55","28","DF"},
            {"8C","A1","89","0D","BF","E6","42","68","41","99","2D","0F","B0","54","BB","16"}
    };

    static final String[][] MATRIXMC =   {
            {"02","03","01","01"},
            {"01","02","03","01"},
            {"01","01","02","03"},
            {"03","01","01","02"}
    };

    static final String[][] RCON = {
            {"01","02","04","08","10","20","40","80","1B","36"},
            {"00","00","00","00","00","00","00","00","00","00"},
            {"00","00","00","00","00","00","00","00","00","00"},
            {"00","00","00","00","00","00","00","00","00","00"}
    };


    @RequiresApi(api = Build.VERSION_CODES.N)
    public  Bitmap generate(){

        imageBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int a =0;
        for(int i=0;i<jumlahBlokPesan;i++){
            for(int y=0; y<4; y++){
                for(int x=0; x<4; x++){
                    pixels_chiper.add((int)Integer.parseInt(pesanHEX[i][x][y],16 ));
                    pixels_chiper_red.add((int)Integer.parseInt(pesanHEXRed[i][x][y],16 ));
                }
            }
        }

        for(int i=0;i<width;i++){
            for(int j=0; j<height ; j++) {
                int px = imageBitmap.getPixel(i,j);
                int alpha = Color.alpha(px);
                int red = Color.red(pixels_chiper_red.get(a));
                int green = Color.green(px);
                int blue = Color.blue(pixels_chiper.get(a));

                int argb = Color.argb(alpha,red,green,blue);
                imageBitmap.setPixel(i,j, argb);
                a++;

            }
        }
        return imageBitmap;
    }


    //menambahkan spasi apaila karakter kurang dari 16
    private List<Integer> tambahPixel(List<Integer> pixelPesan){
        List<Integer> Pesan=pixelPesan;
        if(sisaBagi!=0){//apabila tidak kelipatan 16
            int tambahPix=16-sisaBagi;
            for(int i=1;i<=tambahPix;i++){
                pixelPesan.add(0);
            }
        }

        return Pesan;
    }

    //menecek pesan to matrikHex
    public String getChiper(){
        String pesan="";
        for(int i=0;i<jumlahBlokPesan;i++){
            for(int y=0; y<4; y++){
                for(int x=0; x<4; x++){
                    pesan+=(char)Integer.parseInt(pesanHEX[i][x][y],16 );

                }
            }
        }
        return pesan;
    }

    private String[][][] pesanToMatrixHex(List<Integer> pix){
        int c=0;
        String[][][] matrixHEX = new String[jumlahBlokPesan][4][4];

        for(int i=0;i<jumlahBlokPesan;i++){
            for(int y=0; y<4; y++){
                for(int x=0; x<4; x++){

                    String hex = String.format("%02x", pix.get(c));
                    matrixHEX[i][x][y] = hex.toUpperCase();
                    c++;
                }
            }
        }

        return matrixHEX;
    }

    private String[][] stringToMatrixHex(String string16){
        int c=0;
        String[][] matrixHEX = new String[4][4];
        for(int y=0; y<4; y++){
            for(int x=0; x<4; x++){
                String hex = String.format("%02x", (int) string16.charAt(c++));
                matrixHEX[x][y] = hex.toUpperCase();
            }
        }
        return matrixHEX;
    }

    private void generateRoundKey(){
        String[] rotWord = new String[4];
        roundKey[0] = kunciHEX;
        for(int c=1;c<=10;c++){
            rotWord[0] = roundKey[c-1][0][3];
            rotWord[1] = roundKey[c-1][1][3];
            rotWord[2] = roundKey[c-1][2][3];
            rotWord[3] = roundKey[c-1][3][3];
            rotWord = geser(rotWord,1);
            for (int x=0;x<4;x++){
                rotWord[x] = SBOX[ Integer.parseInt(Character.toString(rotWord[x].charAt(0)), 16)][ Integer.parseInt(Character.toString(rotWord[x].charAt(1)), 16)];
            }

            roundKey[c][0][0] = xor3StringHex(roundKey[c-1][0][0], rotWord[0], RCON[0][c-1]);
            roundKey[c][1][0] = xor3StringHex(roundKey[c-1][1][0], rotWord[1], RCON[1][c-1]);
            roundKey[c][2][0] = xor3StringHex(roundKey[c-1][2][0], rotWord[2], RCON[2][c-1]);
            roundKey[c][3][0] = xor3StringHex(roundKey[c-1][3][0], rotWord[3], RCON[3][c-1]);

            roundKey[c][0][1] = xorStringHex(roundKey[c-1][0][1], roundKey[c][0][0]);
            roundKey[c][1][1] = xorStringHex(roundKey[c-1][1][1], roundKey[c][1][0]);
            roundKey[c][2][1] = xorStringHex(roundKey[c-1][2][1], roundKey[c][2][0]);
            roundKey[c][3][1] = xorStringHex(roundKey[c-1][3][1], roundKey[c][3][0]);

            roundKey[c][0][2] = xorStringHex(roundKey[c-1][0][2], roundKey[c][0][1]);
            roundKey[c][1][2] = xorStringHex(roundKey[c-1][1][2], roundKey[c][1][1]);
            roundKey[c][2][2] = xorStringHex(roundKey[c-1][2][2], roundKey[c][2][1]);
            roundKey[c][3][2] = xorStringHex(roundKey[c-1][3][2], roundKey[c][3][1]);

            roundKey[c][0][3] = xorStringHex(roundKey[c-1][0][3], roundKey[c][0][2]);
            roundKey[c][1][3] = xorStringHex(roundKey[c-1][1][3], roundKey[c][1][2]);
            roundKey[c][2][3] = xorStringHex(roundKey[c-1][2][3], roundKey[c][2][2]);
            roundKey[c][3][3] = xorStringHex(roundKey[c-1][3][3], roundKey[c][3][2]);

        }
    }

    private String[] geser(String[] a, int n){
        String temp;
        for (int d=0;d<n;d++){ // geser 1x
            temp = a[0]; // ambil kotak pertama
            for (int c= 0;c<3;c++){ // geser tiga kotak
                a[c] = a[c+1];
            }
            a[3] = temp; // kotak ke 4 di isi yang tadi di simpan
        }
        return a;
    }

    private String xor3StringHex(String a,String b,String c){
        int n1 = Integer.parseInt(a, 16);
        int n2 = Integer.parseInt(b, 16);
        int n3 = Integer.parseInt(c, 16);
        int n4 = n1 ^ n2 ^ n3;
        String d = String.format("%02x", n4);
        return d.toUpperCase();
    }
    private String xorStringHex(String a, String b){
        int n1 = Integer.parseInt(a, 16);
        int n2 = Integer.parseInt(b, 16);
        int n3 = n1 ^ n2;
        String c = String.format("%02x", n3);
        return c.toUpperCase();
    }

    private void addRoundKey(int n){
        for(int i=0;i<jumlahBlokPesan;i++){
            for(int y=0; y<4; y++){
                for(int x=0; x<4; x++){
                    pesanHEX[i][y][x] = xorStringHex(pesanHEX[i][y][x],roundKey[n][y][x]);
                    pesanHEXRed[i][y][x] = xorStringHex(pesanHEXRed[i][y][x],roundKey[n][y][x]);
                }
            }
        }
    }

    private void subBytes(){
        for(int i=0;i<jumlahBlokPesan;i++){
            for (int y=0;y<4;y++){
                for (int x=0;x<4;x++){
                    pesanHEX[i][y][x] = SBOX[ Integer.parseInt(Character.toString(pesanHEX[i][y][x].charAt(0)), 16)][ Integer.parseInt(Character.toString(pesanHEX[i][y][x].charAt(1)), 16)];
                    pesanHEXRed[i][y][x] = SBOX[ Integer.parseInt(Character.toString(pesanHEXRed[i][y][x].charAt(0)), 16)][ Integer.parseInt(Character.toString(pesanHEXRed[i][y][x].charAt(1)), 16)];
                }
            }
        }
    }

    private void shiftRows(){
        for(int i=0;i<jumlahBlokPesan;i++){
            pesanHEX[i][1] = geser(pesanHEX[i][1],1);
            pesanHEX[i][2] = geser(pesanHEX[i][2],2);
            pesanHEX[i][3] = geser(pesanHEX[i][3],3);

            pesanHEXRed[i][1] = geser(pesanHEXRed[i][1],1);
            pesanHEXRed[i][2] = geser(pesanHEXRed[i][2],2);
            pesanHEXRed[i][3] = geser(pesanHEXRed[i][3],3);
        }
    }

    private void mixColumn(){
        String[] kolomHasil = new String[4];
        String[] kolomHasilRed = new String[4];
        for(int i=0;i<jumlahBlokPesan;i++){
            for(int c=0;c<4;c++){
                kolomHasil[0] =  xor4StringHex   (
                        lookUp(pesanHEX[i][0][c],MATRIXMC[0][0]),
                        lookUp(pesanHEX[i][1][c],MATRIXMC[0][1]),
                        lookUp(pesanHEX[i][2][c],MATRIXMC[0][2]),
                        lookUp(pesanHEX[i][3][c],MATRIXMC[0][3])
                );
                kolomHasil[1] =  xor4StringHex   (
                        lookUp(pesanHEX[i][0][c],MATRIXMC[1][0]),
                        lookUp(pesanHEX[i][1][c],MATRIXMC[1][1]),
                        lookUp(pesanHEX[i][2][c],MATRIXMC[1][2]),
                        lookUp(pesanHEX[i][3][c],MATRIXMC[1][3])
                );
                kolomHasil[2] =  xor4StringHex   (
                        lookUp(pesanHEX[i][0][c],MATRIXMC[2][0]),
                        lookUp(pesanHEX[i][1][c],MATRIXMC[2][1]),
                        lookUp(pesanHEX[i][2][c],MATRIXMC[2][2]),
                        lookUp(pesanHEX[i][3][c],MATRIXMC[2][3])
                );
                kolomHasil[3] =  xor4StringHex   (
                        lookUp(pesanHEX[i][0][c],MATRIXMC[3][0]),
                        lookUp(pesanHEX[i][1][c],MATRIXMC[3][1]),
                        lookUp(pesanHEX[i][2][c],MATRIXMC[3][2]),
                        lookUp(pesanHEX[i][3][c],MATRIXMC[3][3])
                );

                kolomHasilRed[0] =  xor4StringHex   (
                        lookUp(pesanHEXRed[i][0][c],MATRIXMC[0][0]),
                        lookUp(pesanHEXRed[i][1][c],MATRIXMC[0][1]),
                        lookUp(pesanHEXRed[i][2][c],MATRIXMC[0][2]),
                        lookUp(pesanHEXRed[i][3][c],MATRIXMC[0][3])
                );
                kolomHasilRed[1] =  xor4StringHex   (
                        lookUp(pesanHEXRed[i][0][c],MATRIXMC[1][0]),
                        lookUp(pesanHEXRed[i][1][c],MATRIXMC[1][1]),
                        lookUp(pesanHEXRed[i][2][c],MATRIXMC[1][2]),
                        lookUp(pesanHEXRed[i][3][c],MATRIXMC[1][3])
                );
                kolomHasilRed[2] =  xor4StringHex   (
                        lookUp(pesanHEXRed[i][0][c],MATRIXMC[2][0]),
                        lookUp(pesanHEXRed[i][1][c],MATRIXMC[2][1]),
                        lookUp(pesanHEXRed[i][2][c],MATRIXMC[2][2]),
                        lookUp(pesanHEXRed[i][3][c],MATRIXMC[2][3])
                );
                kolomHasilRed[3] =  xor4StringHex   (
                        lookUp(pesanHEXRed[i][0][c],MATRIXMC[3][0]),
                        lookUp(pesanHEXRed[i][1][c],MATRIXMC[3][1]),
                        lookUp(pesanHEXRed[i][2][c],MATRIXMC[3][2]),
                        lookUp(pesanHEXRed[i][3][c],MATRIXMC[3][3])
                );
                //blue
                pesanHEX[i][0][c] = kolomHasil[0];
                pesanHEX[i][1][c] = kolomHasil[1];
                pesanHEX[i][2][c] = kolomHasil[2];
                pesanHEX[i][3][c] = kolomHasil[3];
                //red
                pesanHEXRed[i][0][c] = kolomHasilRed[0];
                pesanHEXRed[i][1][c] = kolomHasilRed[1];
                pesanHEXRed[i][2][c] = kolomHasilRed[2];
                pesanHEXRed[i][3][c] = kolomHasilRed[3];
            }
        }
    }

    private String lookUp(String stringHEX, String kode){
        String hasil = "";
        switch (kode) {
            case "01":
                hasil = stringHEX;
                break;
            case "02":
                hasil = LOOKUPMC02[Integer.parseInt(stringHEX, 16)];
                break;
            case "03":
                hasil = LOOKUPMC03[Integer.parseInt(stringHEX, 16)];
                break;
            case "09":
                hasil = LOOKUPMC09[Integer.parseInt(stringHEX, 16)];
                break;
            case "11":
                hasil = LOOKUPMC11[Integer.parseInt(stringHEX, 16)];
                break;
            case "13":
                hasil = LOOKUPMC13[Integer.parseInt(stringHEX, 16)];
                break;
            case "14":
                hasil = LOOKUPMC14[Integer.parseInt(stringHEX, 16)];
                break;
            default:
                break;
        }
        return hasil;
    }

    private String xor4StringHex(String a,String b,String c,String d){
        int n1 = Integer.parseInt(a, 16);
        int n2 = Integer.parseInt(b, 16);
        int n3 = Integer.parseInt(c, 16);
        int n4 = Integer.parseInt(d, 16);
        int n5 = n1 ^ n2 ^ n3 ^ n4;
        String f = String.format("%02x", n5);
        return f.toUpperCase();
    }

    public void enkripsi(){
        generateRoundKey();
        addRoundKey(0);
        for (int c=1;c<10;c++){
            subBytes();
            shiftRows();
            mixColumn();
            addRoundKey(c);
        }
        subBytes();
        shiftRows();
        addRoundKey(10);
    }

}
