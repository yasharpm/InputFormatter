package com.yashoid.inputformatter;

/**
 * Created by Yashar on 10/26/2017.
 */

public class PriceInputFormatter extends InputFormatter {

    private static Formatter makePriceFormatter(final String divider) {
        return new Formatter() {

            private String mDivider = divider;

            @Override
            public void format(FormattableText text) {
                int seek = text.length();

                while (seek > 3) {
                    text.insert(seek - 3, mDivider);

                    seek -= 3;
                }
            }

        };
    }

    public PriceInputFormatter(String divider) {
        super(makePriceFormatter(divider));
    }

}
