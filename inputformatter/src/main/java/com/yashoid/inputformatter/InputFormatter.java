package com.yashoid.inputformatter;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Yashar on 10/25/2017.
 */

public class InputFormatter implements TextWatcher {

    private Formatter mFormatter;

    public InputFormatter(Formatter formatter) {
        mFormatter = formatter;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        FormattableText formattableText = new FormattableText(s);

        mFormatter.format(formattableText);

        formattableText.addSpans(s);
    }

}
