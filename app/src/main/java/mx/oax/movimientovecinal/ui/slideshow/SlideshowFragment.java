package mx.oax.movimientovecinal.ui.slideshow;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import mx.oax.movimientovecinal.MenuEventos;
import mx.oax.movimientovecinal.R;
import mx.oax.movimientovecinal.ServiceShake.Service911;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    Button btnConfigurar,btnValorShake;
    EditText txtValorShake;
    public static int versionSDK;
    public static int valorShake = 0;
    String valShake = "";
    String cargarInfoServicio;
    int cargarInfoBandera;
    AlertDialog alert = null;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    Activity activity;

    //********************** SENSOR *******************************//
    Intent mServiceIntent;
    private Service911 mSensorService;
    Context ctx;

    public Context getCtx() {
        return ctx;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        /*************************************************************/
        //*****************************************************************//
        versionSDK = android.os.Build.VERSION.SDK_INT;
        Log.i("HEY", String.valueOf( versionSDK ) );

        txtValorShake = root.findViewById(R.id.txtValorShakeM);
        btnConfigurar = root.findViewById(R.id.btnProbarM);
        btnValorShake = root.findViewById(R.id.btnGuardarValorShakeM);

        //************************* SERVICIO ********************************//
        ctx = getActivity();
        mSensorService = new Service911(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());

        btnConfigurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"SACUDA SU TELÉFONO",Toast.LENGTH_LONG).show();
                cargarServicio();
                if(cargarInfoServicio == null){
                    valShake = txtValorShake.getText().toString();
                    valorShake = Integer.parseInt(valShake);
                    guardar();
                    getActivity().startService( new Intent( getActivity(), Service911.class));
                    System.out.println(valorShake + "SERVICIO");
                }else{
                    if(cargarInfoBandera == 1){
                        valShake = txtValorShake.getText().toString();
                        valorShake = Integer.parseInt(valShake);
                        System.out.println(valorShake + "YA CON SERVICIO INICIADO");
                        guardar();
                    }else{
                        valShake = txtValorShake.getText().toString();
                        valorShake = Integer.parseInt(valShake);
                        guardar();
                        getActivity().startService( new Intent( getActivity(), Service911.class));
                        System.out.println(valorShake + "SERVICIO");
                    }
                }
            }
        });

        btnValorShake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("EL VALOR DE SU SACUDIDA ESTÁ POR SER ESTABLECIDO")
                        .setCancelable(false)
                        .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(isMyServiceRunning( mSensorService.getClass())) {
                                    getActivity().stopService( new Intent( getActivity(), Service911.class ) );
                                    onStop();
                                    onDestroy();
                                    Log.i("HEY", "CON SERVICIO DETENIDO");
                                    if(isMyServiceRunning( mSensorService.getClass())) {
                                    }
                                    eliminarServicio();
                                    Intent intentMenu = new Intent( getActivity(), MenuEventos.class );
                                    startActivity( intentMenu);
                                    System.exit(0);
                                }
                            }
                        })
                        .setNegativeButton("NO, VOLVER A CONFIGURAR VALOR ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                alert = builder.create();
                alert.show();
            }
        });

        return root;
    }

    //******************************** METODOS DEL SERVICIO ****************************************//
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService( Context.ACTIVITY_SERVICE);
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
    public void onDestroy() {
        super.onDestroy();
        Log.i("MAINACT", "onDestroy!");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("MAINACT", "onStop!");
    }

    //**********************************************************************//
    private void guardar(){
        share = getActivity().getSharedPreferences("main",Context.MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("valorShake",valorShake);
        editor.putString( "sdk", String.valueOf( versionSDK ));
        editor.apply();
    }

    private void cargarServicio(){
        share = getActivity().getSharedPreferences("main",Context.MODE_PRIVATE);
        cargarInfoServicio = share.getString("servicio911",null);
        cargarInfoBandera = share.getInt("bandera911",0);
    }

    private void eliminarServicio(){
        share = getActivity().getSharedPreferences("main",Context.MODE_PRIVATE);
        editor = share.edit();
        editor.remove("bandera911").commit();
        //Toast.makeText(getApplicationContext(),"Dato Eliminado",Toast.LENGTH_LONG).show();
    }
}