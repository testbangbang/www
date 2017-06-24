package com.onyx.unitconversion;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2017/5/18.
 */

public class ConversionFragment extends Fragment {

    private static final int ALL_OUTPUT = -1;

    @Bind(R.id.input_text)
    EditText inputText;
    @Bind(R.id.input_unit)
    Spinner inputUnit;
    @Bind(R.id.output_unit)
    Spinner outputUnit;
    @Bind(R.id.output_content)
    RecyclerView outputContent;

    public static ConversionFragment newInstance() {
        return new ConversionFragment();
    }

    private List<Pair<String, String>> conversionResult = new ArrayList<>();
    private UnitAdapter adapter;
    private UnitCollection[] collections;
    private int fromUnitIndex;
    private int toUnitIndex = ALL_OUTPUT;
    private int category;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversion, container, false);
        ButterKnife.bind(this, view);
        collections = UnitCollection.getInstance(getContext());
        initView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void changeCategory(int category) {
        this.category = category;
        initInputUnitSelector();
        initOutPutUnitSelector();
        updateConversionResult();
    }

    private void initView() {
        inputText.setRawInputType(Configuration.KEYBOARD_12KEY);
        initInputUnitSelector();
        initOutPutUnitSelector();
        outputContent.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UnitAdapter(conversionResult);
        outputContent.setAdapter(adapter);
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateConversionResult();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateConversionResult() {
        String input = inputText.getText().toString();
        if (input.isEmpty()) {
            return;
        }
        double inputNumber = Double.valueOf(input);
        conversionResult.clear();
        List<SingleUnit> items = collections[category].getItems();
        if (toUnitIndex == ALL_OUTPUT) {
            for (int i = 0; i < items.size(); i++) {
                SingleUnit unit = items.get(i);
                double p = UnitCollection.convert(getActivity(), category, fromUnitIndex, i, inputNumber);
                Pair<String, String> pair = new Pair<>(UnitCollection.getUnitName(getContext(), unit), UnitUtils.getFormattedValueStr(p).toString());
                conversionResult.add(pair);
            }
        }else {
            SingleUnit unit = items.get(toUnitIndex);
            double p = UnitCollection.convert(getActivity(), category, fromUnitIndex, toUnitIndex, inputNumber);
            Pair<String, String> pair = new Pair<>(UnitCollection.getUnitName(getContext(), unit), UnitUtils.getFormattedValueStr(p).toString());
            conversionResult.add(pair);
        }

        adapter.notifyDataSetChanged();
    }

    private void initInputUnitSelector() {
        List<String> items = UnitCollection.getALLUnitNamesByCategory(getActivity(), category);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(),R.layout.unit_selector_list_item, items);
        inputUnit.setAdapter(adapter);
        inputUnit.clearAnimation();
        inputUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromUnitIndex = position;
                updateConversionResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initOutPutUnitSelector() {
        List<String> items = UnitCollection.getALLUnitNamesByCategory(getActivity(), category);
        items.add(0, getString(R.string.all));
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(),R.layout.unit_selector_list_item, items);
        outputUnit.clearAnimation();
        outputUnit.setAdapter(adapter);
        outputUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toUnitIndex = position - 1;
                updateConversionResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
