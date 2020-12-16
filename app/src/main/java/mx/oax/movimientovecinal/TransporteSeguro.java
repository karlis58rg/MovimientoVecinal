package mx.oax.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TransporteSeguro extends AppCompatActivity {
    LinearLayout lyTransporte,lyIntroduce,lyPlaca,lyEnviarPlaca,lyPlacaEnviada,lyEncasoDe,lyDetenerServicio,lyDetenerServicioEjecución,lyEmergenciaEnviada;
    EditText txtPlaca;
    TextView lblNoPlaca;
    Button btnIniciar;
    String placa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transporte_seguro);

        /***************FASE 1********************/
        lyTransporte = findViewById(R.id.lyTransporte);
        lyIntroduce = findViewById(R.id.lyIntroduce);
        lyPlaca = findViewById(R.id.lyPlaca);
        lyEnviarPlaca = findViewById(R.id.lyEnviarPlaca);
        txtPlaca = findViewById(R.id.txtPlaca);
        btnIniciar = findViewById(R.id.btnIniciar);

        /*************FASE 2********************/
        lyPlacaEnviada = findViewById(R.id.lyPlacaEnviada);
        lyEncasoDe = findViewById(R.id.lyEncasoDe);
        lyDetenerServicio = findViewById(R.id.lyDetenerServicio);
        lyDetenerServicioEjecución = findViewById(R.id.lyDetenerServicioEjecución);
        lblNoPlaca = findViewById(R.id.lblNoPlaca);
        lyEmergenciaEnviada = findViewById(R.id.lyEmergenciaEnviada);

        lyPlacaEnviada.setVisibility(View.INVISIBLE);
        lyEncasoDe.setVisibility(View.INVISIBLE);
        lyDetenerServicio.setVisibility(View.INVISIBLE);
        lyDetenerServicioEjecución.setVisibility(View.INVISIBLE);
        lblNoPlaca.setVisibility(View.INVISIBLE);
        lyEmergenciaEnviada.setVisibility(View.INVISIBLE);

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPlaca.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"EL CAMPO **PLACA** ES OBLIGATORIO",Toast.LENGTH_LONG).show();
                }else{
                    placa = txtPlaca.getText().toString();
                    lblNoPlaca.setText(placa);
                    lyTransporte.setVisibility(View.INVISIBLE);
                    lyIntroduce.setVisibility(View.INVISIBLE);
                    lyPlaca.setVisibility(View.INVISIBLE);
                    lyEnviarPlaca.setVisibility(View.INVISIBLE);

                    lyPlacaEnviada.setVisibility(View.VISIBLE);
                    lyEncasoDe.setVisibility(View.VISIBLE);
                    lyDetenerServicio.setVisibility(View.VISIBLE);
                    lyDetenerServicioEjecución.setVisibility(View.VISIBLE);
                    lblNoPlaca.setVisibility(View.VISIBLE);
                }

            }
        });



    }
}
