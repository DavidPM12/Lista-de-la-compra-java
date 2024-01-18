package com.example.davidpascualpractica4.ui.crearLista;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.davidpascualpractica4.MainActivity;
import com.example.davidpascualpractica4.R;
import com.example.davidpascualpractica4.databinding.FragmentCrearlistaBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A fragment representing a list of Items.
 */
public class CrearListaFragment extends Fragment {

    // TODO: Customize parameter argument names
    private FragmentCrearlistaBinding binding;
    private static RecyclerView recycler;
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CrearListaFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CrearListaFragment newInstance(int columnCount) {
        CrearListaFragment fragment = new CrearListaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCrearlistaBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recycler = view.findViewById(R.id.listaproductos);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        MyCrearListaRecyclerViewAdapter adapter = new MyCrearListaRecyclerViewAdapter(new ArrayList<>());
        recycler.setAdapter(adapter);

        notificarCambioss();

        return view;
    }

    public static void notificarCambioss() {
        MyCrearListaRecyclerViewAdapter adapter = (MyCrearListaRecyclerViewAdapter) recycler.getAdapter();
        adapter.setImage();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (MainActivity.ITEMS.isEmpty()) {
            MainActivity.ITEMS.clear();
            MainActivity.loadProductsFromJSON(getContext());
        }
        recycler = view.findViewById(R.id.listaproductos);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        MyCrearListaRecyclerViewAdapter adapter = new MyCrearListaRecyclerViewAdapter((ArrayList<MainActivity.Productos>) MainActivity.ITEMS);
        recycler.setAdapter(adapter);

        Button guardarButton = view.findViewById(R.id.guardar);
        guardarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarListaEnBaseDeDatos();
            }
        });
    }

    private void guardarListaEnBaseDeDatos() {
        MyCrearListaRecyclerViewAdapter adapter = (MyCrearListaRecyclerViewAdapter) recycler.getAdapter();

        EditText editTextNombreLista = requireView().findViewById(R.id.nombrelista);
        String nombreLista = editTextNombreLista.getText().toString();
        SQLiteDatabase db = MainActivity.db;
        if (adapter != null) {
            adapter.setProductos((ArrayList<MainActivity.Productos>) MainActivity.ITEMS);
            ArrayList<MainActivity.Productos> productos = adapter.getProductos();
            if (productos != null && !productos.isEmpty()) {
                try {
                    db.beginTransaction();
                    Cursor c = db.rawQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM MisListas", null);
                    int nuevoIdLista = 1;
                    if (c.moveToFirst()) {
                        nuevoIdLista = c.getInt(0);
                    }
                    String fechaActual = obtenerFechaActual();
                    db.execSQL("INSERT INTO MisListas (id, Nombre, FechaGuardado) VALUES (?, ?, ?);",
                            new Object[]{nuevoIdLista, nombreLista, fechaActual});
                    for (MainActivity.Productos producto : productos) {
                        int cantidad = producto.getCantidad();
                        if (cantidad > 0) {
                            db.execSQL("INSERT INTO listas_productos (NombreProducto, idListas, Cantidad) VALUES (?, ?, ?);",
                                    new Object[]{producto.getNombre(), nuevoIdLista, cantidad});
                        }
                    }
                    db.setTransactionSuccessful();
                    Toast.makeText(getContext(), "Lista guardada exitosamente", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("Guardar Lista", "Error al guardar la lista: " + e.getMessage());
                } finally {
                    db.endTransaction();
                }
            }
        }
    }

    private String obtenerFechaActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}