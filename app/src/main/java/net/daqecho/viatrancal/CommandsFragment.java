package net.daqecho.viatrancal;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class CommandsFragment extends Fragment implements View.OnClickListener {
    String device_address;

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    String device_name;
    View rootView;
    private String dataToSend;

    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter mBluetootheAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket btSocket;
    OutputStream outStream = null;
    InputStream inStream = null;
    TextView ZeroView, SpanView;
    Handler handler = new Handler();

    byte delimiter = 10;
    boolean stopWorker = false;

    int readBufferedPosition = 0;
    byte[] readBuffer = new byte[1024];


    public void setDevice_address(String device_address) {
        this.device_address = device_address;
    }


    public CommandsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_commands, container, false);

        SpanView = rootView.findViewById(R.id.SpanValueView);
        ZeroView = rootView.findViewById(R.id.ZeroValueView);

        Button ZFU = rootView.findViewById(R.id.ZFU);
        Button ZFD = rootView.findViewById(R.id.ZFD);
        Button ZCU = rootView.findViewById(R.id.ZCU);
        Button ZCD = rootView.findViewById(R.id.ZCD);

        Button SFU = rootView.findViewById(R.id.SFU);
        Button SFD = rootView.findViewById(R.id.SFD);
        Button SCU = rootView.findViewById(R.id.SCU);
        Button SCD = rootView.findViewById(R.id.SCD);

        Button SZ = rootView.findViewById(R.id.SZ);
        Button SS = rootView.findViewById(R.id.SS);



        ZFU.setOnClickListener(this);
        ZFD.setOnClickListener(this);
        ZCU.setOnClickListener(this);
        ZCD.setOnClickListener(this);

        SFU.setOnClickListener(this);
        SFD.setOnClickListener(this);
        SCU.setOnClickListener(this);
        SCD.setOnClickListener(this);

        SZ.setOnClickListener(this);
        SS.setOnClickListener(this);

        connect();
        return rootView;
    }

    private void WriteData(String data)
    {
        try
        {
            outStream = btSocket.getOutputStream();
        } catch (IOException e)
        {

        }
        String message = data;
        byte[] msgBuffer = message.getBytes();

        try
        {
            outStream.write(msgBuffer);
        } catch (IOException e)
        {

        }
    }

    @Override
    public void onDestroy()
    {
        // TODO: Implement this method
        super.onDestroy();
        try
        {
            btSocket.close();
        } catch (IOException e)
        {

        }
    }



    private void connect()
    {

        BluetoothDevice  btdevice  = mBluetootheAdapter.getRemoteDevice(device_address);
        mBluetootheAdapter.cancelDiscovery();
        try
        {

            btSocket = btdevice.createRfcommSocketToServiceRecord(MY_UUID);
            btSocket.connect();
            Toast.makeText(getContext(), "Connected to " + device_name , Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            try
            {
                Toast.makeText(getContext(), "Connection Failed with " + device_name, Toast.LENGTH_LONG).show();

                btSocket.close();
            } catch (IOException e2)
            {

            }
        }
      //  WriteData("a");
      //  WriteData("b");

        beginListenForData();
    }

    private void beginListenForData()
    {
        try
        {
            inStream = btSocket.getInputStream();
        } catch (IOException e)
        {

        }
        Thread workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = inStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inStream.read(packetBytes);
                            for(int i = 0; i<bytesAvailable; i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)

                                {
                                    byte[] encodedBytes = new byte[readBufferedPosition];
                                    System.arraycopy(readBuffer, 0,encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferedPosition = 0;
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {

                                            String[] array = data.split(",");

                                            SpanView.setText(array[0]);
                                            ZeroView.setText(array[1]);

                                        }
                                    });
                                } else
                                {
                                    readBuffer[readBufferedPosition++] = b;
                                }

                            }
                        }
                    } catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }


    @Override
    public void onClick(View view) {

            int id = view.getId();
        switch (id) {
            case R.id.ZFU:
                WriteData("2");
                //Toast.makeText(getActivity(), "ZFU", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ZFD:
                WriteData("1");
                //Toast.makeText(getActivity(), "ZFD", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ZCU:
                WriteData("4");
                //Toast.makeText(getActivity(), "ZCU", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ZCD:
                WriteData("3");
                //Toast.makeText(getActivity(), "ZCD", Toast.LENGTH_SHORT).show();
                break;

            case R.id.SFU:
                WriteData("7");
                //Toast.makeText(getActivity(), "SFU", Toast.LENGTH_SHORT).show();
                break;
            case R.id.SFD:
                WriteData("6");
                //Toast.makeText(getActivity(), "SFD", Toast.LENGTH_SHORT).show();
                break;
            case R.id.SCU:
                WriteData("9");
                //Toast.makeText(getActivity(), "SCU", Toast.LENGTH_SHORT).show();
                break;
            case R.id.SCD:
                WriteData("8");
                //Toast.makeText(getActivity(), "SCD", Toast.LENGTH_SHORT).show();
                break;

            case R.id.SZ:
                WriteData("5");
                //Toast.makeText(getActivity(), "SZ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.SS:
                WriteData("0");
                //Toast.makeText(getActivity(), "SS", Toast.LENGTH_SHORT).show();
                break;

        }

    }
}
