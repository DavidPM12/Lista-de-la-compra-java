package com.example.davidpascualpractica4.ui.compartirLista;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.davidpascualpractica4.MainActivity;
import com.example.davidpascualpractica4.R;
import com.example.davidpascualpractica4.databinding.FragmentCompartirBinding;

import java.util.ArrayList;

public class CompartirListaFragment extends Fragment implements MyCompartirListaAdapterRecyclerViewAdapter.OnItemClickListener {
    private FragmentCompartirBinding binding;
    private RecyclerView recycler;

    private ArrayList<Integer> selectedItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCompartirBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recycler = view.findViewById(R.id.listasparaCompartir);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        loadListsFromDatabase();

        if (!MainActivity.ITEMS2.isEmpty()) {
            MyCompartirListaAdapterRecyclerViewAdapter adapter = new MyCompartirListaAdapterRecyclerViewAdapter((ArrayList<MainActivity.Listas>) MainActivity.ITEMS2, this);
            recycler.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycler = view.findViewById(R.id.listasparaCompartir);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        if (recycler.getAdapter() == null) {
            loadListsFromDatabase();
            ArrayList<MainActivity.Listas> listas = (ArrayList<MainActivity.Listas>) MainActivity.ITEMS2;
            MyCompartirListaAdapterRecyclerViewAdapter adapter = new MyCompartirListaAdapterRecyclerViewAdapter(listas, this);
            recycler.setAdapter(adapter);
        }
        Button compartirLista = view.findViewById(R.id.compartirLista);
        compartirLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompartirClick();
            }
        });
    }

    public void SeleccionarClick(MainActivity.Listas item) {
        int selectedPosition = MainActivity.ITEMS2.indexOf(item);
        MyCompartirListaAdapterRecyclerViewAdapter adapter = (MyCompartirListaAdapterRecyclerViewAdapter) recycler.getAdapter();

        if (selectedItems.contains(selectedPosition)) {
            selectedItems.remove(Integer.valueOf(selectedPosition));
        } else {
            selectedItems.add(selectedPosition);
        }
        adapter.setSelectedItems(selectedItems);
    }

    private void CompartirClick() {
        if (!selectedItems.isEmpty()) {
            ArrayList<Integer> selectedListIds = new ArrayList<>();
            for (int position : selectedItems) {
                int idListaSeleccionada = MainActivity.ITEMS2.get(position).getId();
                selectedListIds.add(idListaSeleccionada);
            }
            ContactosFragment.guardarIdListaSeleccionada(selectedListIds);
            StringBuilder messageBuilder = new StringBuilder("Lista de compra:\n");
            for (int position : selectedItems) {
                messageBuilder.append("- ").append(MainActivity.ITEMS2.get(position).getNombre()).append("\n");
            }
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            Bundle bundle = new Bundle();
            bundle.putString("lista_compra", messageBuilder.toString());
            navController.navigate(R.id.contactosFragment, bundle);

            selectedItems.clear();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadListsFromDatabase() {
        MainActivity.ITEMS2.clear();
        Cursor cursor = MainActivity.db.rawQuery("SELECT id, Nombre FROM MisListas", null);
        int idColumnIndex = cursor.getColumnIndex("id");
        int nombreColumnIndex = cursor.getColumnIndex("Nombre");
        if (cursor.moveToFirst()) {
            do {
                if (idColumnIndex != -1 && nombreColumnIndex != -1) {
                    int id = cursor.getInt(idColumnIndex);
                    String nombre = cursor.getString(nombreColumnIndex);
                    MainActivity.Listas lista = new MainActivity.Listas(id, nombre);
                    MainActivity.ITEMS2.add(lista);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}