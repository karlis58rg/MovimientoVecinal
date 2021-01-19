package mx.oax.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import mx.oax.movimientovecinal.ServiceShake.Service911TS;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class AltoALaViolencia extends AppCompatActivity {
    ImageView home;
    GifImageView btnViolencia;
    AlertDialog alert = null;
    int cargarInfoUserRegistrado,cargarInfoVictimaUserRegistrado;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    int numberRandom;
    String randomCodigoVerifi,codigoVerifi;
    String cargarInfoTelefono,cargarInfoNombre,cargarInfoApaterno,cargarInfoAmaterno;
    String mensaje1,mensaje2,direccion, municipio, estado,fecha,hora;
    Double lat,lon;
    private LocationManager locationManager;
    String numero,respuestaJson,m_Item1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alto_ala_violencia);
        cargarUserRegistrado();
        Random();
        locationStart();
        if(cargarInfoUserRegistrado == 0){
            getUserViolencia();
        }

        btnViolencia = findViewById(R.id.btnViolencia);
        home = findViewById(R.id.imgHomeViolencia);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AltoALaViolencia.this,MenuEventos.class);
                startActivity(i);
                finish();
            }
        });

        btnViolencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cargarInfoUserRegistrado == 1){
                    Toast.makeText(getApplicationContext(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS", Toast.LENGTH_SHORT).show();
                    insertUserRegistrado();
                }else {
                    Intent i = new Intent(AltoALaViolencia.this,MensajeSalidaAltoViolencia.class);
                    startActivity(i);
                }
            }
        });
    }

    //********************************** INSERT Y UPDATE AL SERVIDOR ***********************************//
    public void insertUserRegistrado(){
        //*************** FECHA **********************//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        fecha = dateFormat.format(date);

        //*************** HORA **********************//
        Date time = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        hora = timeFormat.format(time);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Folio",randomCodigoVerifi)
                .add("Telefono",cargarInfoTelefono)
                .add("NombreUsuario",cargarInfoNombre)
                .add("ApaternoUsuario",cargarInfoApaterno)
                .add("AmaternoUsuario",cargarInfoAmaterno)
                .add("Municipio",municipio)
                .add("Latitud",lat.toString())
                .add("Longitud",lon.toString())
                .add("DescripcionEmergencia","VIOLENCIA CONTRA LA MUJER")
                .add("Fecha",fecha)
                .add("Hora",hora)
                .add("RutaImagenImputado",direccion)
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/EventosViolencia")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO, FAVOR DE VERIFICAR SU CONEXCIÓN A INTERNET", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();
                    AltoALaViolencia.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(AltoALaViolencia.this,MensajeEnviadoAltoViolencia.class);
                            startActivity(i);
                            finish();
                        }
                    });
                }
            }
        });
    }
    //*********************************************************************//
    public void cargarUserRegistrado() {
        share = getApplication().getSharedPreferences("main", Context.MODE_PRIVATE);
        cargarInfoUserRegistrado = share.getInt("BANDERAUSERREGISTRADO", 0);
        cargarInfoTelefono = share.getString("TELEFONO", "SIN INFORMACION");
        cargarInfoNombre = share.getString("NOMBRE", "SIN INFORMACION");
        cargarInfoApaterno = share.getString("APATERNO", "SIN INFORMACION");
        cargarInfoAmaterno = share.getString("AMATERNO", "SIN INFORMACION");
    }
    //********************* GENERA EL NÚMERO ALEATORIO PARA EL FOLIO *****************************//
    public void Random(){
        Random random = new Random();
        numberRandom = random.nextInt(9000)*99;
        codigoVerifi = String.valueOf(numberRandom);
        randomCodigoVerifi = "OAX2021"+codigoVerifi;
    }
    /*********************Apartir de aqui empezamos a obtener la direciones y coordenadas************************/
    public void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        AltoALaViolencia.Localizacion Local = new AltoALaViolencia.Localizacion();
        Local.setAltoALaViolencia(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        mensaje1 = "Localizacion agregada";
        mensaje2 = "";
        Log.i("HERE", mensaje1);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation(Location loc) {
        /***********Obtener la direccion de la calle a partir de la latitud y la longitud******************************/
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direccion = DirCalle.getAddressLine(0);
                    municipio = DirCalle.getLocality();
                    estado = DirCalle.getAdminArea();
                    if (municipio != null) {
                        municipio = DirCalle.getLocality();
                    } else {
                        municipio = "SIN INFORMACION";
                    }
                    guardar();
                    Log.i("HERE", "dir" + direccion + "mun" + municipio + "est" + estado);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**************************Aqui empieza la Clase Localizacion********************************/
    public class Localizacion implements LocationListener {
        AltoALaViolencia altoALaViolencia;

        public AltoALaViolencia getAltoALaViolencia() {
            return altoALaViolencia;
        }

        public void setAltoALaViolencia(AltoALaViolencia altoALaViolencia1) {
            this.altoALaViolencia = altoALaViolencia1;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            lat = loc.getLatitude();
            lon = loc.getLongitude();
            guardarCoor();
            String Text = "Lat = " + loc.getLatitude() + "\n Long = " + loc.getLongitude();
            mensaje1 = Text;
            Log.i("HERE", mensaje1);
            this.altoALaViolencia.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            mensaje1 = "GPS Desactivado";
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            mensaje1 = "GPS Activado";
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    /****************SE GUARDA LA INFO DE LAS COORDENADAS****************/
    private void guardar() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        editor = share.edit();
        editor.putString("DIRECCION", direccion);
        editor.putString("MUNICIPIO", municipio);
        editor.putString("ESTADO", estado);
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    private void guardarCoor() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        editor = share.edit();
        editor.putString("LATITUDE", lat.toString());
        editor.putString("LONGITUDE", lon.toString());
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    private void guardarUserRegistrado() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("BANDERAUSERREGISTRADO", cargarInfoVictimaUserRegistrado);
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

    /******************GET A LA BD***********************************/
    public void getUserViolencia() {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/UsuarioRegistrado?telefono="+cargarInfoTelefono)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(),"ERROR AL OBTENER LA INFORMACIÓN, POR FAVOR VERIFIQUE SU CONEXIÓN A INTERNET",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    AltoALaViolencia.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jObj = null;
                                String resObj = myResponse;
                                System.out.println(resObj);
                                jObj = new JSONObject("" + resObj + "");
                                respuestaJson = jObj.getString("m_Item1");
                                m_Item1 = "SIN INFORMACION";
                                if (respuestaJson.equals(m_Item1)) {
                                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, NO SE CUENTA CON INFORMACIÓN DE ESTE NÚMERO TELEFÓNICO", Toast.LENGTH_SHORT).show();
                                } else {
                                    cargarInfoVictimaUserRegistrado = 1;
                                    guardarUserRegistrado();
                                    Log.i("HERE", "" + jObj);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        });
    }
}
