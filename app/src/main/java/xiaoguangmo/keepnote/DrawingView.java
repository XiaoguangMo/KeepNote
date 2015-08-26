package xiaoguangmo.keepnote;

/**
 * Created by Xiaoguang Mo on 3/01/15.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;

public class DrawingView extends View implements OnTouchListener {

    private Canvas m_Canvas;

    private Path m_Path;

    private Paint m_Paint;

    private ArrayList<Pair<Path, Paint>> paths = new ArrayList<Pair<Path, Paint>>();

    private ArrayList<Pair<Path, Paint>> undonePaths = new ArrayList<Pair<Path, Paint>>();

    private float mX, mY;

    private static final float TOUCH_TOLERANCE = 4;

    private int setPainterColor = 0;

    private int setPainterSize = 10;


    /**
     * Constructor of DrawingView, set default variables
     * @param context
     * @param attr
     */
    public DrawingView(Context context, AttributeSet attr) {
        super(context, attr);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        this.setOnTouchListener(this);
        onCanvasInitialization();
    }

    /**
     * Initialize the setting of canvas and painter
     */
    public void onCanvasInitialization() {

        m_Paint = new Paint();
        m_Paint.setAntiAlias(true);
        m_Paint.setDither(true);
        m_Paint.setColor(Color.BLACK);
        m_Paint.setStyle(Paint.Style.STROKE);
        m_Paint.setStrokeJoin(Paint.Join.ROUND);
        m_Paint.setStrokeCap(Paint.Cap.ROUND);
        m_Paint.setStrokeWidth(2);

        m_Canvas = new Canvas();

        m_Path = new Path();
        Paint newPaint = new Paint(m_Paint);
        paths.add(new Pair<Path, Paint>(m_Path, newPaint));

    }

    /**
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Manage the touch of user
     * @param arg0
     * @param event
     * @return
     */
    public boolean onTouch(View arg0, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    /**
     * Draw path on this canvas
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        for (Pair<Path, Paint> p : paths) {
            canvas.drawPath(p.first, p.second);
        }
    }

    /**
     * When touch start, note it and set color and size of painter, then start painting
     * @param x
     * @param y
     */
    private void touch_start(float x, float y) {

        switch(setPainterColor) {
            case 0: {
                m_Paint.setColor(Color.BLACK);
                break;
            }
            case 1: {
                m_Paint.setColor(Color.WHITE);
                break;
            }
            case 2: {
                m_Paint.setColor(Color.RED);
                break;
            }
            case 3: {
                m_Paint.setColor(Color.YELLOW);
                break;
            }
            case 4: {
                m_Paint.setColor(Color.BLUE);
                break;
            }
        }
        m_Paint.setStrokeWidth(setPainterSize);
        Paint newPaint = new Paint(m_Paint); // Clones the mPaint object
        paths.add(new Pair<Path, Paint>(m_Path, newPaint));
        m_Path.reset();
        m_Path.moveTo(x, y);
        mX = x;
        mY = y;
    }

    /**
     * When touch moves, keep track of move
     * @param x x coordination
     * @param y y coordination
     */
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            m_Path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    /**
     * When touch is finished, draw a line
     */
    private void touch_up() {
        m_Path.lineTo(mX, mY);

        // commit the path to our offscreen
        m_Canvas.drawPath(m_Path, m_Paint);

        // kill this so we don't double draw
        m_Path = new Path();
        Paint newPaint = new Paint(m_Paint); // Clones the mPaint object
        paths.add(new Pair<Path, Paint>(m_Path, newPaint));
    }

    /**
     * Set the color of painter
     * @param color color of painter
     */
    public void setPaintColor(int color)
    {
        setPainterColor = color;
    }

    /**
     * Set the size of painter
     * @param size size of painter
     */
    public void setPaintSize(int size)
    {
        setPainterSize = size;
    }

    /**
     * Get the color of painter
     * @return color of painter
     */
    public int checkColor()
    {
        return setPainterColor;
    }

    /**
     * Get the size of painter
     * @return size of painter
     */
    public int checkSize()
    {
        return setPainterSize;
    }

    /**
     * reset the whole picture, clear all lines drawn
     */
    public void reset()
    {
        paths.clear();
        invalidate();
    }

    /**
     * Undo a line
     */
	public void onClickUndo() {
		if (paths.size() > 0) {
			undonePaths.add(paths.remove(paths.size() - 1));
			invalidate();
		} else {

		}
	}

    /**
     * Redo a line, because the limitation of space on tool bar, I didn't put it in use
     */
	public void onClickRedo() {
		if (undonePaths.size() > 0) {
			paths.add(undonePaths.remove(undonePaths.size() - 1));
			invalidate();
		} else {

		}
	}

}
