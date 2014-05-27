package fmi.android.data;

import android.database.Cursor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dkpavlov on 1/26/14.
 */
public class Location {

    private static final DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    private long id;

    private String name;

    private String address;

    private Double latitude;

    private Double longitude;

    private String picturePath;

    private Date dateOfCreation;

    public Location(Cursor cursor) {
        this.id = cursor.getLong(0);
        this.name = cursor.getString(1);
        this.address = cursor.getString(2);
        this.latitude = cursor.getDouble(3);
        this.longitude = cursor.getDouble(4);
        this.picturePath = cursor.getString(5);
        try {
            this.dateOfCreation = format.parse(cursor.getString(6));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Location(){
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    @Override
    public String toString() {
        return  name;
    }
}
