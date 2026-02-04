package com.example.pm01p2026;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pm01p2026.Configuracion.SQLiteConexion;
import com.example.pm01p2026.Configuracion.Transacciones;

public class MainActivity extends AppCompatActivity {


    EditText nombres, apellidos, edad, correo;
    ImageView foto;
    Button agregar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        nombres = (EditText) findViewById(R.id.Nombre);
        apellidos = (EditText) findViewById(R.id.Apellido);
        edad = (EditText) findViewById(R.id.Edad);
        correo = (EditText) findViewById(R.id.Correo);
        agregar = (Button) findViewById(R.id.button);

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Hola",Toast.LENGTH_LONG).show();

                AddPerson();


            }
        });
    }
    private void AddPerson()
    {
        try {
            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.dbname, null,Transacciones.dbversion);
            SQLiteDatabase db = conexion.getWritableDatabase();

            ContentValues valores  = new ContentValues();
            valores.put(Transacciones.nombre, nombres.getText().toString());
            valores.put(Transacciones.apellido, apellidos.getText().toString());
            valores.put(Transacciones.edad, edad.getText().toString());
            valores.put(Transacciones.correo, correo.getText().toString());
            valores.put(Transacciones.foto, "");

            Long resultado = db.insert(Transacciones.tbpersons, Transacciones.id,valores);

            Toast.makeText(getApplicationContext(), "Registro ingresado" + resultado.toString(),
                    Toast.LENGTH_LONG).show();

            db.close();

            LimpiarPantalla();

        }catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"OCURRIO UN ERORR" + ex.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
    private void LimpiarPantalla()
    {
        nombres.setText("");
        apellidos.setText("");
        edad.setText("");
        correo.setText("");
        nombres.requestFocus();



    };
}