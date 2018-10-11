package com.saganet.encuestas;

/**
 * Created by Hazael on 20/10/2016.
 */

public class tUsuarios {
    private  int pKey;
    private  String Nick;
    private  String Password;
    private  String Nombre;
    private  String Pat;
    private  String Mat;
    private  int Version;
    public  tUsuarios() {}
    public  tUsuarios(int key, String nick, String nom, String pat, String mat, String pass,int v) {
        this.pKey=key; this.Nick= nick; this.Nombre= nom; this.Pat= pat; this.Mat= mat; this.Password= pass; this.Version= v;
    }

    public int getpKey() {
        return pKey;
    }

    public String getNick() {
        return Nick;
    }

    public String getPassword() {
        return Password;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getMat() {
        return Mat;
    }

    public String getPat() {
        return Pat;
    }

    public int getVersion() {
        return Version;
    }
}
