package mx.oax.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormRegistroUsuario extends AppCompatActivity {
    Button btnRegistrar;
    EditText txtNombre,txtApaterno,txtAmaterno,txtDireccion,txtNoConfianza;
    String nombre,aPaterno,aMaterno,direccionUsuario,noConfianza;
    String cargarInfoTelefono;
    SharedPreferences share;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_registro_usuario);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        txtNombre = findViewById(R.id.txtNombre);
        txtApaterno = findViewById(R.id.txtApaterno);
        txtAmaterno = findViewById(R.id.txtAmaterno);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtNoConfianza = findViewById(R.id.txtNoConfianza);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(txtNombre.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "SU NOMBRE ES NECESARIO", Toast.LENGTH_LONG).show();
                }else if (txtNombre.getText().length() < 3){
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, SU NOMBRE NO PUEDE SER MENOR A 3 LETRAS", Toast.LENGTH_LONG).show();
                }else if(txtApaterno.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "SU A. PATERNO ES NECESARIO", Toast.LENGTH_LONG).show();
                }else if(txtApaterno.getText().length() < 3){
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, SU A. PATERNO NO PUEDE SER MENOR A 3 LETRAS", Toast.LENGTH_LONG).show();
                }else if(txtAmaterno.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "SU A. MATERNO ES NECESARIO", Toast.LENGTH_LONG).show();
                }else if(txtAmaterno.getText().length() < 3){
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, SU A. MATERNO NO PUEDE SER MENOR A 3 LETRAS", Toast.LENGTH_LONG).show();
                }else if(txtDireccion.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "SU DIRECCIÓN ES NECESARIA", Toast.LENGTH_LONG).show();
                }else if(txtDireccion.getText().length() < 3){
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, SU A. DIRECCIÓN NO PUEDE SER MENOR A 3 LETRAS", Toast.LENGTH_LONG).show();
                }else if(txtNoConfianza.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "EL NÓ DE ALGUN CONTACTO DE CONFIANZA ES NECESARIO", Toast.LENGTH_LONG).show();
                }else if(txtNoConfianza.getText().length() < 10){
                    Toast.makeText(getApplicationContext(), "LO SENTIMOS, SU NO TELEFóNICO NO PUEDE SER MENOR A 10 NÚMEROS", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD", Toast.LENGTH_LONG).show();
                    insertUserNoRegistrado();
                }
            }
        });
    }

    public void insertUserNoRegistrado(){
        nombre = txtNombre.getText().toString();
        aPaterno = txtApaterno.getText().toString();
        aMaterno = txtAmaterno.getText().toString();
        direccionUsuario = txtDireccion.getText().toString();
        noConfianza = txtNoConfianza.getText().toString();

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Telefono",cargarInfoTelefono)
                .add("Nombre",nombre)
                .add("APaterno",aPaterno)
                .add("AMaterno",aMaterno)
                .add("Direccion",direccionUsuario)
                .add("TelefonoConfianza",noConfianza)
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare(); // to be able to make toast
                Toast.makeText(getApplicationContext(), "ERROR AL ENVIAR SU REGISTRO, FAVOR DE VERIFICAR SU CONEXCIÓN A INTERNET", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().toString();

                    FormRegistroUsuario.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
                            txtNombre.setText("");
                            txtApaterno.setText("");
                            txtAmaterno.setText("");
                            txtDireccion.setText("");
                            Intent i = new Intent(FormRegistroUsuario.this,ConfiguracionesAgitado.class);
                            startActivity(i);
                            finish();
                        }
                    });
                }

            }
        });

    }
}
