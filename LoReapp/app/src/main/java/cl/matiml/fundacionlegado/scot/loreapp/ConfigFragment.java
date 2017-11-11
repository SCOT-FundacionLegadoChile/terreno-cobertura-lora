package cl.matiml.fundacionlegado.scot.loreapp;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ConfigFragment extends Fragment {

    public final static String DEVICE_ADDRESS = "cl.matiml.fundacionlegado.scot.loreapp.DEVICE_ADDRESS";
    public static final String DEFAULT_DEVICE_ADDRESS = "00:21:13:01:22:19";
    public static final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public final static String NODE_TYPE = "cl.matiml.fundacionlegado.scot.loreapp.NODE_TYPE";
    public final static String ENDPOINT = "cl.matiml.fundacionlegado.scot.loreapp.ENDPOINT";
    public final static String GATEWAY = "cl.matiml.fundacionlegado.scot.loreapp.GATEWAY";
    protected BluetoothAdapter btAdapter;
    protected Set<BluetoothDevice> pairedDevices;
    protected List<BluetoothDevice> devicesList;

    protected Spinner spinner;
    protected RadioButton endpointRadioButton;
    protected RadioButton gatewayRadioButton;

    protected SharedPreferences sharedPref;
    protected String nodeType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        nodeType = sharedPref.getString(NODE_TYPE, ENDPOINT);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        pairedDevices = btAdapter.getBondedDevices();
        devicesList = new ArrayList<>();
        if (pairedDevices.size()>0) {
            for(BluetoothDevice bt : pairedDevices) {
                devicesList.add(bt);
            }
        } else {
            Toast.makeText(getActivity(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        spinner = (Spinner) getView().findViewById(R.id.devicesSpinner);
        endpointRadioButton = (RadioButton) getView().findViewById(R.id.radioButton1);
        gatewayRadioButton  = (RadioButton) getView().findViewById(R.id.radioButton2);

        spinner.setAdapter(new DeviceArrayAdapter(getActivity(), devicesList));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final BluetoothDevice item = (BluetoothDevice) adapterView.getItemAtPosition(i);
                new AlertDialog.Builder(getContext())
                        .setTitle("Dispositivo Bluetooth")
                        .setMessage("¿Configurar " + item.getName() + " como dispositivo predeterminado?")
                        .setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(DEVICE_ADDRESS, item.getAddress());
                                editor.apply();
                                Toast.makeText(getActivity(), "device address: " + item.getAddress(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        View.OnClickListener radioButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nodeType = (view.getId() == R.id.radioButton1) ? ENDPOINT:GATEWAY;
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(NODE_TYPE, nodeType);
                editor.apply();
            }
        };
        endpointRadioButton.setOnClickListener(radioButtonClick);
        gatewayRadioButton.setOnClickListener(radioButtonClick);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}

