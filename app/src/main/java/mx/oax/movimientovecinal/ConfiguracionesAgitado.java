package mx.oax.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import mx.oax.movimientovecinal.ServiceShake.Service911;

public class ConfiguracionesAgitado extends AppCompatActivity {

    Button btnConfigurar,btnValorShake;
    EditText txtValorShake;
    public static int versionSDK;
    public static int valorShake = 0;
    String valShake = "";
    String cargarInfoServicio = "";
    SharedPreferences share;
    SharedPreferences.Editor editor;

    //********************** SENSOR *******************************//
    Intent mServiceIntent;
    private Service911 mSensorService;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuraciones_agitado);

        //*****************************************************************//
        versionSDK = android.os.Build.VERSION.SDK_INT;
        Log.i("HEY", String.valueOf( versionSDK ) );

        txtValorShake = findViewById(R.id.txtValorShake);
        btnConfigurar = findViewById(R.id.btnProbar);
        btnValorShake = findViewById(R.id.btnGuardarValorShake);

        //************************* SERVICIO ********************************//
        ctx = this;
        mSensorService = new Service911(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());

        btnConfigurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarServicio();
                if(cargarInfoServicio != null){
                    valShake = txtValorShake.getText().toString();
                    valorShake = Integer.parseInt(valShake);
                    System.out.println(valorShake + "YA CON SERVICIO INICIADO");
                    guardar();
                }else{
                    valShake = txtValorShake.getText().toString();
                    valorShake = Integer.parseInt(valShake);
                    guardar();
                    startService( new Intent( ConfiguracionesAgitado.this, Service911.class));
                    System.out.println(valorShake + "SERVICIO");
                }
            }
        });

        btnValorShake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMyServiceRunning( mSensorService.getClass())) {
                    stopService( mServiceIntent );
                    stopService( new Intent( ConfiguracionesAgitado.this, Service911.class ) );
                    onDestroy();
                    Intent intent = new Intent( ConfiguracionesAgitado.this, MenuEventos.class );
                    startActivity( intent );
                    finish();
                }
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MAINACT", "onDestroy!");
    }

    //**********************************************************************//
    private void guardar(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("valorShake",valorShake);
        editor.putString( "sdk", String.valueOf( versionSDK ));
        editor.apply();
    }
    private void cargarServicio(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoServicio = share.getString("servicio",null);
    }
}
