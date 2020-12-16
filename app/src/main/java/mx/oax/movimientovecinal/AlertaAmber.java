package mx.oax.movimientovecinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class AlertaAmber extends AppCompatActivity {
    EditText txtNombreAlerta,txtApaternoAlerta,txtAmaternoAlerta,txtEdad,txtNacionalidad,txtColorOjos,txtEstatura,txtComplexion,txtFechaNacimiento,txtFechaHechos,txtLugarHechos,txtDescripcionHechos;
    RadioGroup rgSexo;
    Button btnEnviarAlertaAmber;
    AlertDialog alert = null;
    ImageView pickFotoAvatar;
    CircleImageView avatar2;
    int banderaFoto = 0;
    String cadena;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta_amber);

        btnEnviarAlertaAmber = findViewById(R.id.btnEnviarAlertaAmber);
        rgSexo = findViewById(R.id.rgSexo);
        txtNombreAlerta = findViewById(R.id.txtNombreAlerta);
        txtApaternoAlerta = findViewById(R.id.txtApaternoAlerta);
        txtAmaternoAlerta = findViewById(R.id.txtAmaternoAlerta);
        txtEdad = findViewById(R.id.txtEdad);
        txtNacionalidad = findViewById(R.id.txtNacionalidad);
        txtColorOjos = findViewById(R.id.txtColorOjos);
        txtEstatura = findViewById(R.id.txtEstatura);
        txtComplexion = findViewById(R.id.txtComplexion);
        txtFechaNacimiento = findViewById(R.id.txtFechaNacimiento);
        txtFechaHechos = findViewById(R.id.txtFechaHechos);
        txtLugarHechos = findViewById(R.id.txtLugarHechos);
        txtDescripcionHechos = findViewById(R.id.txtDescripcionHechos);
        pickFotoAvatar = findViewById(R.id.pickFoto_Amber);
        avatar2 = findViewById(R.id.profile_image_Amber);

        pickFotoAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(AlertaAmber.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    banderaFoto = 1;
                    llamarItemAvatar();
                }
            }
        });

        btnEnviarAlertaAmber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AlertaAmber.this);
                builder.setMessage("EMERGENCIA ENVIADA")
                        .setCancelable(true);
                alert = builder.create();
                alert.show();
                txtNombreAlerta.setText("");
                txtApaternoAlerta.setText("");
                txtAmaternoAlerta.setText("");
                txtEdad.setText("");
                txtNacionalidad.setText("");
                txtColorOjos.setText("");
                txtEstatura.setText("");
                txtComplexion.setText("");
                txtFechaNacimiento.setText("");
                txtFechaHechos.setText("");
                txtLugarHechos.setText("");
                txtDescripcionHechos.setText("");
                avatar2.clearAnimation();
                pickFotoAvatar.setVisibility(View.VISIBLE);

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
            //imagen2();
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
}
