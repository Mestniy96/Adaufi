package com.example.mashinetest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import com.google.android.material.slider.RangeSlider;
import static android.R.layout.simple_list_item_1;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    Button btnActTwo, del_x1_up, del_x1_down, el_up_plus, el_up_minus, del_x0_up, del_x0_down, el_down_plus,
            el_down_minus, x1_plus, x1_minus, pause_play, x0_plus, x0_minus, stoping, btnActOne, btnpause, btnstop, upug, upmash,downug, downmash;
    EditText EditTextZadX1, EditTextElUp, EditTextZadX0, EditTextElDown;
    RangeSlider slider;
    SeekBar seekBar, seekBar2;
    public TextView textinfo;
    BluetoothAdapter bluetoothAdapter;
    androidx.constraintlayout.widget.ConstraintLayout ButPanel, ButPanel2;
    ArrayList<String> pairedDeviceArrayList;
    ListView listViewPairedDevice;
    ArrayAdapter<String> pairedDeviceAdapter;
    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;
    private UUID myUUID;
    private StringBuilder sb = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slider=findViewById(R.id.slider);
        slider.addOnChangeListener((slider, value, fromUser) -> {
            ((TextView) findViewById(R.id.textView)).setText(getResources()
                    .getString(R.string.x0_x1f,slider.getValues().get(0) ,slider.getValues().get(1)));
        });

//Инициализация кнопки перехода
        btnActTwo = findViewById(R.id.btnActTwo);// Кнопка перехода в 2 меню
        btnActOne = findViewById(R.id.btnActOne);//Кнопка перехода в 1 меню
        btnpause = findViewById(R.id.btnpause);
        btnstop = findViewById(R.id.btnstop);
        upug = findViewById(R.id.upug);
        upmash = findViewById(R.id.upmash);
        downug = findViewById(R.id.downug);
        downmash = findViewById(R.id.downmash);



        btnActTwo.setOnClickListener(this);
        btnActOne.setOnClickListener(this);
