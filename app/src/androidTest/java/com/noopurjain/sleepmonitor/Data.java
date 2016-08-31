package com.noopurjain.sleepmonitor;

/**
 * Created by noopurjain on 30/08/16.
 */
public class Data {
    protected double x,y,z;
    protected long timestamp;

    public Data(long timestamp, double x, double y, double z){
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String toString() {
        return "t= " + timestamp + ", x=" + x + " ,y=" + y + " ,z=" + z;
    }
}
