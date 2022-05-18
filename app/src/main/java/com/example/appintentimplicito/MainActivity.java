package com.example.appintentimplicito;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Xml;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.net.URI;

public class MainActivity extends AppCompatActivity {
    private EditText etTelefono;
    private ImageButton btnLlamada,btnCamara;
    private String numeroTelf;
    //Código constante para servicio de llamada
    private final int PHONE_CODE = 591;
    //Código constante para activar la camara
    private final int CAMERA_CODE = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        incializarVistas();
        btnLlamada.setOnClickListener(view->{
            obtenerInformacion();
            activarServicioLlamada();
            //TODO
        });
        btnCamara.setOnClickListener(view -> {
            activarServicioCamara();
        });
    }

    private void activarServicioCamara() {
        Intent intentCamara = new Intent("android.media.caption.IMAGE_CAPTURE");
        //TODO ajustar apertura de cámara
        //startActivityForResult(intentCamara);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode)
        {
            case CAMERA_CODE:
                if(resultCode == Activity.RESULT_OK)
                {

                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void activarServicioLlamada() {
        if(!numeroTelf.isEmpty()) {
            //EVALUAN SI SU VERSION DE ANDROID ES MAYOR O IGUAL A LA VERSION DONDE EL SERVICIO DE LLAMADA CAMBIA SU FORMA DE TRABAJAR
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}/*Array de permisos*/,PHONE_CODE/*valor del código constante*/);
                //Versiones nuevas
            }
            else
            {
                //Versiones antiguas
                configVersionAntigua();
            }
        }
    }

    private void configVersionAntigua() {
        //Crear un Intent implícito
        //En el constructor configuramos la accion a realizar
        //El segundo parámetro es el uri que es como una url donde configuras tus parámetros que envias
        Intent intentLlamada = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"/*Esta cadena tel es importante para reconocer el número*/+numeroTelf));
        if(revisarPermisos(Manifest.permission.CALL_PHONE))
        {
            startActivity(intentLlamada);
        }

    }

    private void obtenerInformacion() {
        numeroTelf = etTelefono.getText().toString();
    }

    private void incializarVistas() {
        etTelefono = findViewById(R.id.etNumero);
        btnCamara = findViewById(R.id.btnCamara);
        btnLlamada = findViewById(R.id.btnLlamada);
    }
    //Evaluan permisos para versiones antiguas
    private boolean revisarPermisos(String permiso)
    {
        //Valor entero que reresenta el permiso requerido en nuestra aplicación
        int valorPermiso = this.checkCallingOrSelfPermission(permiso);
        return valorPermiso == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Verificar el servicio a evaluar
        switch(requestCode)
        {
            case PHONE_CODE:
                String permiso = permissions[0];
                int permisoOtorgado = grantResults[0];
                //Asegurarse que para las llamadas van a evaluar el permiso
                if(permiso.equals(Manifest.permission.CALL_PHONE))
                {
                    //Evaluar si el permiso ha sido otorgado o denegado
                    if(permisoOtorgado == PackageManager.PERMISSION_GRANTED)
                    {
                        Intent intentLlamada = new Intent(Intent.ACTION_CALL, Uri.parse(("tel:"+numeroTelf)));
                        startActivity(intentLlamada);
                    }
                    else{
                        Toast.makeText(this, "El permiso esta denegado", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
}