package com.example.davidpascualpractica4.ui.crearLista;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class MyCrearListaRecyclerViewAdapter extends RecyclerView.Adapter<MyCrearListaRecyclerViewAdapter.ViewHolder> {

    private final List<MainActivity.Productos> mValues;
    private ArrayList<MainActivity.Productos> productos;
    public MyCrearListaRecyclerViewAdapter(ArrayList<MainActivity.Productos> items) {
        mValues = items;
    }

    public void setProductos(ArrayList<MainActivity.Productos> nuevosProductos) {
        this.productos = nuevosProductos;
        notifyDataSetChanged();
    }

    public void setImage() {
        notifyDataSetChanged();
        Log.d("update", "Imagen actualizada");
    }

    public ArrayList<MainActivity.Productos> getProductos() {
        return productos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.description.setText(mValues.get(position).getDescription());
        holder.nombre.setText(mValues.get(position).getNombre());
        holder.precio.setText(mValues.get(position).getPrecio());
        holder.photo.setImageBitmap(mValues.get(position).getPhoto());
        holder.cantidad.setText("");
        holder.cantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int cantidad = Integer.parseInt(s.toString());
                    holder.mItem.setCantidad(cantidad);
                } catch (NumberFormatException e) {
                    holder.mItem.setCantidad(0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final EditText cantidad;
        public final TextView nombre;
        public final TextView description;
        public final TextView precio;
        public final ImageView photo;
        public MainActivity.Productos mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            nombre = view.findViewById(R.id.Nombre);
            description = view.findViewById(R.id.DescripciondelProducto);
            precio=view.findViewById(R.id.Precio);
            photo = view.findViewById(R.id.photo);
            cantidad=view.findViewById(R.id.edPrecio);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nombre.getText() + "'";
        }
    }
}