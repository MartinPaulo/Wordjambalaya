package nz.co.martinpaulo.wordjambalaya;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by martinpaulo on 29/07/2014.
 */
public class WordDrawingView extends View {

    private static final String TAG = "WordDrawingView";
    public static final int LIGHT_RED = 0x22ff0000;
    public static final int LIGHT_CREAM = 0xfff8efe0;
    private boolean isActive;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private Paint circlePaint;
    private char character;
    private Paint wordPaint;


    public WordDrawingView(Context context) {
        this(context, null);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(LIGHT_CREAM);
        foregroundPaint = new Paint();
        foregroundPaint.setColor(LIGHT_RED);
        circlePaint = new Paint();
        circlePaint.setColor(LIGHT_RED);
        wordPaint = new Paint();
        wordPaint.setColor(Color.BLACK);
    }

    public WordDrawingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(backgroundPaint);
        final int width = canvas.getWidth() - 1;
        final int height = canvas.getHeight() - 1;
        canvas.drawRect(1, 1, width, height, foregroundPaint);
        if (isActive) {
            canvas.drawCircle(width / 2, height / 2, width / 2, circlePaint);
        }
        if (character != 0) {
            wordPaint.setTextSize(width - 2);
            wordPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(String.valueOf(character), width / 2, height - height / 5, wordPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 100;
        int desiredHeight = 100;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = getDesiredDimension(desiredWidth, widthMode, widthSize);
        int height = getDesiredDimension(desiredHeight, heightMode, heightSize);
        setMeasuredDimension(width, height);
    }

    private int getDesiredDimension(int desired, int mode, int actual) {
        if (mode == MeasureSpec.EXACTLY) {
            return actual;   //Must be this size
        } else if (mode == MeasureSpec.AT_MOST) {
            return Math.min(desired, actual); //Can't be bigger than...
        }
        //Be whatever you want
        return desired;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            isActive = !isActive;
            if (isActive) {
                Dictionary.getInstance().addSelectedChar(character);
            } else {
                Dictionary.getInstance().removeSelectedChar(character);
            }
            invalidate();
        }
        return true;
    }

    public void setCharacter(char character) {
        if (this.character > 0 && isActive) {
            Dictionary.getInstance().removeSelectedChar(this.character);
        }
        this.character = character;
        if (this.character > 0 && isActive) {
            Dictionary.getInstance().addSelectedChar(this.character);
        }
    }

    public void cleanUp() {
        if (isActive) {
            Dictionary.getInstance().removeSelectedChar(character);
            isActive = false;
        }
    }
}
