package com.ghp55.eli.ghp;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

/**
 * Created by elijn on 7/1/2018.
 */

public class PiInterface {

    private int upDownServoPos = 45;
    private int leftRightServoPos = 45;
    private int minPos = 30;
    private int maxPos = 80;
    private boolean isActive = true;

    static public String piIp = "10.19.2.196"; //WOAH!!! Awesome palindrome! Will be read from sharedprefs later
    private Socket piSock;
    private OutputStream os;

    public PiInterface() {
        //init connection, etc
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ipFromServo = PiInterface.get("http://ghp.novartech.net/ipGiver.php");
                piIp = ipFromServo.substring(ipFromServo.indexOf(":")+1);
            }
        }).start();


        //synch our servo positions
    }

    public void openConnection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    piSock = new Socket(piIp, 1738);
                    os = piSock.getOutputStream();
                    os.write("gHp2k18".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void stop(){
        sendCommand("l0");
        sendCommand("r0");
        isActive = false;
    }

    public void start(){
        isActive = true;
    }

    public void startNavigationToCoordinates(LatLng coords){
        sendCommand("n"+round(coords.latitude,7)+","+round(coords.longitude,7));
    }

    public void closeConnection(){
        try {
            os.close();
            piSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void lookUpDown(int howMuch) throws IOException {
        if(upDownServoPos<(maxPos-howMuch) && (upDownServoPos>(Math.abs(howMuch)+minPos)|| howMuch>0)) {
            upDownServoPos += howMuch;
            setUpDownServo(upDownServoPos);
        }
    }
    public void lookLeftRight(int howMuch) throws IOException {
        if(leftRightServoPos<(maxPos-howMuch) && (leftRightServoPos>(Math.abs(howMuch)+minPos) || howMuch>0)) {
            leftRightServoPos += howMuch;
            setLeftRightServo(leftRightServoPos);
        }
    }

    public void setLeftMotor(double val){
        sendCommand("l"+val);
    }

    public void setRightMotor(double val){
        sendCommand("r"+val);
    }

    public boolean isConnected(){
        if(piSock!=null && os!=null){
            return piSock.isConnected();
        }else{
            return false;
        }
    }
    //pos in degrees. Valid range: 1-180
    private void setUpDownServo(int pos) throws IOException {
        sendCommand("u"+pos);
    }

    private void setLeftRightServo(int pos) throws IOException {
        sendCommand("p"+pos);
    }

    private void sendCommand(final String cmd) {
        if(isActive) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isConnected()) {
                        try {
                            String newCmd = cmd;
                            for (int i = newCmd.length(); i < 25; i++) {
                                newCmd += " ";
                            }
                            os.write(newCmd.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    private double round(double x, int significantFigs)
    {
        x += (5 * Math.pow(10, -(significantFigs+1)));
        int a = (int)( x * (Math.pow(10, (significantFigs))));

        return a / Math.pow(10, significantFigs);
    }

    static String get(String murl){
        try{
            URL url = new URL(murl);
            HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection();
            httpurlconnection.setRequestMethod("GET");
            httpurlconnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");

            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
            StringBuffer stringbuffer = new StringBuffer();
            String s;

            while ((s = bufferedreader.readLine()) != null)
            {
                stringbuffer.append(s);
            }
            bufferedreader.close();
            return stringbuffer.toString();
        }
        catch(Exception e){
            System.err.println(e.toString());
            return "[]";
        }
    }
}
