package mx.oax.movimientovecinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import mx.oax.movimientovecinal.ServiceShake.Service911TS;

public class TransporteSeguro extends AppCompatActivity {
    LinearLayout lyTransporte,lyIntroduce,lyPlaca,lyEnviarPlaca,lyPlacaEnviada,lyEncasoDe,lyDetenerServicio,lyDetenerServicioEjecución,lyEmergenciaEnviada;
    EditText txtPlaca;
    TextView lblNoPlaca;
    Button btnIniciar,btnDetenerServicio;
    String placa;
    ImageView home;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    Activity activity;
    private static final int CODIGO_SOLICITUD_PERMISO = 1;
    private LocationManager locationManager;
    private Context context;
    int acceso = 0;
    AlertDialog alert = null;
    String cargarInfoServicio,cargarInfoPlaca;
    String cargarInfoServicioShake = "creado";

    //********************** SENSOR *******************************//
    Intent mServiceIntent;
    private Service911TS mSensorService;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_seguro);

        cargarServicio();
        //locationStart();

        home = findViewById(R.id.imgHomeTransporte);

        /***************FASE 1********************/
        lyTransporte = findViewById(R.id.lyTransporte);
        lyIntroduce = findViewById(R.id.lyIntroduce);
        lyPlaca = findViewById(R.id.lyPlaca);
        lyEnviarPlaca = findViewById(R.id.lyEnviarPlaca);
        txtPlaca = findViewById(R.id.txtPlaca);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnDetenerServicio = findViewById(R.id.btnDetenerServicio);

        /*************FASE 2********************/
        lyPlacaEnviada = findViewById(R.id.lyPlacaEnviada);
        lyEncasoDe = findViewById(R.id.lyEncasoDe);
        lyDetenerServicio = findViewById(R.id.lyDetenerServicio);
        lyDetenerServicioEjecución = findViewById(R.id.lyDetenerServicioEjecución);
        lblNoPlaca = findViewById(R.id.lblNoPlaca);
        lyEmergenciaEnviada = findViewById(R.id.lyEmergenciaEnviada);

        lyPlacaEnviada.setVisibility(View.INVISIBLE);
        lyEncasoDe.setVisibility(View.INVISIBLE);
        lyDetenerServicio.setVisibility(View.INVISIBLE);
        lyDetenerServicioEjecución.setVisibility(View.INVISIBLE);
        lblNoPlaca.setVisibility(View.INVISIBLE);
        lyEmergenciaEnviada.setVisibility(View.INVISIBLE);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransporteSeguro.this,MenuEventos.class);
                startActivity(i);
                finish();
            }
        });

        //************************* SERVICIO ********************************//
        ctx = this;

        mSensorService = new Service911TS(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());


        //************************* SERIVCIO ********************************//
        context = getApplicationContext();
        activity = this;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(acceso == 0){
            solicitarPermisoLocalizacion();
        } else {
            Toast.makeText(getApplicationContext()," **GPS** ES OBLIGATORIO PARA EL CORRECTO FUNCIONAMIENTO DEL APLICATIVO",Toast.LENGTH_LONG).show();
        }

        if (cargarInfoServicio.equals(cargarInfoServicioShake)) {
            cargarPlaca();
            //Toast.makeText(getApplicationContext(), "YA TIENES UN SERVICIO EN EJECUCION CON LA PLACA: " + cargarInfoPlaca, Toast.LENGTH_LONG).show();
            lblNoPlaca.setText(cargarInfoPlaca);
            /*inicia.setVisibility(View.GONE);
            detiene.setVisibility(View.VISIBLE);
            paloma.setVisibility(View.VISIBLE);
            txtPlaca.setVisibility(View.GONE);
            lblagita.setVisibility(View.VISIBLE);
            emergencia.setVisibility(View.VISIBLE);
            lblPlaca.setVisibility(View.VISIBLE);
            fin.setVisibility(View.VISIBLE);*/
        }/*else{
            //Toast.makeText(getApplicationContext(),"SIN SERVICO",Toast.LENGTH_LONG).show();
        }*/

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPlaca.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"EL CAMPO **PLACA** ES OBLIGATORIO",Toast.LENGTH_LONG).show();
                }else{
                    startService( new Intent( TransporteSeguro.this, Service911TS.class ) );
                    placa = txtPlaca.getText().toString();
                    lblNoPlaca.setText(placa);
                    guardar();
                    lyTransporte.setVisibility(View.INVISIBLE);
                    lyIntroduce.setVisibility(View.INVISIBLE);
                    lyPlaca.setVisibility(View.INVISIBLE);
                    lyEnviarPlaca.setVisibility(View.INVISIBLE);

                    lyPlacaEnviada.setVisibility(View.VISIBLE);
                    lyEncasoDe.setVisibility(View.VISIBLE);
                    lyDetenerServicio.setVisibility(View.VISIBLE);
                    lyDetenerServicioEjecución.setVisibility(View.VISIBLE);
                    lblNoPlaca.setVisibility(View.VISIBLE);
                }

            }
        });

        btnDetenerServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertaDetenerServicio();
            }
        });
    }

    //**********************************************************************//
    private void guardar(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("PLACA",placa);
        editor.apply();
    }

    //******************************** METODOS DEL SERVICIO ****************************************//
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService( Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //  return true;
        //}

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(new Intent(FormSensorIngresaPlaca.this, ServiceShake.class));
        Log.i("MAINACT", "onDestroy!");
    }

    //************************************ PERMISOS GPS ***********************************************//

    public void solicitarPermisoLocalizacion(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(TransporteSeguro.this, "Permisos Activados", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, CODIGO_SOLICITUD_PERMISO);
        }
    }

    private void alertaGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema de GPS esta desactivado, ¿Desea Activarlo?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceso = 1;
                        startActivity(new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceso = 1;
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void alertaDetenerServicio(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿ESTÁ USTED SEGURO EN DETENER LA ALERTA?")
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isMyServiceRunning( mSensorService.getClass())) {
                            updateServicio();
                            stopService( mServiceIntent );
                            stopService( new Intent( TransporteSeguro.this, Service911TS.class ) );
                            onDestroy();
                            System.exit( 0 );
                            Log.i("HEY", "CON SERVICIO INICIADO");
                        }else{
                            updateServicio();
                            Intent intent = new Intent( TransporteSeguro.this, MenuEventos.class );
                            startActivity( intent );
                            finish();
                            Log.i("HEY", "SIN SERVICIO");
                        }
                    }
                })
                .setNegativeButton("NO, CONTINUAR CON LA ALERTA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public boolean checarStatusPermiso(int resultado){
        if(resultado == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CODIGO_SOLICITUD_PERMISO :
                int resultado = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

                if(checarStatusPermiso(resultado)) {
                    if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
                        alertaGPS();
                    }
                } else {
                    Toast.makeText(activity, "No estan activos los permisos", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("SI SALE DE ESTÁ PANTALLA, DA POR TERMINADA LA ALERTA")
                .setCancelable(false)
                .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isMyServiceRunning( mSensorService.getClass())) {
                            updateServicio();
                            stopService( mServiceIntent );
                            stopService( new Intent( TransporteSeguro.this, Service911TS.class ) );
                            onDestroy();
                            System.exit( 0 );
                            Log.i("HEY", "CON SERVICIO INICIADO");
                        }else{
                            updateServicio();
                            Intent intent = new Intent( TransporteSeguro.this, MenuEventos.class );
                            startActivity( intent );
                            finish();
                            Log.i("HEY", "SIN SERVICIO");
                        }
                    }
                })
                .setNegativeButton("NO, CONTINUAR CON LA ALERTA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void cargarServicio(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoServicio = share.getString("servicio","");
        //Toast.makeText(getApplicationContext(),cargarInfoServicio,Toast.LENGTH_LONG).show();
    }

    private void cargarPlaca() {
        share = getSharedPreferences("main", MODE_PRIVATE);
        cargarInfoPlaca = share.getString("PLACA", "");
        //Toast.makeText(getApplicationContext(),cargarInfoPlaca,Toast.LENGTH_LONG).show();
    }

    private void updateServicio(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoServicio = share.getString("servicio",null);
        //Toast.makeText(getApplicationContext(),"Dato Eliminado",Toast.LENGTH_LONG).show();
    }

}
