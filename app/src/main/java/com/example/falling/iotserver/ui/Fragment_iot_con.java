package com.example.falling.iotserver.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.example.falling.iotserver.R;
import com.example.falling.iotserver.data.send.SendIOTData;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import static android.widget.Toast.LENGTH_LONG;


/**
 * Created by falling on 2015/11/30.
 */
//控制的fragment
public class Fragment_iot_con extends Fragment {
    private static Fragment_iot_con iot_con;
    private DiscreteSeekBar moto;
    private Switch Led;
    private ColorPickerView colorPickerView;
    private short heartbeat = 0;
    private int Hexcolor;

    private Button mTextButton;

    public static Fragment_iot_con Instance() {
        if (iot_con == null)
            iot_con = new Fragment_iot_con();
        return iot_con;
    }


    //控制的fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_iot_con, container, false);
        colorPickerView = (ColorPickerView) rootView.findViewById(R.id.color_picker_view);
        moto = (DiscreteSeekBar) rootView.findViewById(R.id.MOTO);
        Led = (Switch) rootView.findViewById(R.id.LED);
        mTextButton = new Button(this.getContext());
        mTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(v.getContext())
                        .setTitle("Choose color")
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(getContext(),"onColorSelected: 0x" + Integer.toHexString(selectedColor), LENGTH_LONG).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });
        return rootView;
    }

    public byte[] LoadData() {
        heartbeat++;
        SendIOTData d = new SendIOTData();
        d.setHeartbeat(heartbeat);
        //RGB

        Hexcolor = colorPickerView.getSelectedColor();
        d.getSensor_dev1().setV1((Hexcolor >> 16) & 0xFF);
        d.getSensor_dev1().setV2((Hexcolor >> 8) & 0xFF);
        d.getSensor_dev1().setV3(Hexcolor & 0xFF);

        //MOTO
        d.getSensor_dev2().setV1(moto.getProgress());

        //LED
        int flag;
        if (Led.isChecked()) {
            flag = 1;
        } else {
            flag = 0;
        }
        d.getSensor_dev3().setV1(flag);

        return d.getBytes();
    }
}