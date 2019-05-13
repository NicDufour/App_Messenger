package com.example.messenger.Models;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

import javax.annotation.Nullable;

public class User {

    private String uid;
    private String name;
    private String eMail;
    private String genre;
    private GeoPoint location;
    @Nullable private String dateNassance;
    @Nullable private String numTel;
    @Nullable private String ville;
    @Nullable private String pays;
    @Nullable private String urlPicture;
    @Nullable private List<Friend> friends;
    private Boolean autoriseLocation;

    public User()
    {

    }

    public User(String _uid, String _name, String _eMail, String _genre)
    {
        this.uid = _uid;
        this.name = _name;
        this.eMail = _eMail;
        this.genre= _genre;
        this.location = new GeoPoint(0,0);
        this.autoriseLocation = false;
        this.dateNassance = null;
        this.numTel = null;
        this.ville = null;
        this.pays= null;
        this.location = null;
        this.urlPicture = null;
        this.friends = null;
    }

    public User(String _uid, String _name, String _eMail, String _genre, GeoPoint _location)
    {
        this.uid = _uid;
        this.name = _name;
        this.eMail = _eMail;
        this.genre= _genre;
        this.location = _location;
        this.autoriseLocation = false;
        this.dateNassance = null;
        this.numTel = null;
        this.ville = null;
        this.pays= null;
        this.location = null;
        this.urlPicture = null;
        this.friends = null;
    }

    public User(String _uid, String _name, String _eMail)
    {
        this.uid = _uid;
        this.name = _name;
        this.eMail = _eMail;
        this.location = new GeoPoint(0,0);
        this.autoriseLocation = false;
    }


    public String getUid()
    {
        return uid;
    }

    public String getName()
    {
        return name;
    }

    public String geteMail()
    {
        return eMail;
    }

    public String getGenre()
    {
        return genre;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    @Nullable
    public String getDateNassance() {
        return dateNassance;
    }

    @Nullable
    public String getNumTel() {
        return numTel;
    }

    @Nullable
    public String getPays() {
        return pays;
    }

    @Nullable
    public String getVille() {
        return ville;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public void seteMail(String eMail)
    {
        this.eMail = eMail;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    public void setDateNassance(@Nullable String dateNassance) {
        this.dateNassance = dateNassance;
    }


    public void setNumTel(@Nullable String numTel) {
        this.numTel = numTel;
    }

    public void setPays(@Nullable String pays) {
        this.pays = pays;
    }

    public void setVille(@Nullable String ville) {
        this.ville = ville;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    @Nullable
    public List<Friend> getFriends() {
        return friends;
    }

    public Boolean getAutoriseLocation() {
        return autoriseLocation;
    }

    public void setAutoriseLocation(Boolean autoriseLocation) {
        this.autoriseLocation = autoriseLocation;
    }

    public void setFriends(@Nullable List<Friend> friends) {
        this.friends = friends;
    }
}
