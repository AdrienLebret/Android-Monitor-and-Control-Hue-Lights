package com.aymard.victor.mqtt_bis;

/**
 * Created by Victor AYMARD on 29.12.2018.
 * activité permettant les interactions bluetooth
 * activation / désactivation / scan des appareils disponnibles
 */


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class  bluetooth_connection extends AppCompatActivity {
    public static final int REQUEST_ACCES_COARSE_LOCATION = 1;
    public static final int REQUEST_ENABLE_BLUETOOTH = 11;
    private ListView devicesList;
    private Button scanningBtn, mOffBtn;
    private Button mOnBtn;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> listAdapter;

    ImageView mBlueIv;
    TextView mStatusBlueTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        //we get bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        devicesList = findViewById(R.id.devicesList);
        scanningBtn = findViewById(R.id.scanningBtn);


        mStatusBlueTv = findViewById(R.id.statusBluetoothTv);
        mOnBtn = findViewById(R.id.onBtn);
        mOffBtn = findViewById(R.id.offBtn);
        mBlueIv = findViewById(R.id.bluetoothIv);


        // we create a simple array adapter to display devices detected
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        devicesList.setAdapter(listAdapter);

        //we check bluetooth state
        checkBluetoothState();


        //check if bluetooth is available or not // for th initialization
        if (bluetoothAdapter == null) {
            mStatusBlueTv.setText("Bluetooth is not available");
        } else {
            mStatusBlueTv.setText("Bluetooth is  available");
            //showToast("Bluetooth is available");
        }

        //set image according to bluetooth status (on/off)
        if (bluetoothAdapter.isEnabled()) {
            mBlueIv.setImageResource(R.drawable.ic_action_on);
        } else {
            mBlueIv.setImageResource(R.drawable.ic_action_off);
        }


        //on btn click
        mOnBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()) {
                    showToast("Turning On Bluetooth...");
                    mStatusBlueTv.setText("Bluetooth is  available");
                    //intent to on bluetooth
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
                    mBlueIv.setImageResource(R.drawable.ic_action_on);
                } else {
                    showToast("Bluetooth is already on");
                }
            }
        });

        //off btn click
        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                    mStatusBlueTv.setText("Bluetooth is not available");
                    showToast("Turning Bluetooth off");
                    mBlueIv.setImageResource(R.drawable.ic_action_off);
                } else {
                    showToast("Bluetooth is already off");
                }
            }
        });


        scanningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                    // we check if coarse location must be asked
                    if (checkCoarseLocationPermission()) {
                        listAdapter.clear();
                        bluetoothAdapter.startDiscovery();
                    }
                } else {
                    checkBluetoothState();
                }
            }
        });

        // we check permission a start of the app
        checkCoarseLocationPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //we register a dedicated receiver for some Bluetooth actions
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(devicesFoundReceiver);
    }

    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCES_COARSE_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    private void checkBluetoothState() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on your device !", Toast.LENGTH_SHORT).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (bluetoothAdapter.isDiscovering()) {
                    Toast.makeText(this, "Device discovering process ...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Bluetooth is activated", Toast.LENGTH_SHORT).show();
                }
                mBlueIv.setImageResource(R.drawable.ic_action_on);
            } else {
                Toast.makeText(this, "You need to activate Bluetooth", Toast.LENGTH_SHORT).show();
                //Intent enableIntent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE); //on peut mettre ces lignes pour que lorsque l'on clique sur le btn recherche il active automatiquement le bluetooth et lance la recherche
                //startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            checkBluetoothState();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permisions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permisions, grantResults);

        switch (requestCode) {
            case REQUEST_ACCES_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Access coarse location allowed, You can scan Bluetooth devices", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Access coarse location forbidden, You can't scan Bluetooth devices", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public String previousName = "  ";
    public String myDevice = "BOSS";
    public String myDeviceAdress = "44:85:00:19:CE:5D";


    // we need to implement our revceiver to get devices detected       // config de ce qui est lu ------------------------
    private final BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (previousName.equals(device.getName())) {
                    Log.d("Titouan", "doublon supprimé");
                }

                if (myDevice.equals(device.getName())) {
                    Log.d("Titouan", "le device BOSS est vu");
                }

                if (myDeviceAdress.equals(device.getAddress())) {
                    Log.d("Titouan", "l'adresse du device BOSS est vu");
                }


                if (!previousName.equals(device.getName())) {
                    listAdapter.add(device.getName() + "\n" + device.getAddress());
                    previousName = device.getName();
                    listAdapter.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                scanningBtn.setText("Scanning Bluetooth Devices");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                scanningBtn.setText("Scanning in progres ...");
            }
        }
    };

    //toast message function
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
