package mx.oax.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AltoALaViolencia extends AppCompatActivity {
    ImageView home,btnViolencia;
    AlertDialog alert = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alto_ala_violencia);

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
                final AlertDialog.Builder builder = new AlertDialog.Builder(AltoALaViolencia.this);
                builder.setMessage("EMERGENCIA ENVIADA")
                        .setCancelable(true);
                alert = builder.create();
                alert.show();
            }
        });
    }
}
