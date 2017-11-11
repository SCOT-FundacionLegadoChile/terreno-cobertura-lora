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
    Context context;

    public DeviceArrayAdapter(Context cont, List<BluetoothDevice> dispositivos)
    {
        super(cont, R.layout.spinner_dropdown_item, dispositivos);
        context = cont;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        BluetoothDevice device = (BluetoothDevice) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);

        if (device == null) {
            return convertView;
        }

        TextView titulo = (TextView) convertView.findViewById(R.id.dropdown_item_titulo);
        titulo.setText(device.getName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice device = (BluetoothDevice) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_item, parent, false);

        if (device == null) {
            return convertView;
        }

        TextView tvText1 = (TextView) convertView.findViewById(R.id.dropdown_item_titulo);
        TextView subtitulo = (TextView) convertView.findViewById(R.id.dropdown_item_subtitulo);
        tvText1.setText(device.getName());
        subtitulo.setText(device.getAddress());

        return convertView;
    }
}
