package mx.oax.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormRegistroUsuario extends AppCompatActivity {
    Button btnRegistrar;
    ImageView pickFotoAvatar;
    CircleImageView avatar2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    EditText txtNombre,txtApaterno,txtAmaterno,txtDireccion,txtNoConfianza;
    String nombre,aPaterno,aMaterno,direccionUsuario,noConfianza,cadena;
    String cargarInfoTelefono,cargarInfoNombre,cargarInfoApaterno,cargarInfoAmaterno,cargarInfoDireccion,cargarInfoNuc,cargarInfoIdVictima;
    int bandera = 0;
    int banderaUserRegistrado = 0;
    int banderaFoto = 0;
    SharedPreferences share;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_registro_usuario);

        txtNombre = findViewById(R.id.txtNombre);
        txtApaterno = findViewById(R.id.txtApaterno);
        txtAmaterno = findViewById(R.id.txtAmaterno);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtNoConfianza = findViewById(R.id.txtNoConfianza);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        pickFotoAvatar = findViewById(R.id.pickFoto);
        avatar2 = findViewById(R.id.profile_image);

        cargarDatos();
        if(cargarInfoNombre.equals("SIN INFORMACION")){
            bandera = 1;
            Toast.makeText(getApplicationContext(), "USUARIO NO REGISTRADO", Toast.LENGTH_LONG).show();
        }else{
            bandera = 2;
            banderaUserRegistrado = 1;
            guardarDatoRegistrado();
            Toast.makeText(getApplicationContext(), "USUARIO REGISTRADO", Toast.LENGTH_LONG).show();
            txtNombre.setText(cargarInfoNombre);
            txtApaterno.setText(cargarInfoApaterno);
            txtAmaterno.setText(cargarInfoAmaterno);
            txtDireccion.setText(cargarInfoDireccion);
            txtNombre.setEnabled(false);
            txtApaterno.setEnabled(false);
            txtAmaterno.setEnabled(false);
            txtDireccion.setEnabled(false);
        }

        pickFotoAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(FormRegistroUsuario.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    banderaFoto = 1;
                    llamarItemAvatar();
                }else{
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(FormRegistroUsuario.this);
                    dialogo1.setTitle("IMPORTANTE");
                    dialogo1.setMessage("SI NO ACEPTA LOS PERMISOS SOLICITADOS, LA APLICACION NO FUNCIONARA CORRECTAMENTE");
                    dialogo1.setPositiveButton("ENTERADO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            solicitarPermisosCamera();
                        }
                    });
                    dialogo1.show();
                }

            }
        });

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
                }else if(banderaFoto == 0) {
                   Toast.makeText(getApplicationContext(), "LO SENTIMOS, SU FOTOGRAFIA ES NECESARIA PARA EL USO DE ESTA APLICACIÓN", Toast.LENGTH_LONG).show();
               }else if(bandera == 1){
                   Toast.makeText(getApplicationContext(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS", Toast.LENGTH_LONG).show();
                   insertUserNoRegistrado();
                   insertImagen();
               }else{
                   Toast.makeText(getApplicationContext(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD, ESTO PUEDE TARDAR UNOS MINUTOS", Toast.LENGTH_LONG).show();
                   updateUserRegistrado();
                   insertImagen();
               }
            }
        });
    }

    //********************************** INSERT Y UPDATE AL SERVIDOR ***********************************//
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
                .url("http://187.174.102.142/AppMovimientoVecinal/api/UsuarioNoRegistrado")
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
                            guardarDatosUser();
                            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
                            txtNombre.setText("");
                            txtApaterno.setText("");
                            txtAmaterno.setText("");
                            txtDireccion.setText("");
                            txtNoConfianza.setText("");
                            Intent i = new Intent(FormRegistroUsuario.this,MenuEventos.class);
                            startActivity(i);
                            finish();
                        }
                    });
                }
            }
        });
    }
    public void updateUserRegistrado(){
        noConfianza = txtNoConfianza.getText().toString();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("TelefonoConfianza",noConfianza)
                .build();

        Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/Victimas?nucVictima="+cargarInfoNuc+"&idVictima="+cargarInfoIdVictima+"&telConfianza="+noConfianza)
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
                            txtNoConfianza.setText("");
                            Intent i = new Intent(FormRegistroUsuario.this,MenuEventos.class);
                            startActivity(i);
                            finish();
                        }
                    });
                }
            }
        });
    }
    //********************************** SE CONVIERTE LA IMAGEN A BASE64 ***********************************//
    private void llamarItemAvatar() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            avatar2.setImageBitmap(imageBitmap);
            imagen2();
            pickFotoAvatar.setVisibility(View.GONE);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Tu acción ha sido cancelada", Toast.LENGTH_SHORT).show();
            avatar2.clearAnimation();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void imagen2() {
        avatar2.buildDrawingCache();
        Bitmap bitmap = avatar2.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imgBytes = baos.toByteArray();
        String imgString = android.util.Base64.encodeToString(imgBytes, android.util.Base64.NO_WRAP);
        cadena = imgString;

        imgBytes = android.util.Base64.decode(imgString, android.util.Base64.NO_WRAP);
        Bitmap decoded = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        avatar2.setImageBitmap(decoded);
        System.out.print("IMAGEN" + avatar2);
    }
    //********************************** INSERTA IMAGEN AL SERVIDOR ***********************************//
    public void insertImagen() {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Description", cargarInfoTelefono+".jpg")
                .add("ImageData", cadena)
                .build();
        Request request = new Request.Builder()
                .url("http://187.174.102.142/AppMovimientoVecinal/api/MultimediaFotoUser/")
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
                    final String myResponse = response.body().toString();  /********** ME REGRESA LA RESPUESTA DEL WS ****************/
                    FormRegistroUsuario.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "REGISTRO ENVIADO CON EXITO", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    //*********************************************************************//
    public void cargarDatos() {
        share = getApplication().getSharedPreferences("main", Context.MODE_PRIVATE);
        cargarInfoTelefono = share.getString("TELEFONO", "");
        cargarInfoNombre = share.getString("NOMBRE", "SIN INFORMACION");
        cargarInfoApaterno = share.getString("APATERNO", "");
        cargarInfoAmaterno = share.getString("AMATERNO", "");
        cargarInfoDireccion = share.getString("DIRECCION", "");
        cargarInfoNuc = share.getString("NUC", "");
        cargarInfoIdVictima = share.getString("IDVICTIMA", "");
    }
    //***************************** SE OPTIENEN TODOS LOS PERMISOS AL INICIAR LA APLICACIÓN *********************************//
    public void solicitarPermisosCamera() {
        if (ContextCompat.checkSelfPermission(FormRegistroUsuario.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormRegistroUsuario.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(FormRegistroUsuario.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormRegistroUsuario.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FormRegistroUsuario.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FormRegistroUsuario.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO,Manifest.permission.CALL_PHONE}, 1000);

        }
    }
    //***************************** SE GUARDAN LOS DATOS DE LA PERSONA REGISTRADA *********************************//
    private void guardarDatosUser(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("NOMBRE",nombre);
        editor.putString("APATERNO",aPaterno);
        editor.putString("AMATERNO",aMaterno);
        editor.commit();
    }

    private void guardarDatoRegistrado(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("BANDERAUSERREGISTRADO",banderaUserRegistrado);
        editor.commit();
    }
}
