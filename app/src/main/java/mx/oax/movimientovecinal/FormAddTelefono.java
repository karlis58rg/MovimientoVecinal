package mx.oax.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FormAddTelefono extends AppCompatActivity {
    Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_add_telefono);
        solicitarPermisosCamera();

        btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FormAddTelefono.this,FormRegistroUsuario.class);
                startActivity(i);
                finish();
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
