package net.gustavopaes.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Gustavo on 18/06/13.
 */
public class TrackerLocation {

    MainActivity mainInstance;
    TextView logArea;
    LocationManager locationManager;
    SharedPreferences preferences;

    // provedores de informações
    private static String NETWORK_PROVIDER = LocationManager.NETWORK_PROVIDER;
    private static String GPS_PROVIDER = LocationManager.GPS_PROVIDER;

    // Tempo mínimo para receber atualização, em milisegundos
    private static long MIN_TIME_TO_REQUEST_NEW_POSITION = 1;//1 * 60;

    // Distância, em metros, diferente da última posição, para considerar como sendo
    // uma nova posição
    private static float MIN_DISTANCE_TO_UPDATE_NEW_POSITION = 1;//10;

    public TrackerLocation(MainActivity mainActivity) {
        mainInstance = mainActivity;
        logArea = (TextView) mainActivity.logArea;
        preferences = mainActivity.preferences;

        Log.v("Instanciado", String.valueOf(logArea));

        locationManager = (LocationManager) mainInstance.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Passa a ouvir atualizações de localização
     */
    public void startListenerLocation() {
        // Solicita mudanças de localização da Internet e do GPS
        locationManager.requestLocationUpdates(NETWORK_PROVIDER, MIN_TIME_TO_REQUEST_NEW_POSITION, MIN_DISTANCE_TO_UPDATE_NEW_POSITION, locationListener);
        locationManager.requestLocationUpdates(GPS_PROVIDER, MIN_TIME_TO_REQUEST_NEW_POSITION, MIN_DISTANCE_TO_UPDATE_NEW_POSITION, locationListener);
    }

    /**
     * Para de esperar atualizações de localização.
     */
    public void stopListenerLocation() {
        locationManager.removeUpdates(locationListener);
    }

    /**
     * Retorna a posição conhecida do usuário. Tenta obter a ultima posição do GPS. Se não encontrar
     * tenta a última da rede.
     * @return Location
     */
    public Location getKnowLocation() {
        Location loc = locationManager.getLastKnownLocation(GPS_PROVIDER);
        return loc != null ? loc : locationManager.getLastKnownLocation(NETWORK_PROVIDER);
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            mainInstance.workWithLocation(location);

            JSONObject json_location = new JSONObject();
            try {
                json_location.put("mobile_time", System.currentTimeMillis());
                json_location.put("location_time", location.getTime());
                json_location.put("latitude", location.getLatitude());
                json_location.put("longitude", location.getLongitude());
                json_location.put("provider", location.getProvider());
                json_location.put("accuracy", location.getAccuracy());

                // envia para o servidor
                if(mainInstance.webservice.isNetworkAvailable()) {
                    mainInstance.webservice.sendData(json_location);
                }

                // escreve o json no log
                logArea.setText(json_location.toString()+"\n"+logArea.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void onProviderDisabled(String provider) {
            // required for interface, not used
        }

        public void onProviderEnabled(String provider) {
            // required for interface, not used
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // required for interface, not used
        }
    };
}
