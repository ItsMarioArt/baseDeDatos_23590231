package com.example.basededatos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {
    private EditText ed1, ed2, ed3, ed4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inicializar los EditText después de setContentView
        ed1 = (EditText) findViewById(R.id.nControl);
        ed2 = (EditText) findViewById(R.id.nombre);
        ed3 = (EditText) findViewById(R.id.semestre);
        ed4 = (EditText) findViewById(R.id.carrera);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void altas(View view){
        if (validarCampos()) {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"administracion", null, 1);
            SQLiteDatabase bd = admin.getWritableDatabase();

            String ncontrol = ed1.getText().toString();
            String nombre = ed2.getText().toString();
            String semestre = ed3.getText().toString();
            String carrera = ed4.getText().toString();

            ContentValues registro = new ContentValues();
            registro.put("ncontrol", ncontrol);
            registro.put("nombre", nombre);
            registro.put("semestre", semestre);
            registro.put("carrera", carrera);

            // los inserto en la base de datos
            long resultado = bd.insert("usuario", null, registro);
            bd.close();

            if (resultado != -1) {
                Toast.makeText(this, "Datos del usuario cargados.", Toast.LENGTH_SHORT).show();
                limpiar();
            } else {
                Toast.makeText(this, "Error al guardar. Posible número de control duplicado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para validar campos
    private boolean validarCampos() {
        if (ed1.getText().toString().isEmpty()) {
            Toast.makeText(this, "Debe ingresar un número de control", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ed2.getText().toString().isEmpty()) {
            Toast.makeText(this, "Debe ingresar un nombre", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ed3.getText().toString().isEmpty()) {
            Toast.makeText(this, "Debe ingresar un semestre", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ed4.getText().toString().isEmpty()) {
            Toast.makeText(this, "Debe ingresar una carrera", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void limpiar(View view) {
        limpiar();
    }

    public void limpiar(){
        ed1.setText("");
        ed2.setText("");
        ed3.setText("");
        ed4.setText("");
        ed1.requestFocus();
    }

    public void consulta(View view) {
        String nControl = ed1.getText().toString();

        if (nControl.isEmpty()) {
            Toast.makeText(this, "Debe ingresar un número de control para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"administracion", null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        Cursor fila = bd.rawQuery("select nombre, semestre, carrera from usuario where ncontrol = ?",
                new String[]{nControl});
        if (fila.moveToFirst()) {
            ed2.setText(fila.getString(0));
            ed3.setText(fila.getString(1));
            ed4.setText(fila.getString(2));
        } else {
            Toast.makeText(this, "No existe ningún usuario con ese Nro. de Control", Toast.LENGTH_SHORT).show();
            limpiar();
        }
        fila.close();
        bd.close();
    }

    public void baja(View view) {
        String ncontrol = ed1.getText().toString();

        if (ncontrol.isEmpty()) {
            Toast.makeText(this, "Debe ingresar un número de control para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        // Borrar el usuario usando parámetros de consulta
        int cant = bd.delete("usuario", "ncontrol = ?", new String[]{ncontrol});
        bd.close();
        limpiar();

        if (cant == 1) {
            Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No existe usuario", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para modificar la información del usuario
    public void modificacion(View view) {
        String ncontrol = ed1.getText().toString();

        if (ncontrol.isEmpty()) {
            Toast.makeText(this, "Debe ingresar un número de control para modificar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarCampos()) {
            return;
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        String nombre = ed2.getText().toString();
        String semestre = ed3.getText().toString();
        String carrera = ed4.getText().toString();

        ContentValues registro = new ContentValues();
        // actualizamos con los nuevos datos, la información cambiada
        registro.put("nombre", nombre);
        registro.put("semestre", semestre);
        registro.put("carrera", carrera);

        int cant = bd.update("usuario", registro, "ncontrol = ?", new String[]{ncontrol});
        bd.close();

        if (cant == 1) {
            Toast.makeText(this, "Datos modificados con éxito", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No existe usuario", Toast.LENGTH_SHORT).show();
        }
    }
    /* fin del programa */
}