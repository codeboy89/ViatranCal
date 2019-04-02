package net.daqecho.viatrancal;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static android.widget.Toast.LENGTH_SHORT;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    ListView BTList;


    private String mmDevice_address;

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private String mmDevice_name;


    public SettingsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        Button b = rootView.findViewById(R.id.btn_BTConnects);
        b.setOnClickListener(this);


        BTList = rootView.findViewById(R.id.listView);
        mBluetoothAdapter = getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            pairedDevices = mBluetoothAdapter.getBondedDevices();
            ArrayList list = new ArrayList();
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName()  + "\n" + bt.getAddress() );
            }
            ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, list);
            BTList.setAdapter(adapter);
            BTList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                    String info = list.get(pos).toString();
                    final String address = info.substring(info.length() - 17);
                    final String name = info.substring(0, info.length() - 17);

                    mmDevice_address = address;
                    mmDevice_name = name;
                }
            });
        }

        return rootView;
    }



    @Override
    public void onClick(View view)
    {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        CommandsFragment cf = new CommandsFragment();
        cf.setDevice_address(mmDevice_address);
        cf.setDevice_name(mmDevice_name);
        fragmentTransaction.replace(R.id.fragment_container, cf);

        fragmentTransaction.commit();

    }


}
