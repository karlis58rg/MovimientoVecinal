package mx.oax.movimientovecinal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConfiguracionWidgets extends AppCompatActivity {
    public static int num_imag = 0;
    Button btnCrearWidget,btnCambiarImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_widgets);

        btnCambiarImagen = findViewById(R.id.boton_cambiar_imagen1);
        btnCrearWidget = findViewById(R.id.boton_crear_widget1);

        btnCambiarImagen.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                num_imag =  (++num_imag)%2;
                AppWidgetManager mAppWidgetManager = view.getContext().getSystemService(AppWidgetManager.class);
                Intent intent = new Intent(getApplication(),MiWidget.class);
                intent.setAction(mAppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] ids = mAppWidgetManager.getAppWidgetIds(new ComponentName(getApplication(),MiWidget.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
                sendBroadcast(intent);
            }
        });

        btnCrearWidget.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                AppWidgetManager mAppWidgetManager = view.getContext().getSystemService(AppWidgetManager.class);
                ComponentName myProvider = new ComponentName(view.getContext(),MiWidget.class);
                if(mAppWidgetManager.isRequestPinAppWidgetSupported()){
                    mAppWidgetManager.requestPinAppWidget(myProvider,null,null);
                }
            }
        });
    }
}
