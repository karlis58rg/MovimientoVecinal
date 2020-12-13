package mx.oax.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FormAddTelefono extends AppCompatActivity {
    Button btnEnviar;
    EditText txtNumeroUsuario;
    String numero;
    SharedPreferences share;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_add_telefono);
        solicitarPermisosCamera();

        txtNumeroUsuario = findViewById(R.id.txtTelefono);
        btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtNumeroUsuario.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"SU NÚMERO TELEFÓNICO ES NECESARIO PARA PROCESAR LA SOLICITUD",Toast.LENGTH_SHORT).show();
                }else if(txtNumeroUsuario.getText().length() < 10){
                    Toast.makeText(getApplicationContext(),"LO SENTIMOS SU NÚMERO TELEFÓNICO NO PUEDE SER MENOR A 10 DIGITOS",Toast.LENGTH_SHORT).show();
                }else{
                    getUser();
                }

            }
        });
    }

    /******************GET A LA BD***********************************/
    public void getUser() {
        numero = txtNumeroUsuario.getText().toString();

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/UsuarioRegistrado?telefono=" + numero)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(),"ERROR AL OBTENER LA INFORMACIÓN, POR FAVOR VERIFIQUE SU CONEXIÓN A INTERNET",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    myResponse = myResponse.replace('"',' ');
                    myResponse = myResponse.trim();
                    String resp = myResponse;
                    String valorUser = "false";
                    if(resp.equals(valorUser)){
                        Looper.prepare(); // to be able to make toast
                        Toast.makeText(getApplicationContext(), "LO SENTIMOS, CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else{
                       /* banderaLogin = 2;
                        respPass = resp;
                        Looper.prepare(); // to be able to make toast
                        guardarDatosUserLogin();
                        Intent i = new Intent(LoginUser.this,Reglamento.class);
                        startActivity(i);
                        finish();
                        Looper.loop();*/
                    }
                    Log.i("HERE", resp);
                }
            }

        });
    }

    //***************************** SE OPTIENEN TODOS LOS PERMISOS AL INICIAR LA APLICACIÓN *********************************//
    public void solicitarPermisosCamera() {
        if (ContextCompat.checkSelfPermission(FormAddTelefono.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormAddTelefono.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(FormAddTelefono.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormAddTelefono.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormAddTelefono.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FormAddTelefono.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO,Manifest.permission.CALL_PHONE}, 1000);

        }
    }
}
