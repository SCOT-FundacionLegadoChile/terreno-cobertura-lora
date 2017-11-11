package cl.matiml.fundacionlegado.scot.loreapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import org.apache.poi.hssf.record.formula.functions.T;
import org.apache.poi.hssf.record.pivottable.StreamIDRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.IntegerField;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MonitorFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected final static String TAG = MonitorFragment.class.getSimpleName();
    protected Context context;

    protected TextView monitor;
    protected TextView localizacionTextView;
    protected TextView tiempoRelativotextView;
    protected TextView numPaqueteRelativoTextView;
    protected EditText lugarEditText;
    protected Spinner pa_powLoraConfigSpinner;
    protected Spinner confLoraConfigSpinner;
    protected EditText alturaEditText;
    protected Button transmitirButton;
    protected ProgressBar transmitiendoProgressBar;
    protected Button conectarButton;
    protected CheckBox conectarCheckBox;
    protected ProgressBar conectarProgressBar;


    protected SharedPreferences sharedPref;

    protected BluetoothThread bluetoothThread;
    protected Runnable btConnectionLifecycle;
    protected BluetoothAdapter btAdapter;
    protected BluetoothSocket btSocket;
    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected Boolean isConnected;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    protected String lugar;
    protected String altura;
    protected String loraPa;
    protected String loraPow;
    protected String loraMod;
    protected String timeStamp;
    protected String tiempoRelativo;
    protected Integer numPaqueteRelativo;
    protected Boolean isTransmiting;

    protected String fileName = "";

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
        //  To call Handler
        //    Bundle bundle = new Bundle();
        //    bundle.putString("msje", msg);
        //    Message mssg = new Message();
        //    mssg.setData(bundle);
        //    mHandler.sendMessage(mssg);
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        context = getActivity();

        bluetoothThread = new BluetoothThread("myBluetoothThread");
        bluetoothThread.start();
        bluetoothThread.prepareHandler();

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        isConnected = false;
        isTransmiting = false;

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_monitor, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        localizacionTextView = (TextView) getView().findViewById(R.id.localizacion_textView);
        tiempoRelativotextView = (TextView) getView().findViewById(R.id.tiempo_relativo_text_view);
        numPaqueteRelativoTextView = (TextView) getView().findViewById(R.id.num_paquetes_relativo_text_view);
        lugarEditText = (EditText) getView().findViewById(R.id.lugar_edit_text);
        pa_powLoraConfigSpinner = (Spinner) getView().findViewById(R.id.pa_pow_lora_configurations_spinner);
        confLoraConfigSpinner = (Spinner) getView().findViewById(R.id.conf_lora_configurations_spinner);
        alturaEditText = (EditText) getView().findViewById(R.id.altura_edit_text);
        transmitirButton = (Button) getView().findViewById(R.id.iniciar_transmicion_button);
        transmitiendoProgressBar = (ProgressBar) getView().findViewById(R.id.transmitiendo_progress_bar);
        conectarButton = (Button) getView().findViewById(R.id.button_conectar);
        conectarCheckBox = (CheckBox) getView().findViewById(R.id.conectar_check_box);
        conectarProgressBar = (ProgressBar) getView().findViewById(R.id.conectar_progress_bar);
        monitor = (TextView) getView().findViewById(R.id.monitor_edit_text);
        monitor.setMovementMethod(new ScrollingMovementMethod());

        ArrayAdapter<CharSequence> adapterPA_POW = ArrayAdapter.createFromResource(context, R.array.pa_pow_lora_conf_options, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterCONF = ArrayAdapter.createFromResource(context, R.array.conf_lora_conf_options, android.R.layout.simple_spinner_item);
        adapterPA_POW.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCONF.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pa_powLoraConfigSpinner.setAdapter(adapterPA_POW);
        confLoraConfigSpinner.setAdapter(adapterCONF);

        conectarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "- conectarPressed - in");
                if (!isConnected) {
                    //if the device has bluetooth
                    if (btAdapter.isEnabled()) {
                        Log.i(TAG, "Iniciando thread");

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Title");

                        final EditText input = new EditText(getActivity());
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        input.setHint("prueba");
                        builder.setView(input);
                        builder.setTitle("Iniciar prueba");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fileName = input.getText().toString();
                                if (fileName.matches("")) {
                                    fileName = input.getHint().toString();
                                }
                                bluetoothThread.postTask(btConnectionLifecycle);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        toast("Turn on bluetooth");
                    }
                } else {
                    if (isTransmiting) {
                        toast("transmitiendo...");
                    } else {
                        Log.i(TAG, "cerrando thread");
                        if (btSocket != null) {
                            try {
                                btSocket.close();
                                desconectarUIDispositivoBT();
                            } catch (IOException e) {
                                toast(e.toString());
                            }
                        }
                    }
                }
                Log.i(TAG, "- conectarPressed - out");
            }
        });

        transmitirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isConnected) {
                        if (isTransmiting) {
                            byte[] sended = new byte[]{48};
                            outputStream.write("stop+".toString().getBytes());
                            Log.i(TAG, "sending -> 'stop'");
                            lugar = "not_transmitting";
                            altura = "not_transmitting";
                            isTransmiting = false;
                            transmitirButton.setText("Iniciar transmición");
                            transmitiendoProgressBar.setIndeterminate(false);
                        } else {
                            byte[] sended = new byte[]{49};
                            String cmd = "start" + getLoraConfigurationSerialPacket();
                            Log.i(TAG, "sending -> '" + cmd + "'");
                            outputStream.write(cmd.getBytes());//cmd.getBytes());

                            lugar = lugarEditText.getText().toString();
                            altura = alturaEditText.getText().toString();
                            isTransmiting = true;
                            transmitirButton.setText("Detener transmición");
                            transmitiendoProgressBar.setIndeterminate(true);
                        }
                    } else {
                        toast("no conectado...");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "transmit button error: " + e.toString());
                }
            }
        });

