package com.example.davidpascualpractica4.ui.crearProducto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.davidpascualpractica4.MainActivity;
import com.example.davidpascualpractica4.R;
import com.example.davidpascualpractica4.databinding.FragmentCrearproductoBinding;

public class CrearProductoFragment extends Fragment {
    private static final int FOTO = 1;
    private FragmentCrearproductoBinding binding;
    private ImageView portada;
    private ImageButton guardar;
    private Button foto;
    private Bitmap bmpFoto;
    private boolean hayFoto = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCrearproductoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        guardar = binding.btnSave;
        portada = binding.portada;
        foto = binding.btnAddFoto;
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarProductoEnBaseDeDatos();
            }
        });
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFoto();
            }
        });
    }

    private void guardarProductoEnBaseDeDatos() {
        String nombre = binding.edNombre.getText().toString().trim();
        MainActivity.Productos producto = new MainActivity.Productos(
                bmpFoto,
                nombre,
                binding.edDescripcion.getText().toString(),
                binding.edPrecio.getText().toString(),
                "https://fp.cloud.riberadeltajo.es/listacompra/images/"
        );
        if (hayFoto) {
            producto.setPhoto(bmpFoto);
        }
        SQLiteDatabase db = MainActivity.db;
        insertarProductoEnBaseDeDatos(db, producto);
    }

    private void insertarProductoEnBaseDeDatos(SQLiteDatabase db, MainActivity.Productos producto) {
        EditText editTextNombre = requireView().findViewById(R.id.edNombre);
        String nombreProducto = editTextNombre.getText().toString();
        EditText editTextDescripcion = requireView().findViewById(R.id.edDescripcion);
        String descripcionProducto = editTextDescripcion.getText().toString();
        EditText editTextPrecio = requireView().findViewById(R.id.edPrecio);
        String precioProducto = editTextPrecio.getText().toString();
        int drawableId = R.drawable.agregar;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableId);
        MainActivity.Productos productonuevo = new MainActivity.Productos(bitmap,nombreProducto, descripcionProducto, precioProducto,"R.drawable.agregar");
        if(hayFoto){
            productonuevo.setPhoto(bmpFoto);
        }else{
            productonuevo.setPhoto(bitmap);
        }
        MainActivity.ITEMS.add(productonuevo);
        db.execSQL("INSERT INTO MisProductos (Nombre,Descripcion,Imagen,Precio) VALUES (?, ?,?,?);",
                new Object[]{nombreProducto, descripcionProducto,bmpFoto,precioProducto});
        Toast.makeText(getContext(), "Producto guardado exitosamente", Toast.LENGTH_SHORT).show();
    }

    private void tomarFoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, FOTO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FOTO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            }
        }
    }

    private void abrirCamara() {
        Intent intentTomarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentTomarFoto, FOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FOTO) {
            Bundle extras = data.getExtras();
            bmpFoto = (Bitmap) extras.get("data");
            portada.setImageBitmap(bmpFoto);
            hayFoto = true;
        }
    }
}
