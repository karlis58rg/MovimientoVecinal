package mx.oax.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConfiguracionesAgitado extends AppCompatActivity {

    Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuraciones_agitado);

        btnGuardar = findViewById(R.id.btnGuardarValorShake);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConfiguracionesAgitado.this,MenuEventos.class);
                startActivity(i);
                finish();
            }
        });
    }
}