//        HSSFWorkbook workbook = new HSSFWorkbook();
//        HSSFSheet firstSheet = workbook.createSheet("Sheet No 1");
//        HSSFSheet secondSheet = workbook.createSheet("Sheet No 2");
//        HSSFRow rowA = firstSheet.createRow(0);
//        HSSFCell cellA = rowA.createCell(0);
//        cellA.setCellValue(new HSSFRichTextString("Sheet One"));
//        HSSFRow rowB = secondSheet.createRow(0);
//        HSSFCell cellB = rowB.createCell(0);
//        cellB.setCellValue(new HSSFRichTextString("Sheet two"));
//        FileOutputStream fos = null;
//        try {
//            File file = new File(getActivity().getExternalFilesDir(null), "probandoo.xls");
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            fos = new FileOutputStream(file);
//            workbook.write(fos);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.flush();
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            Toast.makeText(context, "Excel Sheet Generated", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

        btConnectionLifecycle = new Runnable() {
            @Override
            public void run() {

                /* Pre BT connection */
                Log.i(TAG, "UI preparation to initiate BT connection");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        conectarButton.setEnabled(false);
                        conectarCheckBox.setVisibility(View.INVISIBLE);
                        conectarProgressBar.setVisibility(View.VISIBLE);
                    }
                });

                /* BT Connecting - background thread */
                Log.i(TAG, "Connecting to BT device (Taote) in background");
                try {
                    Log.i(TAG, "trying to connect...");
                    String address = sharedPref.getString(ConfigFragment.DEVICE_ADDRESS, ConfigFragment.DEFAULT_DEVICE_ADDRESS);
                    BluetoothDevice hc05 = btAdapter.getRemoteDevice(address);
                    btSocket = hc05.createInsecureRfcommSocketToServiceRecord(ConfigFragment.DEFAULT_UUID);
                    btAdapter.cancelDiscovery();

                    btSocket.connect();    //Timeout de conexion ~7s
                    inputStream = btSocket.getInputStream();
                    outputStream = btSocket.getOutputStream();
                    isConnected = true;
                    Log.i(TAG, "BT Socket connected");
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    isConnected = false;
                }

                /* Post connection attempt - ui thread */
                Log.i(TAG, "Post connection attempt UI management");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isConnected) {
                            conectarProgressBar.setVisibility(View.INVISIBLE);
                            conectarCheckBox.setVisibility(View.VISIBLE);
                            conectarCheckBox.setChecked(true);
                            conectarButton.setText("Desconectar");
                            conectarButton.setEnabled(true);
                        } else {
                            conectarProgressBar.setVisibility(View.INVISIBLE);
                            conectarCheckBox.setVisibility(View.VISIBLE);
                            conectarCheckBox.setChecked(false);
                            conectarButton.setText("Conectar");
                            conectarButton.setEnabled(true);

                            toast("Intente de nuevo");
                        }
                    }
                });


                /* BT Connection Lifecycle */
                if (isConnected) {
                    final String nodeType = sharedPref.getString(ConfigFragment.NODE_TYPE, ConfigFragment.ENDPOINT);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            toast("Node Type: " + nodeType);
                        }
                    });

                    String titulo = (nodeType.equals(ConfigFragment.ENDPOINT)) ? "Tx":"Rx";
                    HSSFWorkbook workbook = new HSSFWorkbook();
                    HSSFSheet firstSheet = workbook.createSheet("Pruebas_Lora_" + titulo);
                    HSSFRow rowA = firstSheet.createRow(0);
                    if (nodeType.equals(ConfigFragment.ENDPOINT)) {
                        Log.i(TAG, "writing sheet titles row for ENDPOINT");
                        rowA.createCell(0).setCellValue(new HSSFRichTextString("rol"));
                        rowA.createCell(1).setCellValue(new HSSFRichTextString("# paquete"));
                        rowA.createCell(2).setCellValue(new HSSFRichTextString("paquete"));
                        rowA.createCell(3).setCellValue(new HSSFRichTextString("lora pa"));
                        rowA.createCell(4).setCellValue(new HSSFRichTextString("lora pow"));
                        rowA.createCell(5).setCellValue(new HSSFRichTextString("lora mod"));
                        rowA.createCell(6).setCellValue(new HSSFRichTextString("time"));
                        rowA.createCell(7).setCellValue(new HSSFRichTextString("date"));
                        rowA.createCell(8).setCellValue(new HSSFRichTextString("lat"));
                        rowA.createCell(9).setCellValue(new HSSFRichTextString("lon"));
                        rowA.createCell(10).setCellValue(new HSSFRichTextString("altura"));
                        rowA.createCell(11).setCellValue(new HSSFRichTextString("lugar"));
                    } else if(nodeType.equals(ConfigFragment.GATEWAY)) {
                        Log.i(TAG, "writing sheet titles row for GATEWAY");
                        rowA.createCell(0).setCellValue(new HSSFRichTextString("rol"));
                        rowA.createCell(1).setCellValue(new HSSFRichTextString("# paquete"));
                        rowA.createCell(2).setCellValue(new HSSFRichTextString("paquete"));
                        rowA.createCell(3).setCellValue(new HSSFRichTextString("lora pa"));
                        rowA.createCell(4).setCellValue(new HSSFRichTextString("lora pow"));
                        rowA.createCell(5).setCellValue(new HSSFRichTextString("lora mod"));
                        rowA.createCell(6).setCellValue(new HSSFRichTextString("time"));
                        rowA.createCell(7).setCellValue(new HSSFRichTextString("date"));
                        rowA.createCell(8).setCellValue(new HSSFRichTextString("lat"));
                        rowA.createCell(9).setCellValue(new HSSFRichTextString("lon"));
                        rowA.createCell(10).setCellValue(new HSSFRichTextString("rssi"));
                        rowA.createCell(11).setCellValue(new HSSFRichTextString("altura"));
                        rowA.createCell(12).setCellValue(new HSSFRichTextString("lugar"));
                    }

                    int num_paquete = 0;

                    // https://github.com/akexorcist/Android-BluetoothSPPLibrary/blob/master/library/src/main/java/app/akexorcist/bluetotohspp/library/BluetoothService.java
                    byte[] buffer;
                    ArrayList<Integer> arr_byte = new ArrayList<Integer>();

                    while (true) {
                        try {
                            int data = inputStream.read();
                            // (data == 0x0A) // 10 '\n' salto de linea
                            // (data == 0x0D) // 13 '\r' retorno de carro
                            // (data == 0x30) // 48 '0'  cero

                            Log.i(TAG, "received    .   " + Integer.toString(data));

                            if (data == 0x0A) {
                                buffer = new byte[arr_byte.size()];
                                for (int i = 0; i < arr_byte.size(); i++) {
                                    buffer[i] = arr_byte.get(i).byteValue();
                                }

                                final int npaq = num_paquete++;
                                final String timeStamp = getTimeStamp("HH:mm:ss");
                                String dateStamp = getTimeStamp("dd-MM-yyyy");
                                mLastLocation = getLastLocation();
                                final String latlng = location2String(mLastLocation);
                                final String paquete = new String(buffer, "US-ASCII");

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        monitor.append("[" + timeStamp + "]" + paquete.concat("\n"));
                                        final int scrollAmount = monitor.getLayout().getLineTop(monitor.getLineCount()) - monitor.getHeight();
                                        if (scrollAmount > 0)
                                            monitor.scrollTo(0, scrollAmount);
                                        else
                                            monitor.scrollTo(0, 0);

                                        localizacionTextView.setText(latlng);
                                        tiempoRelativotextView.setText(timeStamp);
                                        numPaqueteRelativoTextView.setText(Integer.toString(npaq));
                                    }
                                });

                                /* Node Type Routine */

                                HSSFRow row = firstSheet.createRow(num_paquete);
                                switch (nodeType) {
                                    case ConfigFragment.ENDPOINT:
                                        Log.i(TAG, "(endpoint) saving row " + Integer.toString(num_paquete) + "... " + paquete);
                                        row.createCell(0).setCellValue(new HSSFRichTextString(titulo));
                                        row.createCell(1).setCellValue(new HSSFRichTextString(Integer.toString(num_paquete)));
                                        row.createCell(2).setCellValue(new HSSFRichTextString(paquete));
                                        row.createCell(3).setCellValue(new HSSFRichTextString(loraPa));
                                        row.createCell(4).setCellValue(new HSSFRichTextString(loraPow));
                                        row.createCell(5).setCellValue(new HSSFRichTextString(loraMod));
                                        row.createCell(6).setCellValue(new HSSFRichTextString(timeStamp));
                                        row.createCell(7).setCellValue(new HSSFRichTextString(dateStamp));
                                        row.createCell(8).setCellValue(new HSSFRichTextString(Double.toString(mLastLocation.getLatitude())));
                                        row.createCell(9).setCellValue(new HSSFRichTextString(Double.toString(mLastLocation.getLongitude())));
                                        row.createCell(10).setCellValue(new HSSFRichTextString(altura));
                                        row.createCell(11).setCellValue(new HSSFRichTextString(lugar));
                                        //workbook.write(fos);
                                        break;
                                    case ConfigFragment.GATEWAY:
                                        String[] separated = paquete.split(":");
                                        Log.i(TAG, "(gateway) saving row " + Integer.toString(num_paquete) + "... " + paquete);
                                        Log.i(TAG, "paquete: " + separated[0] + ", rssi: " + separated[1]);
                                        row.createCell(0).setCellValue(new HSSFRichTextString(titulo));
                                        row.createCell(1).setCellValue(new HSSFRichTextString(Integer.toString(num_paquete)));
                                        row.createCell(2).setCellValue(new HSSFRichTextString(separated[0]));
                                        row.createCell(3).setCellValue(new HSSFRichTextString(loraPa));
                                        row.createCell(4).setCellValue(new HSSFRichTextString(loraPow));
                                        row.createCell(5).setCellValue(new HSSFRichTextString(loraMod));
                                        row.createCell(6).setCellValue(new HSSFRichTextString(timeStamp));
                                        row.createCell(7).setCellValue(new HSSFRichTextString(dateStamp));
                                        row.createCell(8).setCellValue(new HSSFRichTextString(Double.toString(mLastLocation.getLatitude())));
                                        row.createCell(9).setCellValue(new HSSFRichTextString(Double.toString(mLastLocation.getLongitude())));
                                        row.createCell(10).setCellValue(new HSSFRichTextString(separated[1]));
                                        row.createCell(11).setCellValue(new HSSFRichTextString(altura));
                                        row.createCell(12).setCellValue(new HSSFRichTextString(lugar));
                                        break;
                                    default:
                                        break;
                                }

                                arr_byte = new ArrayList<Integer>();
                            } else {
                                arr_byte.add(data);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    desconectarUIDispositivoBT();
                                }
                            });

                            break;
                        }
                    }
                    FileOutputStream fos = null;
                    File file = new File(getActivity().getExternalFilesDir(null), getTimeStamp("yyyyMMddHHmmss") + "_" + titulo + "_" +  fileName + ".xls");
                    try {
                        if (!file.exists())
                            file.createNewFile();
                        fos = new FileOutputStream(file);
                        workbook.write(fos);
                    } catch (IOException e) {
                        Log.e(TAG, "error writing sheet: " + e.toString());
                    } finally {
                        if (fos != null) {
                            try {
                                fos.flush();
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    Toast.makeText(context, "Excel Sheet finalized", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "reached end of runnable");
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    public void onDestroy() {
        super.onDestroy();

        bluetoothThread.quit();
    }

    private void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    protected void desconectarUIDispositivoBT() {
        isConnected = false;
        conectarButton.setText("Conectar");
        conectarCheckBox.setChecked(false);

        isTransmiting = true;
        transmitirButton.setText("Iniciar Transmisión");
        transmitiendoProgressBar.setIndeterminate(false);
    }

    protected String getTimeStamp(String format) {
        // "dd-MM-yyyy_HH:mm:ss"
        return new SimpleDateFormat(format).format(new Date());
    }

    /* ----- LOCATION API ----- */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.PERMISSION_LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

    }

    protected Location getLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.PERMISSION_LOCATION_REQUEST_CODE);
            return null;
        }
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected String location2String(Location location) {
        return "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }

    protected String getLoraConfigurationSerialPacket() {
        String pa_pow = "+0+10";
        switch ((String) pa_powLoraConfigSpinner.getSelectedItem()) {
            case "rf0 pow-1":   loraPa="RF0"; loraPow="-1"; pa_pow = "+0+-1"; break;
            case "rf0 pow0":    loraPa="RF0"; loraPow="0"; pa_pow = "+0+0"; break;
            case "rf0 pow1":    loraPa="RF0"; loraPow="1"; pa_pow = "+0+1"; break;
            case "rf0 pow2":    loraPa="RF0"; loraPow="2"; pa_pow = "+0+2"; break;
            case "rf0 pow3":    loraPa="RF0"; loraPow="3"; pa_pow = "+0+3"; break;
            case "rf0 pow4":    loraPa="RF0"; loraPow="4"; pa_pow = "+0+4"; break;
            case "rf0 pow5":    loraPa="RF0"; loraPow="5"; pa_pow = "+0+5"; break;
            case "rf0 pow6":    loraPa="RF0"; loraPow="6"; pa_pow = "+0+6"; break;
            case "rf0 pow7":    loraPa="RF0"; loraPow="7"; pa_pow = "+0+7"; break;
            case "rf0 pow8":    loraPa="RF0"; loraPow="8"; pa_pow = "+0+8"; break;
            case "rf0 pow9":    loraPa="RF0"; loraPow="9"; pa_pow = "+0+9"; break;
            case "rf0 pow10":   loraPa="RF0"; loraPow="10"; pa_pow = "+0+10"; break;
            case "rf0 pow11":   loraPa="RF0"; loraPow="11"; pa_pow = "+0+11"; break;
            case "rf0 pow12":   loraPa="RF0"; loraPow="12"; pa_pow = "+0+12"; break;
            case "rf0 pow13":   loraPa="RF0"; loraPow="13"; pa_pow = "+0+13"; break;
            case "rf0 pow14":   loraPa="RF0"; loraPow="14"; pa_pow = "+0+14"; break;
            case "boost pow5":  loraPa="BOOST"; loraPow="5"; pa_pow = "+1+5"; break;
            case "boost pow6":  loraPa="BOOST"; loraPow="6"; pa_pow = "+1+6"; break;
            case "boost pow7":  loraPa="BOOST"; loraPow="7"; pa_pow = "+1+7"; break;
            case "boost pow8":  loraPa="BOOST"; loraPow="8"; pa_pow = "+1+8"; break;
            case "boost pow9":  loraPa="BOOST"; loraPow="9"; pa_pow = "+1+9"; break;
            case "boost pow10": loraPa="BOOST"; loraPow="10"; pa_pow = "+1+10"; break;
            case "boost pow11": loraPa="BOOST"; loraPow="11"; pa_pow = "+1+11"; break;
            case "boost pow12": loraPa="BOOST"; loraPow="12"; pa_pow = "+1+12"; break;
            case "boost pow13": loraPa="BOOST"; loraPow="13"; pa_pow = "+1+13"; break;
            case "boost pow14": loraPa="BOOST"; loraPow="14"; pa_pow = "+1+14"; break;
            case "boost pow15": loraPa="BOOST"; loraPow="15"; pa_pow = "+1+15"; break;
            case "boost pow16": loraPa="BOOST"; loraPow="16"; pa_pow = "+1+16"; break;
            case "boost pow17": loraPa="BOOST"; loraPow="17"; pa_pow = "+1+17"; break;
            case "boost pow18": loraPa="BOOST"; loraPow="18"; pa_pow = "+1+18"; break;
            case "boost pow19": loraPa="BOOST"; loraPow="19"; pa_pow = "+1+19"; break;
            case "boost pow20": loraPa="BOOST"; loraPow="20"; pa_pow = "+1+20"; break;
            case "boost pow21": loraPa="BOOST"; loraPow="21"; pa_pow = "+1+21"; break;
            case "boost pow22": loraPa="BOOST"; loraPow="22"; pa_pow = "+1+22"; break;
            case "boost pow23": loraPa="BOOST"; loraPow="23"; pa_pow = "+1+23"; break;
            default:
                break;
        }
        String conf = "+5+";
        switch ((String) confLoraConfigSpinner.getSelectedItem()) {
            case "mod0":  loraMod = "mod0"; conf = "+0+";  break;
            case "mod1":  loraMod = "mod1"; conf = "+1+";  break;
            case "mod2":  loraMod = "mod2"; conf = "+2+";  break;
            case "mod3":  loraMod = "mod3"; conf = "+3+";  break;
            case "mod4":  loraMod = "mod4"; conf = "+4+";  break;
            case "mod5":  loraMod = "mod5"; conf = "+5+";  break;
            case "mod6":  loraMod = "mod6"; conf = "+6+";  break;
            case "mod7":  loraMod = "mod7"; conf = "+7+";  break;
            case "mod8":  loraMod = "mod8"; conf = "+8+";  break;
            case "mod9":  loraMod = "mod9"; conf = "+9+";  break;
            case "mod10": loraMod = "mod10"; conf = "+10+"; break;
        }

        return pa_pow + conf;
    }
}