//Инициализация кнопок управления
        del_x1_up = findViewById(R.id.button28);
        del_x1_down = findViewById(R.id.button26);
        el_up_plus = findViewById(R.id.button24);
        el_up_minus = findViewById(R.id.button25);
        del_x0_up = findViewById(R.id.button22);
        del_x0_down = findViewById(R.id.button23);
        el_down_plus = findViewById(R.id.button20);
        el_down_minus =  findViewById(R.id.button21);
        x1_plus = findViewById(R.id.button5);
        x1_minus = findViewById(R.id.button6);
        pause_play =  findViewById(R.id.button);
        x0_plus =  findViewById(R.id.button3);
        x0_minus =  findViewById(R.id.button4);
        stoping = findViewById(R.id.button17);

        del_x1_up.setOnClickListener(this);
        del_x1_down.setOnClickListener(this);
        el_up_plus.setOnClickListener(this);
        el_up_minus.setOnClickListener(this);
        del_x0_up.setOnClickListener(this);
        del_x0_down.setOnClickListener(this);
        el_down_plus.setOnClickListener(this);
        el_down_minus.setOnClickListener(this);
        x1_plus.setOnClickListener(this);
        x1_minus.setOnClickListener(this);
        pause_play.setOnClickListener(this);
        x0_plus.setOnClickListener(this);
        x0_minus.setOnClickListener(this);
        stoping.setOnClickListener(this);

        EditTextElUp = findViewById(R.id.editTextTextPersonName3);
        EditTextElUp.setKeyListener(null);
        EditTextElDown = findViewById(R.id.editTextTextPersonName2);
        EditTextElDown.setKeyListener(null);
        EditTextZadX0 = findViewById(R.id.editTextTextPersonName);
        EditTextZadX0.setKeyListener(null);
        EditTextZadX1 = findViewById(R.id.editTextTextPersonName4);
        EditTextZadX1.setKeyListener(null);

        final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
        textinfo = findViewById(R.id.textinfo);
        listViewPairedDevice = findViewById(R.id.list);
        ButPanel =  findViewById(R.id.panel);
        ButPanel2 =  findViewById(R.id.panel2);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)){
            Toast.makeText(this, "BLUETOOTH NOT support", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this hardware platform", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
   @SuppressLint("MissingPermission") String stInfo = bluetoothAdapter.getName() + " " + bluetoothAdapter.getAddress();
        textinfo.setText(String.format("Это устройство: %s", stInfo));
        Log.e(TAG, "onCreate: " + stInfo );

    }

    private static final String TAG = "MainActivity";

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() { // Запрос на включение Bluetooth
        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "onStart: " + BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        setup();
    }


    @SuppressLint("MissingPermission")
    private void setup() { // Создание списка сопряжённых Bluetooth-устройств
 Set< BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) { // Если есть сопряжённые устройства
            pairedDeviceArrayList = new ArrayList<>();
            for (BluetoothDevice device : pairedDevices) { // Добавляем сопряжённые устройства - Имя + MAC-адресс
                pairedDeviceArrayList.add(device.getName() + "\n" + device.getAddress());
            }
            pairedDeviceAdapter = new ArrayAdapter<>(this, simple_list_item_1, pairedDeviceArrayList);
            listViewPairedDevice.setAdapter(pairedDeviceAdapter);
            // Клик по нужному устройству
            listViewPairedDevice.setOnItemClickListener((parent, view, position, id) -> { //тут пробел после скобки !!!!
                listViewPairedDevice.setVisibility(View.GONE); // После клика скрываем список
                String  itemValue = (String) listViewPairedDevice.getItemAtPosition(position);
                String MAC = itemValue.substring(itemValue.length() - 17); // Вычленяем MAC-адрес
                BluetoothDevice device2 = bluetoothAdapter.getRemoteDevice(MAC);
                myThreadConnectBTdevice = new ThreadConnectBTdevice(device2);
                myThreadConnectBTdevice.start();  // Запускаем поток для подключения Bluetooth
            });
        }
    }


    @Override
    protected void onDestroy() { // Закрытие приложения
        super.onDestroy();
        if(myThreadConnectBTdevice!=null) myThreadConnectBTdevice.cancel();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) { // Если разрешили включить Bluetooth, тогда void setup()
            if (resultCode == Activity.RESULT_OK) {
                setup();
            } else { // Если не разрешили, тогда закрываем приложение
                Toast.makeText(this, "BlueTooth не включён", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private class ThreadConnectBTdevice extends Thread { // Поток для коннекта с Bluetooth
        private BluetoothSocket bluetoothSocket = null;
        @SuppressLint("MissingPermission")
        private ThreadConnectBTdevice(BluetoothDevice device) {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }


        @SuppressLint("MissingPermission")
        @Override
        public void run() { // Коннект
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            }
            catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Нет коннекта, проверьте Bluetooth-устройство с которым хотите соединиться!", Toast.LENGTH_LONG).show();
                    listViewPairedDevice.setVisibility(View.VISIBLE);
                });
                try {
                    bluetoothSocket.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(success) {  // Если законнектились, тогда открываем панель с кнопками и запускаем поток приёма и отправки данных
                runOnUiThread(() -> {
                    ButPanel.setVisibility(View.VISIBLE); // открываем панель с кнопками
                });

                myThreadConnected = new ThreadConnected(bluetoothSocket);
                myThreadConnected.start(); // запуск потока приёма и отправки данных
            }
        }

        public void cancel() {
            Toast.makeText(getApplicationContext(), "Close - BluetoothSocket", Toast.LENGTH_LONG).show();
            try {
                bluetoothSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    } // END ThreadConnectBTdevice:


    private class ThreadConnected extends Thread {    // Поток - приём и отправка данных
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;
        private String sbprint;
        public ThreadConnected(BluetoothSocket socket) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() { // Приём данных
            while (true) {
                try {
                    byte[] buffer = new byte[1];
                    int bytes = connectedInputStream.read(buffer);
                    String strIncom = new String(buffer, 0, bytes);
                    sb.append(strIncom); // собираем символы в строку
                    int endOfLineIndex = sb.indexOf("\r\n"); // определяем конец строки
                    if (endOfLineIndex > 0) {
                        sbprint = sb.substring(0, endOfLineIndex);
                        sb.delete(0, sb.length());
                        // Вывод данных
                        runOnUiThread(() -> {
                            switch (sbprint) {

                                case "D10 ON":

                                case "D10 OFF":

                                case "D11 ON":

                                case "D11 OFF":

                                case "D12 ON":

                                case "D12 OFF":

                                case "D13 ON":

                                case "D13 OFF":
                                    Toast.makeText(MainActivity.this, sbprint, Toast.LENGTH_SHORT).show();
                                    break;

                                default:
                                    break;
                            }
                        });
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }


        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onCheckedChanged (CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.seekBar:
                if(isChecked){
                    if(myThreadConnected!=null) {
                        byte[] bytesToSend = "a".getBytes();
                        myThreadConnected.write(bytesToSend );
                    }
                    Toast.makeText(MainActivity.this, "D10 ON", Toast.LENGTH_SHORT).show();
                }else{
                    if(myThreadConnected!=null) {
                        byte[] bytesToSend = "A".getBytes();
                        myThreadConnected.write(bytesToSend );
                    }
                    Toast.makeText(MainActivity.this, "D10 OFF", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.seekBar2:
                if(isChecked){
                    if(myThreadConnected!=null) {

                        byte[] bytesToSend = "b".getBytes();
                        myThreadConnected.write(bytesToSend );
                    }

                    Toast.makeText(MainActivity.this, "D11 ON", Toast.LENGTH_SHORT).show();
                }else{
                    if(myThreadConnected!=null) {

                        byte[] bytesToSend = "B".getBytes();
                        myThreadConnected.write(bytesToSend );
                    }

                    Toast.makeText(MainActivity.this, "D11 OFF", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        int ElUpPl = 0;
        int ElDownMin = 0;
        int ZadX1 = 0;
        int ZadX0 = 0;


        int id = v.getId();
        if (id == R.id.btnActTwo) {
            ButPanel.setVisibility(View.GONE);
            ButPanel2.setVisibility(View.VISIBLE);
        } else if (id == R.id.btnActOne) {
            ButPanel2.setVisibility(View.GONE);
            ButPanel.setVisibility(View.VISIBLE);
        } else if (id == R.id.button23) {
            ZadX0 = Integer.parseInt(EditTextZadX0.getText().toString());
            if (ZadX0 != 0)
                ZadX0 -= 10;
            EditTextZadX0.setText(String.format(Locale.getDefault(), "%d", ZadX0));
        } else if (id == R.id.button22) {
            ZadX0 = Integer.parseInt(EditTextZadX0.getText().toString());
            if (ZadX0 != 100)
                ZadX0 += 10;
            EditTextZadX0.setText(String.format(Locale.getDefault(), "%d", ZadX0));
        } else if (id == R.id.button4) {
            List<Float> floatList = slider.getValues();
            if(floatList.get(0)!=0f){
                floatList.set(0,floatList.get(0)-10f);
                slider.setValues(floatList);
            }
        } else if (id == R.id.button3) {
            List<Float> floatList = slider.getValues();
            if (floatList.get(0) != 100f) {
                floatList.set(0, floatList.get(0) + 10f);
                slider.setValues(floatList);
            }
        }else if (id == R.id.button6) {
            List<Float> floatList = slider.getValues();
            if(floatList.get(1)!=0f){
                floatList.set(1,floatList.get(1)-10f);
                slider.setValues(floatList);
            }
        } else if (id == R.id.button5) {
            List<Float> floatList = slider.getValues();
            if (floatList.get(1) != 100f) {
                floatList.set(1, floatList.get(1) + 10f);
                slider.setValues(floatList);
            }
        }
    }
}