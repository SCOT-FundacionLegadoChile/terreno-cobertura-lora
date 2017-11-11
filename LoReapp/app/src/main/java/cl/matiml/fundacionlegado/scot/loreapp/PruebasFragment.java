package cl.matiml.fundacionlegado.scot.loreapp;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

public class PruebasFragment extends Fragment {
    protected ListView pruebasList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pruebas, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pruebasList = (ListView) getView().findViewById(R.id.pruebas_list_view);

        File directory = getActivity().getExternalFilesDir(null);
        File[] file = directory.listFiles();
        String[] fileNames = new String[file.length];
        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i] = file[i].getName();
        }

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, fileNames);
        pruebasList.setAdapter(adapter);

        pruebasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileName = (String) parent.getItemAtPosition(position);
                File file = new File(getActivity().getExternalFilesDir(null)+ fileName);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(
                        Uri.fromFile(file),
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                //intent.setData(Uri.fromFile(file));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}