package net.gustavopaes.tracker;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Gustavo on 18/06/13.
 */
public class TrackerLocation {

    MainActivity mainInstance;
    TextView logArea;
    LocationManager locationManager;

    // provedores de informações
    private static String NETWORK_PROVIDER = LocationManager.NETWORK_PROVIDER;
    private static String GPS_PROVIDER = LocationManager.GPS_PROVIDER;

    // Tempo mínimo para receber atualização, em milisegundos
    private static long MIN_TIME_TO_REQUEST_NEW_POSITION = 1 * 60;

    // Distância, em metros, diferente da última posição, para considerar como sendo
    // uma nova posição
    private static float MIN_DISTANCE_TO_UPDATE_NEW_POSITION = 10;

    public TrackerLocation(MainActivity mainActivity) {
        mainInstance = mainActivity;
        logArea = (TextView) mainActivity.logArea;

        Log.v("Instanciado", String.valueOf(logArea));

        locationManager = (LocationManager) mainInstance.getSystemService(Context.LOCATION_SERVICE);

        // Solicita mudanças de localização da Internet e do GPS
        locationManager.requestLocationUpdates(NETWORK_PROVIDER, MIN_TIME_TO_REQUEST_NEW_POSITION, MIN_DISTANCE_TO_UPDATE_NEW_POSITION, locationListener);
        locationManager.requestLocationUpdates(GPS_PROVIDER, MIN_TIME_TO_REQUEST_NEW_POSITION, MIN_DISTANCE_TO_UPDATE_NEW_POSITION, locationListener);
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
            Log.i("Location Changed", "Oi");
            logArea.setText("Location changed\n"+logArea.getText());

            mainInstance.workWithLocation(location);
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
