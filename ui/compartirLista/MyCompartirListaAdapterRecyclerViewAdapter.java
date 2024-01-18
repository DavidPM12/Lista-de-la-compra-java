package com.example.davidpascualpractica4.ui.compartirLista;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.davidpascualpractica4.MainActivity;
import com.example.davidpascualpractica4.R;

import java.util.ArrayList;
import java.util.HashSet;

public class MyCompartirListaAdapterRecyclerViewAdapter extends RecyclerView.Adapter<MyCompartirListaAdapterRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<MainActivity.Listas> mValues;
    private HashSet<Integer> selectedItems;
    private OnItemClickListener mListener;

    public MyCompartirListaAdapterRecyclerViewAdapter(ArrayList<MainActivity.Listas> items, MyCompartirListaAdapterRecyclerViewAdapter.OnItemClickListener listener) {
        mValues = items;
        mListener = listener;
        selectedItems = new HashSet<>();
    }

    public void setSelectedItems(ArrayList<Integer> items) {
        selectedItems.clear();
        selectedItems.addAll(items);
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position);
        } else {
            selectedItems.add(position);
        }
        notifyDataSetChanged();
    }

    @Override
    public MyCompartirListaAdapterRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item3, parent, false);
        return new MyCompartirListaAdapterRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.nombreLista.setText(mValues.get(position).getNombre());
        holder.idlista.setText(String.valueOf(mValues.get(position).getId()));
        holder.boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelection(holder.getAdapterPosition());
            }
        });
        if (selectedItems.contains(position)) {
            holder.boton.setBackgroundColor(Color.GREEN);
            holder.boton.setText("CANCELAR");
        } else {
            holder.boton.setBackgroundColor(Color.BLUE);
            holder.boton.setText("SELECCIONAR");
        }
    }
    private void handleSelection(int position) {
        toggleSelection(position);
        mListener.SeleccionarClick(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView nombreLista;
        public final TextView idlista;
        public final Button boton;
        public MainActivity.Listas mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            nombreLista = view.findViewById(R.id.nombredelproducto);
            idlista = view.findViewById(R.id.id);
            boton = view.findViewById(R.id.seleccionar);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nombreLista.getText() + "'";
        }
    }

    public interface OnItemClickListener {
        void SeleccionarClick(MainActivity.Listas item);
    }
}