package com.example.davidpascualpractica4.ui.consultarLista;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.davidpascualpractica4.MainActivity;
import com.example.davidpascualpractica4.R;
import com.example.davidpascualpractica4.databinding.FragmentItemList2Binding;
import com.example.davidpascualpractica4.ui.crearLista.CrearListaFragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsultarListaFragment extends Fragment implements MyListaAdapterRecyclerViewAdapter.OnItemClickListener {

    private FragmentItemList2Binding binding;
    private RecyclerView recycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentItemList2Binding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recycler = view.findViewById(R.id.listas);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        sacarListasdeBasedeDatos();
        if (!MainActivity.ITEMS2.isEmpty()) {
            MyListaAdapterRecyclerViewAdapter adapter = new MyListaAdapterRecyclerViewAdapter((ArrayList<MainActivity.Listas>) MainActivity.ITEMS2, this);
            recycler.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycler = view.findViewById(R.id.listas);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        if (recycler.getAdapter() == null) {
            sacarListasdeBasedeDatos();
            ArrayList<MainActivity.Listas> listas = (ArrayList<MainActivity.Listas>) MainActivity.ITEMS2;
            MyListaAdapterRecyclerViewAdapter adapter = new MyListaAdapterRecyclerViewAdapter(listas, this);
            recycler.setAdapter(adapter);
        }
    }

    @Override
    public void MostrarClick(MainActivity.Listas item) {
        ArrayList<MainActivity.Productos> productos = getProductosDeLista(item.getId());
        mostrarProductos(productos, item.getNombre());
    }

    private void mostrarProductos(ArrayList<MainActivity.Productos> productos, String nombreLista) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Productos de la lista: " + nombreLista);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_item_list2, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.listas);

        MyProductosAdapterRecyclerViewAdapter adapter = new MyProductosAdapterRecyclerViewAdapter(productos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        builder.setView(dialogView);
        builder.setPositiveButton("Cerrar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private ArrayList<MainActivity.Productos> getProductosDeLista(int idLista) {
        ArrayList<MainActivity.Productos> productos = new ArrayList<>();
        Cursor cursor = MainActivity.db.rawQuery(
                "SELECT p.Nombre, p.Descripcion, p.Imagen, p.Precio, lp.Cantidad " +
                        "FROM listas_productos lp " +
                        "JOIN MisProductos p ON lp.NombreProducto = p.Nombre " +
                        "WHERE lp.idListas = ?",
                new String[]{String.valueOf(idLista)});
        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(0);
                String descripcion = cursor.getString(1);
                String imageUrl = cursor.getString(2);
                String precio = cursor.getString(3);
                int cantidad = cursor.getInt(4);
                MainActivity.Productos producto = new MainActivity.Productos(null, nombre, descripcion, precio, imageUrl);
                agregarImagen(producto);
                producto.setCantidad(cantidad);
                productos.add(producto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    private void agregarImagen(MainActivity.Productos producto) {
        Handler handler = new Handler(Looper.getMainLooper());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = null;
                    if (producto.getImageUrl() != null && !producto.getImageUrl().isEmpty()) {
                        URL url = new URL(producto.getImageUrl());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        bitmap = BitmapFactory.decodeStream(input);
                    } else if (producto.getPhoto() != null) {
                        bitmap = producto.getPhoto();
                    }
                    if (bitmap == null) {
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.agregar);
                    }
                    producto.setPhoto(bitmap);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            CrearListaFragment.notificarCambioss();
                        }
                    });
                } catch (IOException e) {
                }
            }
        });
    }

    @Override
    public void ModificarClick(MainActivity.Listas item) {
        ArrayList<MainActivity.Productos> productos = getProductosDeLista(item.getId());
        mostrarProductosModificar(productos, item.getNombre(), item.getId());
    }

    private void mostrarProductosModificar(ArrayList<MainActivity.Productos> productos, String nombre, int listaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Productos de la lista: " + nombre);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_modificarlista, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.listaproductos);

        Button guardar = dialogView.findViewById(R.id.guardar2);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (MainActivity.Productos producto : productos) {
                    actualizarCantidadEnBaseDeDatos(producto.getNombre(), producto.getCantidad(), listaId);
                }

            }
        });

        MyProductosModificarAdapterRecyclerViewAdapter adapter = new MyProductosModificarAdapterRecyclerViewAdapter(productos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        builder.setView(dialogView);
        builder.setPositiveButton("Cerrar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void actualizarCantidadEnBaseDeDatos(String nombreProducto, int nuevaCantidad, int listaId) {
        SQLiteDatabase db = MainActivity.db;

        String sqlQuery = "UPDATE listas_productos SET Cantidad = ? WHERE NombreProducto = ? AND idListas = ? ";
        String[] whereArgs = {String.valueOf(nuevaCantidad), nombreProducto, String.valueOf(listaId)};
        try {
            db.execSQL(sqlQuery, whereArgs);
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void sacarListasdeBasedeDatos() {
        MainActivity.ITEMS2.clear();
        Cursor cursor = MainActivity.db.rawQuery("SELECT id, Nombre FROM MisListas", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);

                MainActivity.Listas lista = new MainActivity.Listas(id, nombre);
                MainActivity.ITEMS2.add(lista);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
