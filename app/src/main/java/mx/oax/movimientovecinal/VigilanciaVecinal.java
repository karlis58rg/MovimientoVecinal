package mx.oax.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import mx.oax.movimientovecinal.ServiceShake.Service911;

public class VigilanciaVecinal extends AppCompatActivity {
    ImageView btnVigilancia;
    AlertDialog alert = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vigilancia_vecinal);

        btnVigilancia = findViewById(R.id.btnVigilancia);

        btnVigilancia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(VigilanciaVecinal.this);
                builder.setMessage("EMERGENCIA ENVIADA")
                        .setCancelable(true);
                alert = builder.create();
                alert.show();
            }
        });
    }
}
