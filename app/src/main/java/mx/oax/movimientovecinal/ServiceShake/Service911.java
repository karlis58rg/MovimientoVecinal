package mx.oax.movimientovecinal.ServiceShake;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import mx.oax.movimientovecinal.ConfiguracionesAgitado;
import mx.oax.movimientovecinal.R;

import static mx.oax.movimientovecinal.Shake.App.CHANNEL_ID;
public class Service911 extends Service implements SensorEventListener {
    public Service911(Context applicationContext) {
        super();
        Log.i("HERE", "HERE I AM!");
    }

    MyTask2 miTareaSuper;
    public SensorManager mSensorManager;
    public Sensor mAccelerometer;
    public float mAccel; // acceleration apart from gravity
    public float mAccelCurrent=0; // current acceleration including gravity
    public float mAccelLast; // last acceleration including gravity
    public int sacudidas=0;
    public long tiempoActual=0;
    public long tiempoAnterior=0;
    public long diferencia;
    public String cargarInfoSDK;
    public int cargarInfoValorShake = 0;
    public String serbar = "sincrear";

    SharedPreferences shared;
    SharedPreferences.Editor editor;
    String version;
    int comparar;

    private static final int NOTIFICATION_ID = 200;
    private static final String CHANNEL_NAME = "PANIC_BUTTON";

    public Service911() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("HERE", "SERVICIO CREADO");
        miTareaSuper = new MyTask2();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        miTareaSuper.execute();
        cargar();
        version = cargarInfoSDK;
        comparar = Integer.parseInt( version );

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());
        //startForeground();

        Intent notificationIntent = new Intent(this, ConfiguracionesAgitado.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Servicio de Botón de Pánico")
                .setContentText("Sacudir para verificar el servicio de sacudida")
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        //All notifications should go through NotificationChannel on Android 26 & above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("HEY", "onDestroy!");
        miTareaSuper.cancel(true);
    }

    public class MyTask2 extends AsyncTask<String,String,String> {
        private boolean aux;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            aux = true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.i("HERE","ACTUALIZANDO EVENTO");
            serbar = "creado";
            Log.i("HERE",serbar);
            guardarServicio();
            Log.i("HERE","ACTUALIZANDO EVENTO DESPUES DE CREARLO");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            miTareaSuper.onCancelled();
            aux = false;
        }

        @Override
        protected String doInBackground(String... strings) {
            while (aux){
                try {
                    publishProgress();
                    Thread.sleep(15000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        cargar();
        version = cargarInfoSDK;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter
        Log.i("HERE",version);

        if (mAccel > cargarInfoValorShake) {
            tiempoAnterior = tiempoActual;
            tiempoActual = System.currentTimeMillis();
            diferencia=tiempoActual-tiempoAnterior;
            if(diferencia < 700){
                sacudidas++;
            }
            else{
                sacudidas = 0;
            }
            if(sacudidas == 2 ) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1500);
                Toast.makeText(getApplicationContext(), "VERIFICACNDO SACUDIDA", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //**************************************************************************************//
    private void guardarServicio(){
        shared = getSharedPreferences("main",MODE_PRIVATE);
        editor = shared.edit();
        editor.putString("servicio",serbar);
        editor.apply();
    }

    private void cargar(){
        shared = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoValorShake = shared.getInt("valorShake",0);
        cargarInfoSDK = shared.getString( "sdk","" );
    }
}
