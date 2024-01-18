package com.example.davidpascualpractica4.ui.compartirLista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.davidpascualpractica4.MainActivity;
import com.example.davidpascualpractica4.R;

import java.util.ArrayList;

public class MyContactosAdapterRecyclerViewAdapter extends RecyclerView.Adapter<MyContactosAdapterRecyclerViewAdapter.ContactoViewHolder> {
    private ArrayList<MainActivity.Contacto> contactosList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public MyContactosAdapterRecyclerViewAdapter(ArrayList<MainActivity.Contacto> contactosList, OnItemClickListener listener) {
        this.contactosList = contactosList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item6, parent, false);
        return new ContactoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        MainActivity.Contacto contacto = contactosList.get(position);
        holder.bind(contacto, listener);
    }

    @Override
    public int getItemCount() {
        return contactosList.size();
    }

    static class ContactoViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreTextView;
        private TextView numeroTextView;
        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreContacto);
            numeroTextView = itemView.findViewById(R.id.telefonoContacto);
        }
        public void bind(final MainActivity.Contacto contacto, final OnItemClickListener listener) {
            nombreTextView.setText(contacto.getNombre());
            numeroTextView.setText(String.valueOf(contacto.getNumeros().get(0)));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}