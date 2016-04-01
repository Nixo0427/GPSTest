package com.example.gpstest;

import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText editText;
	private LocationManager lm;
	public static final String TAG="MainActivity";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		editText=(EditText)findViewById(R.id.editText);
		lm=(LocationManager) getSystemService(LOCATION_SERVICE);
		//�ж�GPS�Ƿ��Ѿ�����
		if( !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){ //������û������
			Toast.makeText(this, "���GPS����", Toast.LENGTH_SHORT).show();
			//���ؿ���GPS������ҳ��
			//android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS ����������ڴ� ����-λ�úͰ�ȫ���ã�
			Intent intent=new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0);//����ʹ�õ���Intent�����ݷ��ظ���һ���
			return;
		}
		// Ϊ��ȡ����λ����Ϣʱ���ò�ѯ����  
		String bestProvider=lm.getBestProvider(getCriteria(), true);
	    // ��ȡλ����Ϣ  
        // ��������ò�ѯҪ��getLastKnownLocation��������Ĳ���ΪLocationManager.GPS_PROVIDER  
		Location location=lm.getLastKnownLocation(bestProvider);
		udateView(location);
		 // ����״̬  
		lm.addGpsStatusListener(listener);
			  // �󶨼�������4������  
	        // ����1���豸����GPS_PROVIDER��NETWORK_PROVIDER����  
	        // ����2��λ����Ϣ�������ڣ���λ����  
	        // ����3��λ�ñ仯��С���룺��λ�þ���仯������ֵʱ��������λ����Ϣ  
	        // ����4������  
	        // ��ע������2��3���������3��Ϊ0�����Բ���3Ϊ׼������3Ϊ0����ͨ��ʱ������ʱ���£�����Ϊ0������ʱˢ��  
		 // 1�����һ�Σ�����Сλ�Ʊ仯����1�׸���һ�Σ�  
        // ע�⣺�˴�����׼ȷ�ȷǳ��ͣ��Ƽ���service��������һ��Thread����run��sleep(10000);Ȼ��ִ��handler.sendMessage(),����λ��  
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
	}
	private LocationListener locationListener=new LocationListener() {
		/** 
         * GPS״̬�仯ʱ���� 
         */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// GPS״̬Ϊ�ɼ�ʱ 
			case LocationProvider.AVAILABLE:
			    Log.i(TAG, "��ǰGPS״̬Ϊ�ɼ�״̬");  
                break;  
             // GPS״̬Ϊ��������ʱ  
			case LocationProvider.OUT_OF_SERVICE:
				  Log.i(TAG, "��ǰGPS״̬Ϊ��������״̬");  
	               break;  
	            // GPS״̬Ϊ��ͣ����ʱ 
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i(TAG, "��ǰGPS״̬Ϊ��ͣ����״̬");
				break;
			}
			
		}
		 /** 
         * GPS����ʱ���� 
         */  
		@Override
		public void onProviderEnabled(String provider) {
			Location location=lm.getLastKnownLocation(provider);
			udateView(location);
		}
		  /** 
         * GPS����ʱ���� 
         */ 
		@Override
		public void onProviderDisabled(String provider) {
			udateView(null);
			
		}
		/** 
         * λ����Ϣ�仯ʱ���� 
         */
		@Override
		public void onLocationChanged(Location location) {
			udateView(location);
			    Log.i(TAG, "ʱ�䣺" + location.getTime());  
	            Log.i(TAG, "���ȣ�" + location.getLongitude());  
	            Log.i(TAG, "γ�ȣ�" + location.getLatitude());  
	            Log.i(TAG, "���Σ�" + location.getAltitude());  
		}
	};
	   // ״̬���� 
	GpsStatus.Listener listener=new GpsStatus.Listener(){

		@Override
		public void onGpsStatusChanged(int event) {
			switch (event) {
			  // ��һ�ζ�λ 
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				   Log.i(TAG, "��һ�ζ�λ");  
	                break;
	         // ����״̬�ı� 
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				 Log.i(TAG, "����״̬�ı�"); 
				// ��ȡ��ǰ״̬  
				 GpsStatus gpsStatus=lm.getGpsStatus(null);
				  // ��ȡ���ǿ�����Ĭ�����ֵ 
				 int maxStatellites=gpsStatus.getMaxSatellites();
				 // ����һ��������������������  
				 Iterator<GpsSatellite> iters=gpsStatus.getSatellites().iterator();
				 int count=0;
				 while(iters.hasNext()&&count<=maxStatellites ){
					 GpsSatellite s=iters.next();
					 count++;
				 }
				 System.out.println("��������" + count + "������");  
	                break;
	                // ��λ���� 
			case GpsStatus.GPS_EVENT_STARTED:
				 Log.i(TAG, "��λ����");  
	                break;  
	                //��λ����
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.i(TAG,"��λ����");
				break;
			}
		}
		
	};
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(locationListener);
	}


	private  void udateView(Location location) {
		if(location!=null){
			   editText.setText("�豸λ����Ϣ\n\n���ȣ�");  
	            editText.append(String.valueOf(location.getLongitude()));  //����
	            editText.append("\nγ�ȣ�");  
	            editText.append(String.valueOf(location.getLatitude()));  //γ��
		}else{
			 // ���EditText����  
            editText.getEditableText().clear();  
		}
	}
	 /** 
     * ���ز�ѯ���� 
     *  
     * @return 
     */  
    private Criteria getCriteria() {  
        Criteria criteria = new Criteria();  
        // ���ö�λ��ȷ�� Criteria.ACCURACY_COARSE�Ƚϴ��ԣ�Criteria.ACCURACY_FINE��ȽϾ�ϸ  
        criteria.setAccuracy(Criteria.ACCURACY_FINE);  
        // �����Ƿ�Ҫ���ٶ�  
        criteria.setSpeedRequired(false);  
        // �����Ƿ�������Ӫ���շ�  
        criteria.setCostAllowed(false);  
        // �����Ƿ���Ҫ��λ��Ϣ  
        criteria.setBearingRequired(false);  
        // �����Ƿ���Ҫ������Ϣ  
        criteria.setAltitudeRequired(false);  
        // ���öԵ�Դ������  
        criteria.setPowerRequirement(Criteria.POWER_LOW);  
        return criteria;  
    }  
}
