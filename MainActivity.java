package com.example.davidpascualpractica4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.davidpascualpractica4.databinding.ActivityMainBinding;
import com.example.davidpascualpractica4.ui.crearLista.CrearListaFragment;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public static List<MainActivity.Productos> ITEMS = new ArrayList<MainActivity.Productos>();
    public static List<MainActivity.Listas> ITEMS2 = new ArrayList<MainActivity.Listas>();
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniciarBD();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_nueva_lista, R.id.nav_consultar, R.id.nav_crear, R.id.nav_compartir)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public static void loadProductsFromJSON(final Context c) {
        String json = null;
        try {
            InputStream is = c.getAssets().open("listaproductos.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);
            JSONArray productList = jsonObject.getJSONArray("productos");

            for (int i = 0; i < productList.length(); i++) {
                JSONObject jsonProduct = productList.getJSONObject(i);
                String nombre = jsonProduct.getString("nombre");
                String description = jsonProduct.getString("descripcion");
                String precio = jsonProduct.getString("precio");
                String nombreimagen = jsonProduct.getString("imagen");
                String imageUrl = "https://fp.cloud.riberadeltajo.es/listacompra/images/" + nombreimagen;
                Productos producto = new Productos(null, nombre, description, precio, imageUrl);
                ITEMS.add(producto);
                db.execSQL("INSERT OR IGNORE INTO MisProductos (Nombre, Descripcion, Imagen, Precio) VALUES (?, ?, ?, ?);",
                        new Object[]{nombre, description, imageUrl, precio});
            }
            ITEMS = ordenProductos();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            for (final Productos producto : ITEMS) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(producto.getImageUrl());
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(input);
                            producto.setPhoto(bitmap);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    CrearListaFragment.notificarCambioss();
                                }
                            });
                        } catch (IOException e) {
                            Log.e("ProductosContent", "Error al cargar la imagen para: " + producto.getNombre(), e);
                        }
                    }
                });
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Productos> ordenProductos() {
        SQLiteDatabase db = MainActivity.db;
        List<Productos> ordenProductos = new ArrayList<>();
        String query = "SELECT p.Nombre, p.Descripcion, p.Imagen, p.Precio, COALESCE(SUM(lp.Cantidad), 0) AS TotalCantidad " +
                "FROM MisProductos p " +
                "LEFT JOIN listas_productos lp ON lp.NombreProducto = p.Nombre " +
                "GROUP BY p.Nombre " +
                "ORDER BY TotalCantidad DESC";
        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                String nombreProducto = cursor.getString(0);
                String descripcion = cursor.getString(1);
                String imageUrl = cursor.getString(2);
                String precio = cursor.getString(3);
                int totalCantidad = cursor.getInt(4);

                Productos producto = new Productos(null, nombreProducto, descripcion, precio, imageUrl);
                producto.setCantidad(totalCantidad);
                ordenProductos.add(producto);
            }
        }
        return ordenProductos;
    }


    public static class Listas{
        private int id;
        private String nombre;

        public Listas(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return "Listas{" +
                    "id=" + id +
                    ", nombre='" + nombre + '\'' +
                    '}';
        }
    }

    public class Contacto {
        private String nombre;
        private List<String> numeros;

        public Contacto(String nombre, String numero) {
            this.nombre = nombre;
            this.numeros = new ArrayList<>();
            this.numeros.add(numero);
        }

        public String getNombre() {
            return nombre;
        }

        public List<String> getNumeros() {
            return numeros;
        }

        public void addNumero(String numero) {
            this.numeros.add(numero);
        }
    }
    public static class Productos {
        private Bitmap photo;
        private String nombre;
        private String description;
        private String precio;
        private String imageUrl;
        private int id;
        private int cantidad;

        public Productos(Bitmap photo, String nombre, String description, String precio, String imageUrl) {
            this.photo = photo;
            this.nombre = nombre;
            this.description = description;
            this.precio = precio;
            this.imageUrl = imageUrl;
            this.cantidad = 0;

        }

        public Productos(String nombreProducto, int cantidadProducto) {
            this.nombre=nombreProducto;
            this.cantidad=cantidadProducto;
        }

        public int getCantidad() {
            return cantidad;
        }
        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
        public String getImageUrl() {
            return imageUrl;
        }

        public Bitmap getPhoto() {
            return photo;
        }
        public void setPhoto(Bitmap photo) {
            this.photo = photo;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
        public String getDescription() {
            return description;
        }

        public String getPrecio() {
            return precio;
        }

        public void setPrecio(String precio) {
            this.precio = precio;
        }
        @Override
        public String toString() {
            return "Productos{" +
                    "photo=" + photo +
                    ", nombre='" + nombre + '\'' +
                    ", description='" + description + '\'' +
                    ", precio='" + precio + '\'' +
                    '}';
        }
    }

    public void iniciarBD() {
        db = openOrCreateDatabase("MyApplication", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS MisProductos(" +
                "Nombre VARCHAR(100) PRIMARY KEY," +
                "Descripcion VARCHAR(100)," +
                "Imagen BLOB," +
                "Precio VARCHAR(100));");
        db.execSQL("CREATE TABLE IF NOT EXISTS MisListas(" +
                "id NUMBER(5) PRIMARY KEY," +
                "Nombre VARCHAR(100)," +
                "FechaGuardado TEXT" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS listas_productos (" +
                "NombreProducto VARCHAR(100)," +
                "idListas NUMBER(5)," +
                "Cantidad INTEGER," +
                "PRIMARY KEY (NombreProducto, idListas)," +
                "FOREIGN KEY (NombreProducto) REFERENCES MisProductos(Nombre)," +
                "FOREIGN KEY (idListas) REFERENCES MisListas(id)" +
                ");");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}