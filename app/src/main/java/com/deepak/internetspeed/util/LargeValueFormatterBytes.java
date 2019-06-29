package com.deepak.internetspeed.util;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class LargeValueFormatterBytes extends ValueFormatter
{

    private String[] mSuffix = new String[]{
            "", "K", "M", "G", "T"
    };
    private int mMaxLength = 5;
    private DecimalFormat mFormat;
    private String mText = "";

    public LargeValueFormatterBytes() {
        mFormat = new DecimalFormat("###E00");
    }

    /**
     * Creates a formatter that appends a specified text to the result string
     *
     * @param appendix a text that will be appended
     */
    public LargeValueFormatterBytes(String appendix) {
        this();
        mText = appendix;
    }

    @Override
    public String getFormattedValue(float value) {
        return makePretty(value) + mText;
    }

    /**
     * Set an appendix text to be added at the end of the formatted value.
     *
     * @param appendix
     */
    public void setAppendix(String appendix) {
        this.mText = appendix;
    }

    /**
     * Set custom suffix to be appended after the values.
     * Default suffix: ["", "k", "m", "b", "t"]
     *
     * @param suffix new suffix
     */
    public void setSuffix(String[] suffix) {
        this.mSuffix = suffix;
    }

    public void setMaxLength(int maxLength) {
        this.mMaxLength = maxLength;
    }

    /**
     * Formats each number properly. Special thanks to Roman Gromov
     * (https://github.com/romangromov) for this piece of code.
     */
    private String makePretty(double number) {

        String r = mFormat.format(number);

        int numericValue1 = Character.getNumericValue(r.charAt(r.length() - 1));
        int numericValue2 = Character.getNumericValue(r.charAt(r.length() - 2));
        int combined = Integer.valueOf(numericValue2 + "" + numericValue1);

        r = r.replaceAll("E[0-9][0-9]", mSuffix[combined / 3]);

        while (r.length() > mMaxLength || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }

        return r;
    }

    public int getDecimalDigits() {
        return 0;
    }
}
