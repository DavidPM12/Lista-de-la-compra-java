package com.example.davidpascualpractica4.ui.consultarLista;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.davidpascualpractica4.MainActivity;
import com.example.davidpascualpractica4.R;

import java.util.ArrayList;

public class MyProductosModificarAdapterRecyclerViewAdapter extends RecyclerView.Adapter<MyProductosModificarAdapterRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<MainActivity.Productos> mValues;

    public MyProductosModificarAdapterRecyclerViewAdapter(ArrayList<MainActivity.Productos> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item5, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MainActivity.Productos producto = mValues.get(position);
        holder.mItem = mValues.get(position);
        holder.Nombre.setText(mValues.get(position).getNombre());
        holder.Descripcion.setText(mValues.get(position).getDescription());
        holder.Precio.setText(mValues.get(position).getPrecio());
        holder.Cantidad.setText(""+mValues.get(position).getCantidad());
        holder.photo.setImageBitmap(mValues.get(position).getPhoto());
        if (producto.getPhoto() != null) {
            holder.photo.setImageBitmap(producto.getPhoto());
        } else {
            holder.photo.setImageResource(R.drawable.agregar);
        }
        holder.Cantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable s) {
                String nuevaCantidadString = s.toString();
                if (!nuevaCantidadString.isEmpty()) {
                    int nuevaCantidad = Integer.parseInt(nuevaCantidadString);
                    mValues.get(position).setCantidad(nuevaCantidad);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView Nombre;
        public final TextView Descripcion;
        public final TextView Precio;
        public final EditText Cantidad;
        public final ImageView photo;

        public MainActivity.Productos mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            Nombre = view.findViewById(R.id.Nombre);
            Descripcion = view.findViewById(R.id.DescripciondelProducto);
            Precio = view.findViewById(R.id.Precio);
            Cantidad = view.findViewById(R.id.edPrecio);
            photo=view.findViewById(R.id.photo);

        }
    }
}