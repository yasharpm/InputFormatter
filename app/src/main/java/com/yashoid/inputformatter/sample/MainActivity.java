package com.yashoid.inputformatter.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.yashoid.inputformatter.FormattableText;
import com.yashoid.inputformatter.Formatter;
import com.yashoid.inputformatter.InputFormatter;
import com.yashoid.inputformatter.PanInputFormatter;
import com.yashoid.inputformatter.PriceInputFormatter;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editPan = (EditText) findViewById(R.id.edit_pan);
        editPan.addTextChangedListener(PanInputFormatter.getInstance());

        EditText editPrice = (EditText) findViewById(R.id.edit_price);
        editPrice.addTextChangedListener(new PriceInputFormatter(","));

        EditText editCustom = (EditText) findViewById(R.id.edit_custom);
        editCustom.addTextChangedListener(new InputFormatter(mCustomFormatter));
    }

    private Formatter mCustomFormatter = new Formatter() {

        @Override
        public void format(FormattableText text) {
            text.replaceAll(' ', '.');
        }

    };

}
