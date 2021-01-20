package mx.oax.movimientovecinal;

import android.Manifest;
import android.app.Activity;
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

import mx.oax.movimientovecinal.ui.slideshow.SlideshowFragment;
import mx.oax.movimientovecinal.ui.transporte.transporte;

/**
 * Implementation of App Widget functionality.
 */
public class MiWidget extends AppWidgetProvider {
    SharedPreferences share;
    SharedPreferences.Editor editor;
    int cargarInfoViolenciaWidget,cargarInfoTransporteWidget;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
        cargarInfoTransporteWidget = share.getInt("TRANSPORTE", 0);
        cargarInfoViolenciaWidget= share.getInt("VIOLENCIA", 0);

            if(cargarInfoTransporteWidget == 1) {
                final int N = appWidgetIds.length;
                    System.out.println(cargarInfoTransporteWidget);
                    Intent intent = new Intent(context, TransporteSeguro.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mi_widget);
                    views.setOnClickPendingIntent(R.id.boton_abrir_app, pendingIntent);

                    SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                    String strDate = mdformat.format(Calendar.getInstance().getTime());
                    views.setTextViewText(R.id.hora_ultima_actualizacion, strDate);

                    if (transporte.num_imag_transporte == 0) {
                        views.setImageViewResource(R.id.imagen_widget, R.drawable.ic_destino);
                    }
                    System.out.println(appWidgetIds);
                    share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
                    editor = share.edit();
                    editor.remove("TRANSPORTE").commit();
                    appWidgetManager.updateAppWidget(appWidgetIds, views);

            }
            if(cargarInfoViolenciaWidget == 2) {
                final int N = appWidgetIds.length;
                    System.out.println(cargarInfoViolenciaWidget);
                    Intent intent = new Intent(context, AltoALaViolencia.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mi_widget);
                    views.setOnClickPendingIntent(R.id.boton_abrir_app, pendingIntent);

                    SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                    String strDate = mdformat.format(Calendar.getInstance().getTime());
                    views.setTextViewText(R.id.hora_ultima_actualizacion, strDate);

                    if (SlideshowFragment.num_imag_violencia == 0) {
                        views.setImageViewResource(R.id.imagen_widget, R.drawable.ic_altoviolencia);
                    }
                    System.out.println(appWidgetIds);
                    share = context.getSharedPreferences("main", Context.MODE_PRIVATE);
                    editor = share.edit();
                    editor.remove("VIOLENCIA").commit();
                    appWidgetManager.updateAppWidget(appWidgetIds, views);
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

