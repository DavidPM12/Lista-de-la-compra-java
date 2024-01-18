package com.example.davidpascualpractica4.ui.compartirLista;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.davidpascualpractica4.MainActivity;
import com.example.davidpascualpractica4.R;

import java.util.ArrayList;
import java.util.Collections;

public class ContactosFragment extends Fragment implements MyContactosAdapterRecyclerViewAdapter.OnItemClickListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private RecyclerView recyclerView;
    private static ArrayList<Integer> idsListasSeleccionadas = new ArrayList<>();

    private ArrayList<MainActivity.Contacto> contactosList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list2, container, false);
        recyclerView = view.findViewById(R.id.listas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SQLiteDatabase db = MainActivity.db;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            contactosList = obtenerContactos();
            actualizarRecyclerView();
        }
        return view;
    }

    private void actualizarRecyclerView() {
        MyContactosAdapterRecyclerViewAdapter adapter = new MyContactosAdapterRecyclerViewAdapter(contactosList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        MainActivity.Contacto contacto = contactosList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_opciones_contacto, null);
        builder.setView(dialogView);
        Button btnEnviarSMS = dialogView.findViewById(R.id.btnEnviarSMS);
        Button btnEnviarWhatsApp = dialogView.findViewById(R.id.btnEnviarWhatsApp);
        btnEnviarSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarSMS(contacto, idsListasSeleccionadas);
            }
        });
        btnEnviarWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarWhatsApp(contacto, idsListasSeleccionadas);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void enviarSMS(MainActivity.Contacto contacto, ArrayList<Integer> idsListasSeleccionadas) {
        try {
            String numeroContacto = contacto.getNumeros().get(0);
            if (numeroContacto != null && !numeroContacto.isEmpty()) {
                for (int idListaSeleccionada : idsListasSeleccionadas) {
                    String mensaje = construirMensaje(contacto, new ArrayList<>(Collections.singletonList(idListaSeleccionada)));
                    enviarMensajeSMS(numeroContacto, mensaje);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ERROR SMS", "Error al enviar SMS: " + e.getMessage());
        }
    }

    private void enviarWhatsApp(MainActivity.Contacto contacto, ArrayList<Integer> idsListasSeleccionadas) {
        try {
            String numeroContacto = contacto.getNumeros().get(0);
            for (int idListaSeleccionada : idsListasSeleccionadas) {
                String mensaje = construirMensaje(contacto, new ArrayList<>(Collections.singletonList(idListaSeleccionada)));
                enviarMensajeWhatsApp(numeroContacto, mensaje);
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al abrir la aplicación de WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarMensajeSMS(String numeroContacto, String mensaje) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + Uri.encode(numeroContacto)));
            intent.putExtra("sms_body", mensaje);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ERROR SMS", "Error al enviar SMS: " + e.getMessage());
        }
    }

    private void enviarMensajeWhatsApp(String numeroContacto, String mensaje) {
        try {
            String phoneNumberWithCountryCode =  numeroContacto;
            String url = String.format("https://api.whatsapp.com/send?phone=%s&text=%s",
                    phoneNumberWithCountryCode, Uri.encode(mensaje));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error al abrir la aplicación de WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }

    private String construirMensaje(MainActivity.Contacto contacto, ArrayList<Integer> idsListasSeleccionadas) {
        StringBuilder mensaje = new StringBuilder("Hola, aquí está la lista:\n");
        for (int idListaSeleccionada : idsListasSeleccionadas) {
            Log.d("LISTA IDS",""+idListaSeleccionada);
            ArrayList<MainActivity.Productos> productosLista = obtenerProductosLista(contacto, idListaSeleccionada);
            for (MainActivity.Productos producto : productosLista) {
                mensaje.append(producto.getNombre()).append(" - ").append(producto.getCantidad()).append(" unidades\n");
            }
        }
        Log.d("MENSAJE",mensaje.toString());
        return mensaje.toString();
    }
    private ArrayList<MainActivity.Productos> obtenerProductosLista(MainActivity.Contacto contacto, int idListaSeleccionada) {
        Log.d("ID_LISTA", "ID de lista seleccionada: " + idListaSeleccionada);
        ArrayList<MainActivity.Productos> productosLista = new ArrayList<>();
        SQLiteDatabase db = MainActivity.db;
        String consultaProductos = "SELECT NombreProducto, Cantidad " +
                "FROM listas_productos " +
                "WHERE idListas = ?";
        Cursor cursorProductos = db.rawQuery(consultaProductos, new String[]{String.valueOf(idListaSeleccionada)});
        if (cursorProductos != null && cursorProductos.moveToFirst()) {
            do {
                String nombreProducto = cursorProductos.getString(0);
                int cantidadProducto = cursorProductos.getInt(1);
                productosLista.add(new MainActivity.Productos(nombreProducto, cantidadProducto));
            } while (cursorProductos.moveToNext());

            cursorProductos.close();
        }
        return productosLista;
    }

    private ArrayList<MainActivity.Contacto> obtenerContactos() {
        ArrayList<MainActivity.Contacto> contactos = new ArrayList<>();
        ContentResolver contentResolver = requireContext().getContentResolver();
        String[] projection = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(0);
                String numero = cursor.getString(1);
                boolean existeContacto = false;
                for (MainActivity.Contacto existingContact : contactos) {
                    if (existingContact.getNombre().equals(nombre)) {
                        existingContact.addNumero(numero);
                        existeContacto = true;
                        break;
                    }
                }
                if (!existeContacto) {
                    MainActivity.Contacto newContact = new MainActivity().new Contacto(nombre, numero);
                    contactos.add(newContact);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return contactos;
    }

    public static void guardarIdListaSeleccionada(ArrayList<Integer> listaIds) {
        idsListasSeleccionadas.addAll(listaIds);
        Log.d("ID_LISTA", "IDs de listas seleccionadas guardados: " + idsListasSeleccionadas.toString());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                contactosList = obtenerContactos();
                actualizarRecyclerView();
            } else {
                Toast.makeText(requireContext(), "Permiso denegado para acceder a los contactos", Toast.LENGTH_SHORT).show();
            }
        }
    }
}