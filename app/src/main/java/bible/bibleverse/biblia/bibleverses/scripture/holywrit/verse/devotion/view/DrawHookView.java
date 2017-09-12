package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;


public class DrawHookView extends View {
    private int progress = 0;
    private float line1_x = 0;
    private float line1_y = 0;
    private float line2_x = 0;
    private float line2_y = 0;
    private int circleStrokeWidth, lineStrokeWidth, lineOffset;
    private DrawHookListener mDrawHookListener;

    public DrawHookView(Context context) {
        super(context);
        init();
    }

    public DrawHookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawHookView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public interface DrawHookListener {
        void onDrawEnd();
    }

    public void setDrawHookListener(DrawHookListener drawHookListener) {
        this.mDrawHookListener = drawHookListener;
    }

    private void init() {
        circleStrokeWidth = getResources().getDimensionPixelSize(R.dimen.cirle_stroke_width);
        lineStrokeWidth = getResources().getDimensionPixelSize(R.dimen.hook_stroke_width);
        lineOffset = getResources().getDimensionPixelSize(R.dimen.lineOffset);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            progress += 4;

            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.white));
            paint.setStrokeWidth(circleStrokeWidth);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);

            Paint linePaint = new Paint();
            linePaint.setColor(getResources().getColor(R.color.white));
            linePaint.setStrokeWidth(lineStrokeWidth);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setAntiAlias(true);

            float center = getWidth() / 2.0f;
            float center1 = getWidth() / 4.0f;
            float radius = getWidth() / 2.0f - circleStrokeWidth;

            RectF rectF = new RectF(center - radius - 1, center - radius - 1, center + radius + 1, center + radius + 1);

            canvas.drawArc(rectF, 235, -360 * progress / 100, false, paint);

            if (progress >= 100) {
                if (center1 + line1_x < 9 * radius / 10.0f) {
                    float interval = (9 * radius / 10.0f - center1) / 5.0f;
                    line1_x += interval;
                    line1_y += 1.2f * interval;
                }
                canvas.drawLine(center1, center, center1 + line1_x, center + line1_y, linePaint);

                if (center1 + line1_x == 9 * radius / 10.0f) {
//                line2_x = line1_x;
//                line2_y = line1_y;
//                line1_x++;
//                line1_y++;

                }
                if (center1 + line1_x >= 9 * radius / 10.0f && center1 + line1_x + line2_x <= 3 * radius / 2.0f) {
                    float interval = (3 * radius / 2.0f - 9 * radius / 10.0f) / 5.0f;
                    line2_x += interval;
                    line2_y -= 1.2f * interval;
                }
                canvas.drawLine(center1 + line1_x - lineOffset, center + line1_y, center1 + line1_x + line2_x, center + line1_y + line2_y, linePaint);
            }

            if (center1 + line1_x + line2_x <= 3 * radius / 2.0f) {
                postInvalidateDelayed(1);
            } else {
                if (this.mDrawHookListener != null) {
                    this.mDrawHookListener.onDrawEnd();
                }
            }
        } catch (Exception e) {
        }

    }

    public void reDraw() {
        progress = 0;
        line1_x = 0;
        line1_y = 0;
        line2_x = 0;
        line2_y = 0;
        invalidate();
    }

}  