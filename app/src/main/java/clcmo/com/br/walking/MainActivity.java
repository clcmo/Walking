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
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private TextView distanciaPercorrida;
    private static final int REQ_PERMISSAO_GPS = 1001;
    private Chronometer cronometro;

    //chamando os botões de permissão e consulta
    private Button permissaoGPS;
    private Button ativarGPS;
    private Button desativarGPS;
    private Button iniciarPercurso;
    private Button finalizarPercurso;

    //verificar se o usuário ativou o GPS
    private boolean teste = false, testeGPS = false, latInicial = false;
    private double distanciatotal = 0;
    private Location locationA = new Location("point A");
    private String stringfinal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissaoGPS = findViewById(R.id.permissaoGPS);
        ativarGPS = findViewById(R.id.ativarGPS);
        desativarGPS = findViewById(R.id.desativarGPS);
        iniciarPercurso = findViewById(R.id.iniciarPercurso);
        finalizarPercurso = findViewById(R.id.finalizarPercurso);
        distanciaPercorrida = findViewById(R.id.distanciaPercorrida);
        cronometro = findViewById(R.id.cronometro);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private Observador observer = new Observador();
    private class Observador implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (teste == true) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                if (latInicial == false) {
                    latInicial = true;
                    locationA.setLatitude(latitude);
                    locationA.setLongitude(longitude);
                }
                else {
                    Location locationB = new Location("point B");
                    locationB.setLatitude(latitude);
                    locationB.setLongitude(longitude);
                    distanciatotal = locationA.distanceTo(locationB);
                    String texto = String.format("%.2f", distanciatotal);
                    distanciaPercorrida.setText(texto);
                }
            }
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
    }

    public void onClick(View v) {
        switch (v.getId()) {
            //PermissãoGPS
            case R.id.permissaoGPS:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED){ //Se o usuário já permitiu mandar mensagem de aviso
                    Toast.makeText(this,getString(R.string.ativadoGPS),Toast.LENGTH_SHORT).show();
                } else{
                    ActivityCompat.requestPermissions(this,new String[]{ //Senão permitiu, faz a solicitação
                            Manifest.permission.ACCESS_FINE_LOCATION }, REQ_PERMISSAO_GPS);
                }
                break;

            //Ativar GPS
            case R.id.ativarGPS:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED){ //Se já foi permitido ativa
                    testeGPS = true;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,observer);
                    Toast.makeText(this,getString(R.string.ativandoGPS),Toast.LENGTH_SHORT).show();

                } else{ //Senão avisa que precisa permitir para ativar
                    Toast.makeText(this,getString(R.string.pedePermissao),Toast.LENGTH_SHORT).show();
                }
                break;

            //Desativar GPS
            case R.id.desativarGPS:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED){ //Se já foi permitido ativa
                    testeGPS = false;
                    locationManager.removeUpdates(observer);
                    Toast.makeText(this,getString(R.string.desativandoGPS),Toast.LENGTH_SHORT).show();
                } else{ //Senão avisa que precisa permitir para ativar
                    Toast.makeText(this,getString(R.string.desativadoGPS),Toast.LENGTH_SHORT).show();
                }
                break;

            //Iniciar Percuso
            case R.id.iniciarPercurso:
                if (teste == true) {
                    Toast.makeText(this,getString(R.string.ativadoPercurso),Toast.LENGTH_SHORT).show();
                } else if (testeGPS == false) {
                    Toast.makeText(this,getString(R.string.desativadoGPS),Toast.LENGTH_SHORT).show();
                } else {
                    teste = true;
                    desativarGPS.setEnabled(false);
                    distanciaPercorrida.setText("0");
                    cronometro.start();
                }
                break;

            //Terminar Percuso
            case R.id.finalizarPercurso:
                if (teste == false) {
                    Toast.makeText(this,getString(R.string.finalizadoPercurso),Toast.LENGTH_SHORT).show();
                } else{
                    teste = false;
                    desativarGPS.setEnabled(true);
                    cronometro.stop();
                    latInicial = false;
                    distanciaPercorrida.setText(" ");
                    stringfinal = String.format("Distancia: %.2f Tempo: ",distanciatotal);
                    Toast.makeText(this,stringfinal,Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
}
