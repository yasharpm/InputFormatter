package com.yashoid.inputformatter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextDirectionHeuristics;
import android.text.style.ReplacementSpan;

/**
 * Created by Yashar on 10/25/2017.
 */

public class FormattableText implements CharSequence {

    private class FormattableChar extends ReplacementSpan {

        private int index;
        private String c;

        private String drawingText;
        private float drawingTextLength;
        private float startX;
        private float endX;

        protected FormattableChar(int index, char c) {
            this.index = index;
            this.c = "" + c;
        }

        protected int length() {
            return c.length();
        }

        protected char charAt(int index) {
            return c.charAt(index);
        }

        protected CharSequence subSequence(int start, int end) {
            return c.subSequence(start, end);
        }

        protected void insert(int index, String s) {
            c = c.substring(0, index) + s + c.substring(index, c.length());
        }

        protected void delete(int start, int end) {
            c = c.substring(0, start) + c.substring(end, c.length());
        }

        protected void replaceAll(char src, char dst) {
            c = c.replace(src, dst);
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            int startIndex = FormattableText.this.length(index);
            int endIndex = FormattableText.this.length(index + 1);

            int lineIndex = mBigPicture.substring(0, startIndex).lastIndexOf('\n', startIndex);

            if (lineIndex >= 0) {
                drawingText = mBigPicture.substring(lineIndex + 1);

                startIndex -= lineIndex + 1;
                endIndex -= lineIndex + 1;
            }
            else {
                drawingText = mBigPicture;
            }

            drawingTextLength = paint.measureText(drawingText);

            startX = paint.measureText(drawingText, 0, startIndex);
            endX = paint.measureText(drawingText, 0, endIndex);

            return (int) (endX - startX);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            Paint.FontMetrics fm = paint.getFontMetrics();

            mHelperRect.set(x, -1000, x + endX - startX, 1000);

            canvas.save();
            canvas.clipRect(mHelperRect);

            if (mIsRtl) {
                x -= drawingTextLength - (endX);
            }
            else {
                x -= startX;
            }

            canvas.drawText(drawingText, x, top - fm.top, paint);

            canvas.restore();
        }

    }

    private String mBigPicture;
    private boolean mIsRtl;

    private FormattableChar[] mChars;

    private RectF mHelperRect = new RectF();

    protected FormattableText(CharSequence text) {
        mBigPicture = text.toString();
        onBigPictureChanged();

        mChars = new FormattableChar[text.length()];

        for (int i = 0; i < mChars.length; i++) {
            mChars[i] = new FormattableChar(i, text.charAt(i));
        }
    }

    @Override
    public int length() {
        int len = 0;

        for (FormattableChar c: mChars) {
            len += c.length();
        }

        return len;
    }

    protected int length(int end) {
        int len = 0;

        for (int i = 0; i < end; i++) {
            len += mChars[i].length();
        }

        return len;
    }

    @Override
    public char charAt(int index) {
        CharLookupResult result = lookupChar(index);

        if (result == null) {
            throw new RuntimeException("Are you sure index " + index + " is within valid range " + length() + "?");
        }

        return result.c.charAt(result.indexOffset);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        CharLookupResult startResult = lookupChar(start);
        CharLookupResult endResult = lookupChar(end);

        if (startResult.charIndex == endResult.charIndex) {
            return startResult.c.subSequence(startResult.indexOffset, endResult.indexOffset);
        }

        StringBuilder sb = new StringBuilder();

        sb.append(startResult.c.subSequence(startResult.indexOffset, startResult.c.length()));

        for (int i = startResult.charIndex + 1; i < endResult.charIndex; i++) {
            sb.append(mChars[i].subSequence(0, mChars[i].length()));
        }

        if (endResult.charIndex < mChars.length) {
            sb.append(mChars[endResult.charIndex].subSequence(0, endResult.indexOffset));
        }

        return sb.toString();
    }

    public FormattableText insert(int index, String s) {
        mBigPicture = mBigPicture.substring(0, index) + s + mBigPicture.substring(index);
        onBigPictureChanged();

        CharLookupResult result = lookupChar(index);

        result.c.insert(result.indexOffset, s);

        return this;
    }

    public FormattableText delete(int start, int end) {
        mBigPicture = mBigPicture.substring(0, start) + mBigPicture.substring(end);
        onBigPictureChanged();

        CharLookupResult startResult = lookupChar(start);
        CharLookupResult endResult = lookupChar(end);

        if (startResult.charIndex == endResult.charIndex) {
            startResult.c.delete(startResult.indexOffset, endResult.indexOffset);
        }
        else {
            startResult.c.delete(startResult.indexOffset, startResult.c.length());

            for (int i = startResult.charIndex + 1; i < endResult.charIndex; i++) {
                mChars[i].delete(0, mChars[i].length());
            }

            if (endResult.charIndex < mChars.length) {
                endResult.c.delete(0, endResult.indexOffset);
            }
        }

        return this;
    }

    public FormattableText replace(int start, int end, String s) {
        return delete(start, end).insert(start, s);
    }

    public FormattableText replaceAll(char src, char dst) {
        mBigPicture = mBigPicture.replace(src, dst);
        onBigPictureChanged();

        for (FormattableChar c: mChars) {
            c.replaceAll(src, dst);
        }

        return this;
    }

    @Override
    public String toString() {
        return mBigPicture;
    }

    protected void addSpans(Spannable s) {
        FormattableChar[] spans = s.getSpans(0, s.length(), FormattableChar.class);

        for (FormattableChar span: spans) {
            s.removeSpan(span);
        }

        for (int i = 0; i < mChars.length; i++) {
            s.setSpan(mChars[i], i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void onBigPictureChanged() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mIsRtl = TextDirectionHeuristics.FIRSTSTRONG_LTR.isRtl(mBigPicture, 0, mBigPicture.length());
        }
        else {
            mIsRtl = mBigPicture.matches("[\\p{IsAlphabetic}&&\\W]");
        }
    }

    protected boolean isRtl() {
        return mIsRtl;
    }

    private CharLookupResult lookupChar(int index) {
        int current = 0;
        int seekIndex = 0;

        FormattableChar c = null;
        int indexOffset = 0;

        while (current < mChars.length) {
            c = mChars[current];

            seekIndex += c.length();

            if (seekIndex > index) {
                indexOffset = seekIndex - index - 1;
                break;
            }

            current++;
        }

        return c == null ? null : new CharLookupResult(current, c, indexOffset);
    }

    private class CharLookupResult {

        int charIndex;
        FormattableChar c;
        int indexOffset;

        private CharLookupResult(int charIndex, FormattableChar c, int indexOffset) {
            this.charIndex = charIndex;
            this.c = c;
            this.indexOffset = indexOffset;
        }

    }

}
