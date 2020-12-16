package mx.oax.movimientovecinal;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReporteEmergencias extends Fragment implements OnMapReadyCallback {
    /**************** MAPA*****************/
    private OnFragmentInteractionListener mListener;
    GoogleMap map;
    Boolean actul_posicion = true;
    Marker marker = null;
    Double lat_origen, lon_origen;
    TextView tv_add;
    LatLng aux;
    Location aux_loc;
    String direccion1,municipio,estado;
    ScrollView scroll;

    /************** EVENTOS*****************/
    EditText descEmergencia;
    ImageView photo,video,audio,imgImagen;
    VideoView videoViewImage;
    Chronometer tiempo;
    Button btnSendEmergencia;
    int bandera = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    String cadena,cadenaVideo,cadenaAudio,mediaPath;


    public ReporteEmergencias() {
        // Required empty public constructor
    }

    public static ReporteEmergencias newInstance(String param1, String param2) {
        ReporteEmergencias fragment = new ReporteEmergencias();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_reporte_emergencias, container, false);
        scroll= view.findViewById(R.id.scrollMap);

        photo = view.findViewById(R.id.imgImagen);
        video = view.findViewById(R.id.imgVideo);
        audio= view.findViewById(R.id.imgAudio);

        imgImagen = view.findViewById(R.id.viewImage);
        videoViewImage = view.findViewById(R.id.viewVideo);

        /*recordAu = view.findViewById(R.id.btnRecordAudio);
        playAu = view.findViewById(R.id.btnPlayAudio);
        detenerAudio = view.findViewById(R.id.btnDetener);*/

        tiempo = view.findViewById(R.id.timer);

        btnSendEmergencia = view.findViewById(R.id.btnEnviarEmergencia);

        descEmergencia = view.findViewById(R.id.txtEmergencia);

        imgImagen.setVisibility(view.GONE);
        videoViewImage.setVisibility(view.GONE);
        tiempo.setVisibility(View.GONE);
        //recordAu.setVisibility(view.GONE);
        //detenerAudio.setVisibility(view.GONE);
        //playAu.setVisibility(view.GONE);


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bandera == 2 || bandera == 3){
                    videoViewImage.clearAnimation();
                    //resetChronometro();
                }
                bandera = 1;
                imgImagen.setVisibility(view.VISIBLE);
                videoViewImage.setVisibility(view.GONE);
                /*playAu.setVisibility(View.GONE);
                recordAu.setVisibility(View.GONE);
                detenerAudio.setVisibility(View.GONE);
                tiempo.setVisibility(View.GONE);*/
                llamarItem();
                Toast.makeText(getActivity(), "PROCESANDO SU SOLICITUD DE IMAGEN", Toast.LENGTH_SHORT).show();
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bandera == 1 || bandera == 3){
                    imgImagen.clearAnimation();
                    //resetChronometro();
                }
                bandera = 2;
                videoViewImage.setVisibility(view.VISIBLE);
                imgImagen.setVisibility(view.GONE);
               /* playAu.setVisibility(View.GONE);
                recordAu.setVisibility(View.GONE);
                detenerAudio.setVisibility(View.GONE);
                tiempo.setVisibility(View.GONE);*/
                llamarItemVideo();
                Toast.makeText(getActivity(), "PROCESANDO SU SOLICITUD DE VIDEO", Toast.LENGTH_SHORT).show();

            }
        });
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bandera == 1 || bandera == 2){
                    imgImagen.clearAnimation();
                    videoViewImage.clearAnimation();
                }
                bandera = 3;
               /* recordAu.setVisibility(view.VISIBLE);
                detenerAudio.setVisibility(View.VISIBLE);
                detenerAudio.setEnabled(false);
                tiempo.setVisibility(View.VISIBLE);*/
                videoViewImage.setVisibility(view.GONE);
                imgImagen.setVisibility(view.GONE);
                //Toast.makeText(getActivity(), "PROCESANDO SU SOLICITUD DE AUDIO", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "EN ESTOS MOMENTOS ESTA OPCIÓN NO ESTA DISPONIBLE", Toast.LENGTH_SHORT).show();
            }
        });

        btnSendEmergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(descEmergencia.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(),"EL CAMPO **DESCRIPCIÓN DE EMERGENCIA** ES OBLIGATORIO",Toast.LENGTH_SHORT).show();
                }else {
                    if (bandera == 1){
                        imgImagen.setVisibility(View.GONE);
                        imgImagen.clearAnimation();
                    }
                    if(bandera == 2){
                        videoViewImage.setVisibility(View.GONE);
                        videoViewImage.clearAnimation();
                    }
                    descEmergencia.setText("");
                    Toast.makeText(getActivity(),"SUS REPORTE HA SIDO ENVIADO",Toast.LENGTH_SHORT).show();

                }

            }
        });

        //*************************** SE MUESTRA EL MAPA ****************************************//
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final String[] permisos = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        map = googleMap;
        tv_add = (TextView) getActivity().findViewById(R.id.tv_miadres);
        map.setMyLocationEnabled(true);

        //activar el boton " ubicación" de mapa y regrese la dirección actual/////
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                actul_posicion=true;
                return false;
            }
        });
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                aux_loc = location;

                if(actul_posicion) {
                    iniciar(location);
                }
            }
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                scroll.requestDisallowInterceptTouchEvent(true);
                Toast.makeText(getActivity(), "Se moverá el marcador", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng neww = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                mi_ubi(neww);
            }
        });
    }

    public String mi_ubi(LatLng au){
        String address = "";

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(au.latitude, au.longitude, 1);
            address = addresses.get(0).getAddressLine(0);
            address = "Dirección: \n" + address;
            tv_add.setText(address);
            Address DirCalle = addresses.get(0);
            direccion1 = DirCalle.getAddressLine(0);
            municipio = DirCalle.getLocality();
            estado = DirCalle.getAdminArea();
            if(municipio != null){
                municipio = DirCalle.getLocality();
            }else{
                municipio = "SIN INFORMACIÓN";
            }

        } catch (IOException e){

            e.printStackTrace();
        }

        return address;
    }

    public void iniciar(Location location){
        tv_add.setText("");

        if(marker != null){
            marker.remove();
        }

        lat_origen = location.getLatitude();
        lon_origen = location.getLongitude();
        actul_posicion = false;

        LatLng mi_posicion = new LatLng(lat_origen, lon_origen);

        marker = map.addMarker(new MarkerOptions().position(mi_posicion).title("Mi posición!").draggable(true));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat_origen, lon_origen)).zoom(14).bearing(15).build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        aux = new LatLng(lat_origen, lon_origen);

        mi_ubi(aux);
    }

    //********************************** IMAGEN ***********************************//
    //****************************** ABRE LA CAMARA ***********************************//
    private  void llamarItem(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }
    //********************************** VIDEO ***********************************//
    //****************************** PARA UTILIZACIÓN DEL VIDEO ***********************************//

    private  void llamarItemVideo(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,7);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent,REQUEST_VIDEO_CAPTURE);
        }
    }

    //********************************** ACCIÓN PARA AUDIO, VIDEO E IMAGEN ***********************************//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(bandera == 1){
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imgImagen.setImageBitmap(imageBitmap);
                imagen();

            }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_CANCELED){
                imgImagen.setVisibility(View.GONE);
                imgImagen.clearAnimation();
            }
        }else if(bandera == 2){
            super.onActivityResult(requestCode,resultCode,data);
            try
            {
                if (requestCode == 0 && resultCode == Activity.RESULT_OK && null != data)
                {
                    Toast.makeText(getActivity(), "EL CODIGO NO ES DE VIDEO", Toast.LENGTH_LONG).show();

                }else if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_CANCELED){
                    videoViewImage.setVisibility(View.GONE);
                    videoViewImage.clearAnimation();
                }
                else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK )
                {
                    Uri videoUri = data.getData();
                    videoViewImage.setVideoURI(videoUri);
                    Log.i("HERE", "PRIMER PARTE DONDE TRAE LA URI");

                    // SELECCIÓN DEL VIDEO ********************

                    Uri selectedVideo = data.getData();
                    String[] filePathColum = {MediaStore.Video.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedVideo,filePathColum,null,null,null);
                    assert cursor != null;
                    cursor.moveToFirst();
                    Log.i("HERE", "GRABANDO");

                    //RUTA FISICA DEL VIDEO *******************

                    int columIndex = cursor.getColumnIndex(filePathColum[0]);
                    mediaPath = cursor.getString(columIndex);
                    //txtResVideo.setText(mediaPath);
                    cursor.close();

                    // VISTA PREVIA DEL VIDEO DESDE UNA RUTA FISICA ************************

                    videoViewImage.setVideoURI(selectedVideo);
                    MediaController mediaController = new MediaController(this.getActivity());
                    videoViewImage.setMediaController(mediaController);
                    mediaController.setAnchorView(videoViewImage);
                    Log.i("HERE", "VISTA PREVIA");

                    //CONVERTIR  VIDEO A BASE64 **************************

                    InputStream inputStream = null;
                    String encodedString = null;

                    try
                    {
                        inputStream = new FileInputStream(mediaPath);

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    byte[] bytes;
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    try
                    {
                        while ((bytesRead = inputStream.read(buffer)) != -1)
                        {
                            output.write(buffer,0,bytesRead);

                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    bytes = output.toByteArray();
                    encodedString = Base64.encodeToString(bytes,Base64.DEFAULT);
                    cadenaVideo = encodedString;
                    //rest.setText(encodedString);

                }else
                {
                    Toast.makeText(getActivity(),"VIDEO EN CONSTRUCCIÓN",Toast.LENGTH_SHORT);
                }

            }catch (Exception e)
            {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    //********************************** SE CONVIERTE A BASE64 ***********************************//
    private void imagen()
    {
        imgImagen.buildDrawingCache();
        Bitmap bitmap = imgImagen.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70,baos);
        byte[] imgBytes = baos.toByteArray();
        String imgString = android.util.Base64.encodeToString(imgBytes, android.util.Base64.NO_WRAP);
        cadena = imgString;

        imgBytes = android.util.Base64.decode(imgString, android.util.Base64.NO_WRAP);
        Bitmap decoded= BitmapFactory.decodeByteArray(imgBytes,0,imgBytes.length);
        imgImagen.setImageBitmap(decoded);
        System.out.print("IMAGEN" + imgImagen);
    }
}
