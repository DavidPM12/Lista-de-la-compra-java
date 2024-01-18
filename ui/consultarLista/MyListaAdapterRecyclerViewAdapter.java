package com.example.davidpascualpractica4.ui.consultarLista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.davidpascualpractica4.MainActivity;
import com.example.davidpascualpractica4.R;

import java.util.ArrayList;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class MyListaAdapterRecyclerViewAdapter extends RecyclerView.Adapter<MyListaAdapterRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<MainActivity.Listas> mValues;
    private final OnItemClickListener mListener;

    public MyListaAdapterRecyclerViewAdapter(ArrayList<MainActivity.Listas> items, OnItemClickListener listener) {
        mValues = items;
        mListener = listener;
    }

    public interface OnItemClickListener {
        void MostrarClick(MainActivity.Listas item);
        void ModificarClick(MainActivity.Listas item);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.nombreLista.setText(mValues.get(position).getNombre());
        holder.idlista.setText(String.valueOf(mValues.get(position).getId()));
        holder.mView.findViewById(R.id.mostrar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.MostrarClick(holder.mItem);
            }
        });

        holder.mView.findViewById(R.id.modificar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.ModificarClick(holder.mItem);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView nombreLista;
        public final TextView idlista;
        public MainActivity.Listas mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            nombreLista = view.findViewById(R.id.nombredelproducto);
            idlista = view.findViewById(R.id.id);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nombreLista.getText() + "'";
        }
    }
}