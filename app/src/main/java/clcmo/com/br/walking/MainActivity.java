package clcmo.com.br.walking;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private TextView distanciaPercorrida;
    private static final int REQ_PERMISSAO_GPS = 1001;
    private Chronometer cronometro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        distanciaPercorrida = findViewById(R.id.distanciaPercorrida);
        cronometro = findViewById(R.id.cronometro);
    }

    double calcDistancia(double latIni, double lngIni, double latFim, double lngFim) {
        double disTerra = 6371;                         //em kilometros e considerando até mesmo a curvatura
        double dLat = Math.toRadians(latFim - latIni);
        double dLng = Math.toRadians(lngFim - lngIni);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(latIni))
                * Math.cos(Math.toRadians(latFim));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return ((disTerra * c) * 1000);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double metros = calcDistancia(
                    location.getLatitude(),     //latitude na posição inicial
                    location.getLongitude(),    //longitude na posição inicial
                    location.getLatitude(),     //latitude na posição final
                    location.getLongitude());   //longitude na posição final
            distanciaPercorrida.setText(String.format("%sms", metros));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();        //a permissão ainda não foi concedida pelo usuário??
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //se não estiver ativado o GPS, é exibido este toast
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this,
                        "@string/PedePermissao",
                        Toast.LENGTH_SHORT).show();
            }
            //se sim, ele pede permissão
            ActivityCompat.requestPermissions(this, new String []
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSAO_GPS);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, locationListener);
            distanciaPercorrida.setText("");
            cronometro.stop();
        }
        cronometro.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQ_PERMISSAO_GPS:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED){

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                                0, locationListener);
                        distanciaPercorrida.setText("");
                    }
                } else {
                    distanciaPercorrida.setText("@string/FaltadePermissao");
                } break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
        cronometro.stop();
    }
}
