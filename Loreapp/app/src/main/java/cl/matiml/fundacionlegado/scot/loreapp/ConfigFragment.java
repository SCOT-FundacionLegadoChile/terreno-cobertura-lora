package cl.matiml.fundacionlegado.scot.loreapp;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConfigFragment extends Fragment {

    public final static String DEVICE_ADDRESS = "cl.matiml.fundacionlegado.scot.loreapp.DEVICE_ADDRESS";
    public final static String NODE_TYPE = "cl.matiml.fundacionlegado.scot.loreapp.NODE_TYPE";
    public final static String ENDPOINT = "cl.matiml.fundacionlegado.scot.loreapp.ENDPOINT";
    public final static String GATEWAY = "cl.matiml.fundacionlegado.scot.loreapp.GATEWAY";
    protected BluetoothAdapter btAdapter;
    private Set<BluetoothDevice> pairedDevices;

    protected ListView deviceListView;
    protected View rootView;
    protected Button endpointButton;
    protected Button gatewayButton;

    protected SharedPreferences sharedPref;
    protected String nodeType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        nodeType = sharedPref.getString(NODE_TYPE, ENDPOINT);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onStart() {
        super.onStart();

        /* BT Devices */
        pairedDevices = btAdapter.getBondedDevices();
        List list = new ArrayList<BluetoothDevice>();

        if (pairedDevices.size()>0) {
            for(BluetoothDevice bt : pairedDevices) {
                list.add(bt);
            }
        } else {
            Toast.makeText(getActivity(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        /* Spinner */
        Spinner spinner = (Spinner) getView().findViewById(R.id.devicesSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice item = (BluetoothDevice) adapterView.getItemAtPosition(i);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(DEVICE_ADDRESS, item.getAddress());
                editor.commit();
                Toast.makeText(getActivity(), "device address: " + item.getAddress(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        DeviceArrayAdapter dataAdapter = new DeviceArrayAdapter(getActivity(), list);
        spinner.setAdapter(dataAdapter);

        /* Tipo de nodo */
        endpointButton = (Button) getView().findViewById(R.id.endpoint_button);
        gatewayButton  = (Button) getView().findViewById(R.id.gateway_button);
        configNodeTypeUI(nodeType);

        final View.OnClickListener nodeTypeClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nodeType = (nodeType == ENDPOINT) ? GATEWAY:ENDPOINT;
                configNodeTypeUI(nodeType);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(NODE_TYPE, nodeType);
                editor.commit();
            }
        };
        endpointButton.setOnClickListener(nodeTypeClick);
        gatewayButton.setOnClickListener(nodeTypeClick);
    }



    protected void configNodeTypeUI(String nodeType) {
        if (nodeType == ENDPOINT) {
            endpointButton.setEnabled(false);
            gatewayButton.setEnabled(true);
        } else if(nodeType == GATEWAY) {

            endpointButton.setEnabled(true);
            gatewayButton.setEnabled(false);
        }
    }
}

