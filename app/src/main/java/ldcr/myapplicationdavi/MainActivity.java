package ldcr.myapplicationdavi;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int READ_MSM_PERMISSION = 100;
    //Variables
    TextView msmMostrado;
    Button msmAMostrar;

    //Obtiene el mensaje
    Cursor verMsm;
    Uri uriMsm;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msmMostrado = (TextView) findViewById(R.id.txt1);
        msmAMostrar = (Button) findViewById(R.id.btn1);
        msmMostrado.setMovementMethod(new ScrollingMovementMethod());
        msmAMostrar.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                permisoAcceso();
                break;
        }
    }


    public void permisoAcceso() {
        //si API es 23 o mas

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //habilitar permiso
            int verificarPermisoReadMsm = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

            //se verifica si el permiso no existe
            if (verificarPermisoReadMsm != PackageManager.PERMISSION_GRANTED) {
                //se verifica si fue rechazado el permiso anteriormente
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                    //si se rechazo el permiso se muestra este mensaje
                    mostrarMensaje();
                } else {
                    //de lo contrario muestra la ventana para autorización
                    requestPermissions(new String[]{Manifest.permission.READ_SMS}, READ_MSM_PERMISSION);
                }

            } else {
                //si el permiso ya fue concedido abrimos el intent del buzón
                cargarMensaje();
            }

        } else {//Si la API es menor a 23 - se abre el intent del buzón
            cargarMensaje();
        }
    }

    public void cargarMensaje() {

        uriMsm = Uri.parse("content://sms/inbox");

        String addresDaviPlata = "85888";

        String[] projection = {
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY
        };

        String selectionClause = Telephony.Sms.ADDRESS + " = " + addresDaviPlata;


        verMsm = getContentResolver().query((uriMsm), projection, selectionClause, null, null);

        String msgInfo = "";

        if (verMsm.moveToFirst()) { /* false = no sms */
            do {
                for (int i = 0; i < verMsm.getColumnCount(); i++) {


                    msgInfo += "" + verMsm.getColumnName(i) + ":" + verMsm.getString(i) + "\n";
                }

                msmMostrado.setText(msgInfo);
            } while (verMsm.moveToNext());
        }

    }

    public void mostrarMensaje() {
        new AlertDialog.Builder(this)
                .setTitle("Autorización")
                .setMessage("Es necesario el permiso para poder acceder a los mensajes del dispositivo.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_SMS}, READ_MSM_PERMISSION);
                        }
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //mensaje acción cancelada
                mensajeAccionCancelada();
            }
        })
                .show();
    }


    public void mensajeAccionCancelada() {
        //Mostrar mensaje de la petición denegada
        Toast.makeText(getApplicationContext(), "Se ha rechazado la petición, por favor considere en aprobarla.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_MSM_PERMISSION:
                //Si el permiso a sido concedido abrimos el buzon de msm
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoAcceso();
                } else {
                    mensajeAccionCancelada();
                }
                break;
        }
    }

}
