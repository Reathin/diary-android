package com.rair.diary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.rair.diary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LockPattern extends View {
    private int side = 3;// 设置每个边有几个点，默认3*3九宫格
    private long CLEAR_TIME = 5000;
    private long ERROR_CLEAR_TIME = 1000;
    private Paint arrowPaint;
    private Paint errorPaint;
    private Paint linePaint;
    private Paint normalPaint;
    private Paint selectedPaint;

    private int innerColor = 0XEE898989;// 内圆颜色
    private int outerColor = 0XEED9D9D9;// 外圆颜色

    private int selectedInnerColor = 0XEE33FF11;// 选中时内圆颜色
    private int selectedOuterColor = 0XCCFFCC12;// 选中时外圆颜色

    private int errorInnerColor = 0XFFEA0945;// 密码错误时内圆颜色
    private int errorOuterColor = 0XFF901032;// 密码错误时外圆颜色

    private int lineColor = 0XFFFFA429;// 选中时线条颜色
    private int arrowColor = 0XFFFFA429;// 选中时箭头颜色
	//private int arrowColor = this.selectedInnerColor;// 选中时箭头颜色

    private int errorArrowColor = this.errorInnerColor;// 密码错误时箭头颜色
    private int errorlineColor = this.errorInnerColor;// 密码错误时线条颜色

    private float outerRadius;// 外圆半径
    private float innerRadius;// 内圆半径
    private boolean isChecking = false;// 是否正在滑屏
    private boolean hasDrawArrow = true;// 是否显示箭头
    private boolean isInitialized = false;
    private Point[][] mPoints = new Point[side][side];
    float moveingX;
    float moveingY;
    private boolean movingNoPoint = false;// 滑屏时未到达下一个点
    private int pwdMaxLen = side * side;// 密码最大长度
    private int pwdMinLen = 4;// 密码最小长度，默认4
    private List<Point> selectedPoints = new ArrayList<Point>();
    private boolean touchEnable = true;
    private float viewHeight;
    private float viewWidth;

    public LockPattern(Context context) {
        super(context);
    }

    public LockPattern(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initAttrs(context, attributeSet);
    }

    public LockPattern(Context paramContext, AttributeSet attributeSet, int paramInt) {
        super(paramContext, attributeSet, paramInt);
    }

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.LockPattern);
        pwdMinLen = array.getInteger(R.styleable.LockPattern_min_length, 3);
        pwdMaxLen = array.getInteger(R.styleable.LockPattern_max_length, 9);
        innerColor = array.getColor(R.styleable.LockPattern_inner_color, 0XFF392F27);
        outerColor = array.getColor(R.styleable.LockPattern_outer_color, 0XFF645E5C);
        selectedInnerColor = array.getColor(R.styleable.LockPattern_selected_inner_color, 0XFFFEB110);
        selectedOuterColor = array.getColor(R.styleable.LockPattern_selected_outer_color, 0XFFA26522);
        hasDrawArrow = array.getBoolean(R.styleable.LockPattern_draw_arrow, true);
        array.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!touchEnable)
            return false;
        movingNoPoint = false;
        float ex = event.getX();
        float ey = event.getY();
        Point p = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (task != null) {
                    task.cancel();
                    task = null;
                }
                reset();
                p = isInRound(ex, ey);
                if (p != null)
                    isChecking = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isChecking) {
                    p = isInRound(ex, ey);
                    if (p == null) {
                        movingNoPoint = true;
                        moveingX = ex;
                        moveingY = ey;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                p = isInRound(ex, ey);
                isChecking = false;
                break;
        }
        if (isChecking && p != null) {
            if (isNewPoint(p)) {
                p.state = Point.STATE_CHECK;
                selectedPoints.add(p);
            } else {
                movingNoPoint = true;
                moveingX = ex;
                moveingY = ey;
            }
        }

        if (!isChecking && mCompleteListener != null) {
            if (selectedPoints.isEmpty())
                return true;

            this.touchEnable = false;
            if (selectedPoints.size() < pwdMinLen || selectedPoints.size() > pwdMaxLen) {
                mCompleteListener.onPwdShortOrLong(selectedPoints.size());
                passwordError();
            } else {
                mCompleteListener.onComplete(getPassword());
                clearPassword(CLEAR_TIME);
            }
        }
        this.postInvalidate();
        return true;
    }

    /**
     * 密码数字以英文逗号分隔，直接使用加密工具加密即可
     */
    private String getPassword() {
        StringBuilder pwd = new StringBuilder();
        for (Point p : selectedPoints)
            pwd.append(",").append(p.value);
        return pwd.deleteCharAt(0).toString();
    }

    private boolean isNewPoint(Point p) {
        if (selectedPoints.contains(p))
            return false;
        return true;
    }

    private Point isInRound(float x, float y) {
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++)
                if (MathUtils.isInRound(mPoints[i][j].x, mPoints[i][j].y, outerRadius, x, y))
                    return mPoints[i][j];
        }
        return null;
    }

    private void drawArrow(Canvas canvas, Paint paint, Point start, Point end, float arrowHeight) {
        double d = MathUtils.distance(start.x, start.y, end.x, end.y);
        float cosB = (float) ((end.x - start.x) / d);
        float sinB = (float) ((end.y - start.y) / d);
        float tanC = (float) Math.tan(Math.PI / 4);// 箭头尖锐程度，默认为直角
        float h = (float) (d - arrowHeight - outerRadius);
        float l = arrowHeight * tanC;
        float a = l * cosB;
        float b = l * sinB;
        float x0 = h * cosB;
        float y0 = h * sinB;
        float x1 = start.x + (h + arrowHeight) * cosB;
        float y1 = start.y + (h + arrowHeight) * sinB;
        float x2 = start.x + x0 - b;
        float y2 = start.y + y0 + a;
        float x3 = start.x + x0 + b;
        float y3 = start.y + y0 - a;
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawLine(Point start, Point end, Canvas canvas, Paint paint) {
        double d = MathUtils.distance(start.x, start.y, end.x, end.y);
        float cosA = (float) ((end.x - start.x) / d);
        float sinA = (float) ((end.y - start.y) / d);
        float rx = cosA * innerRadius;
        float ry = sinA * innerRadius;
        canvas.drawLine(rx + start.x, ry + start.y, end.x - rx, end.y - ry, paint);
    }

    private void drawLockPattern(Canvas canvas) {
        boolean isError = false;
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++) {
                Point p = mPoints[i][j];
                int tempOuterColor = 0;
                int tempInnerColor = 0;
                Paint temP = null;
                switch (p.state) {
                    case Point.STATE_CHECK:
                        tempOuterColor = selectedOuterColor;
                        tempInnerColor = selectedInnerColor;
                        temP = selectedPaint;
                        break;

                    case Point.STATE_CHECK_ERROR:
                        isError = true;
                        tempOuterColor = errorOuterColor;
                        tempInnerColor = errorInnerColor;
                        temP = errorPaint;
                        break;

                    case Point.STATE_NORMAL:
                        tempOuterColor = outerColor;
                        tempInnerColor = innerColor;
                        temP = normalPaint;
                        break;
                }
                temP.setColor(tempOuterColor);
                canvas.drawCircle(p.x, p.y, outerRadius, temP);
                temP.setColor(tempInnerColor);
                canvas.drawCircle(p.x, p.y, innerRadius, temP);
            }
        }

        if (isError) {
            arrowPaint.setColor(errorArrowColor);
            linePaint.setColor(errorlineColor);
        } else {
            arrowPaint.setColor(arrowColor);
            linePaint.setColor(lineColor);
        }

        if (selectedPoints.size() > 0) {
            Point tp = selectedPoints.get(0);
            for (int i = 1; i < selectedPoints.size(); i++) {
                Point p = selectedPoints.get(i);
                drawLine(tp, p, canvas, linePaint);
                if (hasDrawArrow)
                    drawArrow(canvas, arrowPaint, tp, p, innerRadius);
                tp = p;
            }
            if (this.movingNoPoint)
                drawLine(tp, new Point(moveingX, moveingY, -1), canvas, linePaint);
        }
    }

    private void init() {
        viewWidth = getWidth();
        viewHeight = getHeight();
        float offsetX = 0;
        float offsetY = 0;
        // 强烈建议AndroidManifest.xml中对应的Activity加入
        // android:screenOrientation="portrait"强制竖屏
        if (viewWidth > viewHeight) {// 防止横屏
            offsetX = (viewWidth - viewHeight) / 2;
            viewWidth = viewHeight;
        } else {// 竖屏
            offsetY = (viewHeight - viewWidth) / 2;
            viewHeight = viewWidth;
        }

        int avgCount = 3 * side + 1;
        float spacing = viewWidth / avgCount;

        int value = 0;
        for (int i = 0; i < mPoints.length; i++) {
            float y = offsetY + (3 * i + 2) * spacing;
            for (int j = 0; j < mPoints[i].length; j++) {
                float x = offsetX + (3 * j + 2) * spacing;
                mPoints[i][j] = new Point(x, y, value++);
            }
        }

        outerRadius = spacing;
        innerRadius = spacing / 4;
        isInitialized = true;
        initPaints();
    }

    /**
     * 初始化画笔
     */
    private void initPaints() {
        this.arrowPaint = new Paint();
        this.arrowPaint.setColor(this.arrowColor);
        this.arrowPaint.setStyle(Paint.Style.FILL);
        this.arrowPaint.setAntiAlias(true);

        this.linePaint = new Paint();
        this.linePaint.setColor(this.lineColor);
        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setAntiAlias(true);
        this.linePaint.setStrokeWidth(this.outerRadius / 8);

        this.selectedPaint = new Paint();
        this.selectedPaint.setStyle(Paint.Style.STROKE);
        this.selectedPaint.setAntiAlias(true);
        this.selectedPaint.setStrokeWidth(this.outerRadius / 6);

        this.errorPaint = new Paint();
        this.errorPaint.setStyle(Paint.Style.STROKE);
        this.errorPaint.setAntiAlias(true);
        this.errorPaint.setStrokeWidth(this.outerRadius / 6);

        this.normalPaint = new Paint();
        this.normalPaint.setStyle(Paint.Style.STROKE);
        this.normalPaint.setAntiAlias(true);
        this.normalPaint.setStrokeWidth(this.outerRadius / 9);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!(this.isInitialized))
            init();
        drawLockPattern(canvas);
    }

    private Timer timer = new Timer();
    private TimerTask task = null;

    /**
     * 密码错误时调用，显示错误轨迹，1秒后清屏
     */
    public void passwordError() {
        for (Point p : selectedPoints)
            p.state = Point.STATE_CHECK_ERROR;
        clearPassword(ERROR_CLEAR_TIME);
    }

    private void clearPassword(final long time) {
        if (time > 1) {
            if (task != null)
                task.cancel();
            postInvalidate();
            timer.schedule(new TimerTask() {
                public void run() {
                    reset();
                    postInvalidate();
                }
            }, time);
        } else {
            reset();
            postInvalidate();
        }
    }

    private void reset() {
        for (Point p : selectedPoints)
            p.state = Point.STATE_NORMAL;
        selectedPoints.clear();
        touchEnable = true;
    }

    private OnCompleteListener mCompleteListener;

    public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }

    public interface OnCompleteListener {
        /**
         * 当密码滑屏完毕
         *
         * @param password
         */
        void onComplete(String password);

        /**
         * 当密码太短或太长时调用
         *
         * @param pwdLength
         */
        void onPwdShortOrLong(int pwdLength);
    }
}

/**
 * 此处为方便使用,请根据需求单独放在一个工具类中
 */
class MathUtils {
    /**
     * 点(x2,y2)是否在以(x1,y1)为圆心，r为半径的圆内
     */
    static boolean isInRound(double x1, double y1, double r, double x2, double y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) < r * r;
    }

    /**
     * 两点间距
     */
    static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}


/**
 * 此处为方便使用,请根据需求单独放在一个Java类中
 */
class Point {
    static final int STATE_NORMAL = 0; // 默认未选中
    static final int STATE_CHECK = 1; // 选中
    static final int STATE_CHECK_ERROR = 2;// 选中密码错误或密码太短，太长

    /**
     * 圆心x坐标
     */
    float x;
    /**
     * 圆心y坐标
     */
    float y;
    /**
     * 该点当前状态
     */
    int state = STATE_NORMAL;
    /**
     * 该点对应的密码值
     */
    int value;

    Point(float x, float y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }
}