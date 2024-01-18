package com.example.davidpascualpractica4.ui.crearProducto;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.davidpascualpractica4.MainActivity;
import com.example.davidpascualpractica4.databinding.FragmentCrearproductoBinding;

public class CrearProductoActivity extends AppCompatActivity {
    private final int FOTO = 1;
    private static final long ID_LISTA = 1;
    FragmentCrearproductoBinding binding;
    ImageView portada;
    ImageButton guardar;
    Button foto;
    Bitmap bmpFoto;
    boolean hayFoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentCrearproductoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        guardar = binding.btnSave;
        portada = binding.portada;
        foto = binding.btnAddFoto;
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.Productos p = new MainActivity.Productos(
                        bmpFoto,
                        binding.edNombre.getText().toString(),
                        binding.edDescripcion.getText().toString(),
                        binding.edPrecio.getText().toString(),
                        "https://fp.cloud.riberadeltajo.es/listacompra/images/"
                );
                if (hayFoto) {
                    p.setPhoto(bmpFoto);
                }
                SQLiteDatabase db = MainActivity.db;
                insertarProductoEnBaseDeDatos(db, p);
                finish();
            }
        });
    }

    private void insertarProductoEnBaseDeDatos(SQLiteDatabase db, MainActivity.Productos producto) {
        ContentValues values = new ContentValues();
        values.put("nombre", producto.getNombre());
        values.put("descripcion", producto.getDescription());
        values.put("precio", producto.getPrecio());
        long idProducto = db.insert("productos", null, values);
        if (idProducto != -1) {
            ContentValues listaProductoValues = new ContentValues();
            listaProductoValues.put("id_lista", ID_LISTA);
            listaProductoValues.put("id_producto", idProducto);
            db.insert("listas_productos", null, listaProductoValues);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FOTO) {
            bmpFoto = (Bitmap) data.getExtras().get("data");
            portada.setImageBitmap(bmpFoto);
            hayFoto = true;
        }
    }
}