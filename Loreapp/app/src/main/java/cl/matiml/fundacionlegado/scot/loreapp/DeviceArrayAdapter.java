package cl.matiml.fundacionlegado.scot.loreapp;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DeviceArrayAdapter extends ArrayAdapter {
    public DeviceArrayAdapter(Context context, List<BluetoothDevice> dispositivos)
    {
        super(context, android.R.layout.simple_spinner_item, dispositivos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        BluetoothDevice device = (BluetoothDevice) getItem(position);

        convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);

        if (device == null) {
            return convertView;
        }

        TextView titulo = (TextView) convertView.findViewById(android.R.id.text1);
        titulo.setText(device.getName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = View.inflate(getContext(), android.R.layout.simple_spinner_dropdown_item, null);

        BluetoothDevice device = (BluetoothDevice) getItem(position);

        TextView tvText1 = (TextView)convertView.findViewById(android.R.id.text1);
        tvText1.setText(device.getName());
        return convertView;
    }
}
