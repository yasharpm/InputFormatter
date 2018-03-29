package com.yashoid.inputformatter;

import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Yashar on 10/25/2017.
 */

public class InputFormatter implements TextWatcher {

    private static final String RTL_CHAR = "\u202B";

    private TextView mTextView;

    private Formatter mFormatter;

    public InputFormatter(Formatter formatter) {
        mFormatter = formatter;
    }

    public InputFormatter(Formatter formatter, TextView textView) {
        mTextView = textView;

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

        if (mTextView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mTextView.setTextDirection(formattableText.isRtl() ? View.TEXT_DIRECTION_RTL : View.TEXT_DIRECTION_LTR);
            }
        }
    }

}
