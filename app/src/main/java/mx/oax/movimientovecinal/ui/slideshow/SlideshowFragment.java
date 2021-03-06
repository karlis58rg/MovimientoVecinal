package mx.oax.movimientovecinal.ui.slideshow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import mx.oax.movimientovecinal.MenuEventos;
import mx.oax.movimientovecinal.MiWidget;
import mx.oax.movimientovecinal.R;
import mx.oax.movimientovecinal.ServiceShake.Service911;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    Button btnCrear;
    public static int num_imag_violencia = 0;
    SharedPreferences share;
    SharedPreferences.Editor editor;
    int widgetViolencia = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        /*************************************************************/
        //*****************************************************************//
        btnCrear = root.findViewById(R.id.boton_crear_widget);

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                widgetViolencia = 2;
                guardarActividad();
                AppWidgetManager mAppWidgetManager = v.getContext().getSystemService(AppWidgetManager.class);
                ComponentName myProvider = new ComponentName(v.getContext(), MiWidget.class);
                if(mAppWidgetManager.isRequestPinAppWidgetSupported()){
                    mAppWidgetManager.requestPinAppWidget(myProvider,null,null);
                }
            }
        });

        return root;
    }

    private void guardarActividad() {
        share = getActivity().getSharedPreferences("main",getContext().MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("VIOLENCIA", widgetViolencia );
        editor.commit();
        // Toast.makeText(getApplicationContext(),"Dato Guardado",Toast.LENGTH_LONG).show();
    }

}