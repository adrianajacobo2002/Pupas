package sv.edu.catolica.pupas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class PartyDetailFragment extends Fragment {


    FloatingActionButton prueba;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_party_detail, container, false);

        prueba = vista.findViewById(R.id.floatingActionButton);



        prueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        return vista;
    }

    private void showAlertDialog() {

        LayoutInflater inflater = requireActivity().getLayoutInflater();


        View dialogView = inflater.inflate(R.layout.dialog_with_dropdown_layout, null);

        AutoCompleteTextView autoCompleteTextView = dialogView.findViewById(R.id.auto_complete_txt);
        // Configurar el Spinner

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.pupusa_types, android.R.layout.simple_dropdown_item_1line);

        autoCompleteTextView.setAdapter(adapter);

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .show();
    }





}