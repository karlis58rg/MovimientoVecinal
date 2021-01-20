package mx.oax.movimientovecinal;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
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
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of App Widget functionality.
 */
public class MiWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for(int i = 0; i < N; i++){
            int appWidgetId = appWidgetIds[i];
            Intent intent = new Intent(context,AltoALaViolencia.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.mi_widget);
            views.setOnClickPendingIntent(R.id.boton_abrir_app,pendingIntent);

            SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
            String strDate = mdformat.format(Calendar.getInstance().getTime());
            views.setTextViewText(R.id.hora_ultima_actualizacion,strDate);

            if(ConfiguracionWidgets.num_imag == 0){
                views.setImageViewResource(R.id.imagen_widget,R.drawable.ic_altoviolencia);
            }else if(ConfiguracionWidgets.num_imag == 1){
                views.setImageViewResource(R.id.imagen_widget,R.drawable.ic_destino);
            }
            appWidgetManager.updateAppWidget(appWidgetId,views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

