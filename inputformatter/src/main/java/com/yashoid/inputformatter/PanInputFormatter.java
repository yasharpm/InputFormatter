package com.yashoid.inputformatter;

/**
 * Created by Yashar on 10/26/2017.
 */

public class PanInputFormatter extends InputFormatter {

    private static PanInputFormatter mInstance = null;

    public static PanInputFormatter getInstance() {
        if (mInstance == null) {
            mInstance = new PanInputFormatter();
        }

        return mInstance;
    }

    private static Formatter makePanFormatter(final char separator) {
        return new Formatter() {

            @Override
            public void format(FormattableText text) {
                int len = text.length();

                if (len > 12) {
                    text.insert(12, "" + separator);
                }

                if (len > 8) {
                    text.insert(8, " " + separator);
                }

                if (len > 4) {
                    text.insert(4, " " + separator);
                }
            }

        };
    }

    public PanInputFormatter(char separator) {
        super(makePanFormatter(separator));
    }

    public PanInputFormatter() {
        this(' ');
    }

}
