package com.example.datacollection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.*;
import android.provider.Settings.Secure;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,LocationListener,GooglePlayServicesClient.OnConnectionFailedListener{
	LinearLayout linear;
	TextView devicePID;
	TextView devicePIDSDK;
	TextView deviceDnotTrack;
	TextView deviceID;
	TextView wifiSpeed;
	TextView carrier;
	TextView androidSDK;
	TextView androidVer;
	TextView brand;
	TextView model;
	TextView macaddress;
	TextView macaddressSha;
	TextView macaddressMD5;
	TextView blueToothMACNorm;
	TextView blueToothMACsha;
	TextView blueToothMACmd5;
	TextView gpsEnabled;
	TextView lat;
	TextView lon;
	TextView alt;
	TextView acc;
	TextView spe;
	TextView bea;
	String latitude = "N/A";
	String longitude = "N/A";
	String altitude = "N/A";
	String accuracy = "N/A";
	String speed = "N/A";
	String bearing = "N/A";
	LocationRequest mLocationRequest;
	LocationClient mLocationClient;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //First Load Google Play Services
        Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Context ctx = MainActivity.this.getApplicationContext();
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(ctx);
                 // When ready fire this function to render to Page in another thread
                    finished(adInfo);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        thr.start();
    }
    
    private void initiatePage()
    {
    	//Add Layout
    	linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.VERTICAL);
        
        devicePID = new TextView(this);
        linear.addView(devicePID);
        
        devicePIDSDK = new TextView(this.getApplicationContext());
        linear.addView(devicePIDSDK);
        
        deviceDnotTrack = new TextView(this.getApplicationContext());
        linear.addView(deviceDnotTrack);
        
        deviceID= new TextView(this);
        linear.addView(deviceID);
        
        wifiSpeed = new TextView(this);
        linear.addView(wifiSpeed);
        
        carrier= new TextView(this);
        linear.addView(carrier);
        
        androidSDK= new TextView(this);
        linear.addView(androidSDK);
        
        androidVer= new TextView(this);
        linear.addView(androidVer);
        
        brand= new TextView(this);
        linear.addView(brand);
        
        model= new TextView(this);
        linear.addView(model);
        
        macaddress= new TextView(this);
        linear.addView(macaddress);
        
        macaddressSha= new TextView(this);
        linear.addView(macaddressSha);
        
        macaddressMD5 = new TextView(this);
        linear.addView(macaddressMD5);
        
        blueToothMACNorm = new TextView(this);
        linear.addView(blueToothMACNorm);
        
        blueToothMACsha = new TextView(this);
        linear.addView(blueToothMACsha);
        
        blueToothMACmd5 = new TextView(this);
        linear.addView(blueToothMACmd5);
        
        gpsEnabled = new TextView(this);
        linear.addView(gpsEnabled);
        
        //Add GPS placeholder to the layout
    	lat = new TextView(this);
    	linear.addView(lat);
    	
    	lon = new TextView(this);
    	linear.addView(lon);
    	
    	alt = new TextView(this);
    	linear.addView(alt);
    	
    	acc = new TextView(this);
    	linear.addView(acc);
    	
    	spe = new TextView(this);
    	linear.addView(spe);
    	
    	bea = new TextView(this);
    	linear.addView(bea);
        
        //Render
        setContentView(linear);
    }
    
    private void displayGooglePlayID(AdvertisingIdClient.Info adInfo)
    {
    	// +++ Unique Platform ID - SDK +++
        devicePIDSDK.setText("Platform Unique Device ID - SDK: " + adInfo.getId());
        
        // +++ Do Not Track - SDK +++
        deviceDnotTrack.setText("Do not track: " + String.valueOf(adInfo.isLimitAdTrackingEnabled()));
    }
    
    private void renderPage()
    {
        // +++ Unique Platform ID +++
        String devicePlatformID = Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID);
        devicePID.setText("Platform Unique Device ID: " + devicePlatformID);
        
        // +++ Unique Device ID +++
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        deviceID.setText("Unique Device ID: " + telephonyManager.getDeviceId());
        
        // +++ Connection Speed +++
        String ConnectionSpeedLabel;
        //First check if we're using Wifi connection
        try
        {
	        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = null;
	        if (connectivityManager != null) {
	            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	        }
	        if(networkInfo.isConnected())
	        {
	        	ConnectionSpeedLabel = "Wifi";
	        }
	        else
	        {
		        int ConnectionSpeed = telephonyManager.getNetworkType();
		        switch (ConnectionSpeed) {
			        case TelephonyManager.NETWORK_TYPE_GPRS:
			        case TelephonyManager.NETWORK_TYPE_EDGE:
			        case TelephonyManager.NETWORK_TYPE_CDMA:
			        case TelephonyManager.NETWORK_TYPE_1xRTT:
			        case TelephonyManager.NETWORK_TYPE_IDEN:
			        	ConnectionSpeedLabel = "Cellular data - 2G";
			        	break;
			        case TelephonyManager.NETWORK_TYPE_UMTS:
			        case TelephonyManager.NETWORK_TYPE_EVDO_0:
			        case TelephonyManager.NETWORK_TYPE_EVDO_A:
			        case TelephonyManager.NETWORK_TYPE_HSDPA:
			        case TelephonyManager.NETWORK_TYPE_HSUPA:
			        case TelephonyManager.NETWORK_TYPE_HSPA:
			        case TelephonyManager.NETWORK_TYPE_EVDO_B:
			        case TelephonyManager.NETWORK_TYPE_EHRPD:
			        case TelephonyManager.NETWORK_TYPE_HSPAP:
			        	ConnectionSpeedLabel = "Cellular data - 3G";
			        	break;
			        case TelephonyManager.NETWORK_TYPE_LTE:
			        	ConnectionSpeedLabel = "Cellular data - 4G";
			        	break;
			        default:
		        	ConnectionSpeedLabel = "Cellular data - Unknown Generation ";
		        }
	        }
        }
        catch(Exception e)
        {
        	ConnectionSpeedLabel = e.getMessage();
        }
        
        wifiSpeed.setText("Connection Speed: " + ConnectionSpeedLabel);
        
        // +++ Carrier +++
        carrier.setText("Carrier: " + telephonyManager.getNetworkOperatorName());
        
        // +++ Device/OS +++
    	androidSDK.setText("Android SDK version: " + String.valueOf(android.os.Build.VERSION.SDK_INT));
    	androidVer.setText("Android OS version: " + android.os.Build.VERSION.RELEASE);
    	brand.setText("Brand: " + android.os.Build.BRAND);
    	model.setText("Model: " + android.os.Build.MODEL);

        // +++ WIFI Mac Address +++
        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String macAddress = info.getMacAddress();
        
        macaddress.setText("WIFI Mac Address: " + macAddress);
        
    	macaddressSha.setText("WIFI Mac Address SHA1: " + sha(macAddress));
        
        macaddressMD5.setText("WIFI Mac Address MD5: " + md5(macAddress));
        
        // +++ Bluetooth Mac Address +++
        String bluetoothMac = "";
        try
        {
	        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	        bluetoothMac = mBluetoothAdapter.getAddress();
        }
        catch(Exception e)
        {
        	bluetoothMac = e.getMessage();
        }
        
        blueToothMACNorm.setText("BlueTooth Mac Address: " + bluetoothMac);
        
        blueToothMACsha.setText("BlueTooth Mac Address SHA1: " + sha(bluetoothMac));
        
        blueToothMACmd5.setText("BlueTooth Mac Address MD5: " + md5(bluetoothMac));
        
        // Check if GPS is enabled
        
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
        	gpsEnabled.setText("GPS State: Enabled");
        }
        else
        {
        	gpsEnabled.setText("GPS State: Disabled");
        }
    }
    
    private void updateGPS()
    {
    	//Update GPS information on the layout
    	lat.setText("Latitude: " + latitude);
    	lon.setText("Longitude: " + longitude);
    	alt.setText("Altitude: " + altitude);
    	acc.setText("Accuracy: " + accuracy);
    	spe.setText("Speed: " + speed);
    	bea.setText("Bearing: " + bearing);
    }
    
    private static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    private static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
    
    private static String sha(String text){
    	try
    	{
    		return SHA1(text);
    	}
    	catch(Exception e)
    	{
    		return e.getMessage();
    	}
    }

    private void finished(final AdvertisingIdClient.Info adInfo){
    	if(adInfo!=null){
        	try
        	{
        		//Threads which refreshes the UI every second
        		final Thread refresh = new Thread() {
        		    @Override
        		    public void run() {
        		    	while(true)
        		    	{
	        		        // Block this thread for 1 seconds.
	        		        try
	        		        {
	        		            Thread.sleep(1000);
	        		        }
        		        	catch (InterruptedException e)
        		        	{
        		        		e.printStackTrace();
	        		        }
	        		 
	        		        // After sleep finished blocking, create a Runnable to run on the UI Thread and refresh the UI
	        		        runOnUiThread(new Runnable() {
	        		            @Override
	        		            public void run()
	        		            {
	        		            	renderPage();
	        		            }
	        		        });
        		    	}
        		    }
        		};

        		//First Thread to render the UI basics
	            this.runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                	initiatePage();
	                	launchGPS();
	                	renderPage();
	                	displayGooglePlayID(adInfo);
	                	updateGPS();
	                	refresh.start();
	                }
	            });
    		}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
        }
    }
    
    private void launchGPS()
    {
    	// Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 6 seconds
        mLocationRequest.setInterval(6);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(1);
        // Create a new location client, using the enclosing class to handle callbacks.
        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();
    }

    @Override
    public void onLocationChanged(Location location)
    {
    	//Update GPS coordinates when the location changed based on Google Play events
    	latitude = String.valueOf(location.getLatitude());
    	
    	longitude = String.valueOf(location.getLongitude());
    	
    	if(location.hasAltitude())
    	{
    		altitude = String.valueOf(location.hasAltitude());
    	}
    	
    	if(location.hasAccuracy())
    	{
    		accuracy = String.valueOf(location.getAccuracy());
    	}
    	
    	if(location.hasSpeed())
    	{
    		speed = String.valueOf(location.getSpeed());
    	}
    	
    	if(location.hasBearing())
    	{
    		bearing = String.valueOf(location.getBearing());
    	}
    	
    	updateGPS();
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}
	
	@Override
	public void onDisconnected() {
		
	}
}
