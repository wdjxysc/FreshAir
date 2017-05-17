package com.rthc.freshair.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.rthc.freshair.willianchartview.Tools;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/1.
 */
public class RcLineChart extends View {

    String TAG = "RCLineChart";

    String dataLineTitle = "TVOC";

    Float[] dataX = {0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f};
    Float[] dataY = {0f, 14f, 20f, 30f, 80f, 50f, 60f, 56f, 80f, 23f};

    String[] dataStrX = {"0----", "1----", "2----", "3----", "4----", "5----", "6----", "7----", "8----", "9----"};
    String[] dataStrY = {"0----", "1----", "2----", "3----", "4----", "5----", "6----", "7----", "8----", "9----"};

    Float[] valueAxisX = {0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f};
    Float[] valueAxisY = {0f, 40f, 50f, 80f, 90f};

    String[] valueStrAxisX = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    String[] valueStrAxisY = {"0", "40", "50", "80", "90"};

    int[] colorGridLineY = {Color.parseColor("#ffffff"),
            Color.parseColor("#34ff34"),
            Color.parseColor("#ffff00"),
            Color.parseColor("#ff6000"),
            Color.parseColor("#ff0000")};

    ArrayList<PointF> dataPointList = new ArrayList<PointF>();

    //曲线区边与控件边之间的空间
    float space =  Tools.fromDpToPx(15);


    float chartAreaLeftSpace = Tools.fromDpToPx(12);//曲线图左边纵坐标刻度留白
    float chartAreaRightSpace = Tools.fromDpToPx(0);//曲线图右边纵留白
    float chartAreaBottomSpace = Tools.fromDpToPx(7);//曲线图底部横坐标刻度留白
    float chartAreaTopSpace = Tools.fromDpToPx(30);//曲线顶部留白
    //曲线图区域
    RectF lineRectF = new RectF(chartAreaLeftSpace + space, chartAreaTopSpace + space, getWidth() - chartAreaRightSpace + space, getHeight() - chartAreaBottomSpace + space);


    int width;//控件宽度
    int height;//控件高度


    Paint lineAxisPaint;//轴线条画笔

    TextPaint mTextPaint;//文字画笔

    float textSize = Tools.fromDpToPx(10);//文字画笔字体大小

    Paint lineDataPaint;//曲线画笔

    Paint dotPaint;

    Canvas mCanvas;

    int bgColor = Color.parseColor("#FF5398c1");

    int toolTipBgColor1 = Color.parseColor("#FF216b97");
    int toolTipBgColor2 = Color.parseColor("#FFF45F03");
    int toolTipBgColor3 = Color.WHITE;

    int toolTipTextColor1 = Color.WHITE;
    int toolTipTextColor2 = Color.WHITE;
    int toolTipTextColor3 = Color.parseColor("#FF216b97");


    public void setParam(String dataLineTitle, int[] colorGridLineY,
                         @NonNull Float[] valueAxisX, @NonNull String[] valueStrAxisX,
                         @NonNull Float[] valueAxisY, @NonNull String[] valueStrAxisY) {

        this.colorGridLineY = colorGridLineY;
        this.dataLineTitle = dataLineTitle;
        this.valueAxisX = valueAxisX;
        this.valueAxisY = valueAxisY;
        this.valueStrAxisX = valueStrAxisX;
        this.valueStrAxisY = valueStrAxisY;

        invalidate();
    }


    public void setData(@NonNull Float[] dataX, @NonNull String[] dataStrX,
                        @NonNull Float[] dataY, @NonNull String[] dataStrY) {
        this.dataX = dataX;
        this.dataY = dataY;
        this.dataStrX = dataStrX;
        this.dataStrY = dataStrY;
        dataPointList.clear();
        invalidate();
    }

    public RcLineChart(Context context) {
        super(context);
    }

    public RcLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);

        // 实例化文本画笔并设置参数
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mTextPaint.setColor(Color.WHITE);

        // 实例化线条画笔并设置参数
        lineAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        lineAxisPaint.setStyle(Paint.Style.STROKE);
        lineAxisPaint.setStrokeJoin(Paint.Join.ROUND);
//        lineAxisPaint.setStrokeCap(Paint.Cap.ROUND);
        lineAxisPaint.setColor(Color.WHITE);

        // 实例化数据线条画笔并设置参数
        lineDataPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        lineDataPaint.setStyle(Paint.Style.STROKE);
        lineDataPaint.setStrokeJoin(Paint.Join.ROUND);
        lineDataPaint.setStrokeCap(Paint.Cap.ROUND);
        lineDataPaint.setColor(Color.WHITE);
        lineDataPaint.setStrokeWidth(3);

        // 实例化数据点画笔并设置参数
        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setColor(Color.GREEN);
    }

    public RcLineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        lineRectF = new RectF(chartAreaLeftSpace + space,
                chartAreaTopSpace + space,
                getWidth() - chartAreaRightSpace - space,
                getHeight() - chartAreaBottomSpace - space);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        width = getWidth();
        height = getHeight();




        //设置背景
        canvas.drawColor(bgColor);

        //画坐标轴
        drawAxis(canvas);

        //画数据线
        drawDataLine(canvas);

        //画当前选中数据的详细信息
        drawData(currentShowIndex, canvas);
    }


    int widthAxisX = 2; //X轴线宽
    int widthAxisY = 12;//Y轴线宽
    int radiusDot = 2; //数据点半径

    private void drawAxis(Canvas canvas) {

        lineAxisPaint.setStrokeWidth(widthAxisY);

//        Path path = new Path();
//        path.moveTo(space, space);
//        path.lineTo(space, height - space);
//        path.lineTo(width - space, height - space);
//        canvas.drawPath(path, lineAxisPaint);

        //画x轴及刻度
        canvas.drawLine(lineRectF.left, lineRectF.bottom, lineRectF.left, lineRectF.top, lineAxisPaint);
        lineAxisPaint.setStrokeWidth(1);
        lineAxisPaint.setColor(Color.parseColor("#99FFFFFF"));
        canvas.drawLine(lineRectF.left, lineRectF.bottom, lineRectF.right, lineRectF.bottom, lineAxisPaint);//底边
        canvas.drawLine(lineRectF.right, lineRectF.bottom, lineRectF.right, lineRectF.top, lineAxisPaint);//右边


        //画Y轴 有值 颜色
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        float offset = (mTextPaint.descent() + mTextPaint.ascent()) / 2;
        for (int i = 1; i < valueAxisY.length; i++) {

            float y = lineRectF.bottom - (valueAxisY[i] - valueAxisY[0]) * lineRectF.height() /  (valueAxisY[valueAxisY.length - 1] - valueAxisY[0]);
            mTextPaint.setColor(colorGridLineY[i]);
            canvas.drawText(valueStrAxisY[i], space, y - offset, mTextPaint);

            if (i != 0) {
                lineAxisPaint.setColor(colorGridLineY[i]);
                lineAxisPaint.setStrokeWidth(widthAxisY);
                float yLast = lineRectF.bottom - (valueAxisY[i - 1] - valueAxisY[0]) * lineRectF.height() / (valueAxisY[valueAxisY.length - 1] - valueAxisY[0]);
                canvas.drawLine(lineRectF.left, yLast, lineRectF.left, y, lineAxisPaint);//一段纵轴
                lineAxisPaint.setStrokeWidth(widthAxisX);
                canvas.drawLine(lineRectF.left, y, lineRectF.right, y, lineAxisPaint);//横向网格
            }
        }

        //画x轴文字
        mTextPaint.setColor(Color.WHITE);
        for (int i = 0; i < valueAxisX.length; i++) {
            float x = lineRectF.left + (valueAxisX[i] - valueAxisX[0]) * lineRectF.width() / (valueAxisX[valueAxisX.length - 1]-valueAxisX[0]);

            canvas.drawText(valueStrAxisX[i], x, lineRectF.bottom + chartAreaBottomSpace / 2 + offset + 10, mTextPaint);

            lineAxisPaint.setStrokeWidth(2);
            lineAxisPaint.setColor(Color.WHITE);
            canvas.drawLine(x, lineRectF.bottom, x, lineRectF.bottom + 10, lineAxisPaint);
        }

    }


    void drawDataLine(Canvas canvas) {
        //画数据线
        Path lineDataPath = new Path();
        for (int i = 0; i < dataX.length; i++) {
            float x = lineRectF.left + (dataX[i] - valueAxisX[0]) / (valueAxisX[valueAxisX.length - 1] - valueAxisX[0]) * lineRectF.width();
            float y = lineRectF.bottom - (dataY[i] - valueAxisY[0]) / (valueAxisY[valueAxisY.length - 1] - valueAxisY[0]) * lineRectF.height();

            dataPointList.add(new PointF(x, y));

            if (i == 0) {
                lineDataPath.moveTo(x, y);
            } else {
                lineDataPath.lineTo(x, y);
            }


        }
        canvas.drawPath(lineDataPath, lineDataPaint);
    }


    private static final int MODE_NONE = 0x00123;// 默认的触摸模式
    private static final int MODE_DRAG = 0x00321;// 拖拽模式
    private static final int MODE_ZOOM = 0x00132;// 缩放or旋转模式

    private int mode;// 当前的触摸模式

    private int currentShowIndex = -1;//当前显示的点index

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();

        int showIndex = getShowDataIndex(touchX);
        if ((!(showIndex < 0)) && showIndex<dataY.length) {
            Log.i(TAG, "index=" + showIndex + "x:" + dataX[showIndex] + "   y:" + dataY[showIndex]);

//            if(PtInRect(lineRectF,new PointF(event.getX(),event.getY()))) {
                if (showIndex != currentShowIndex) {//如果是新的数据就显示
                    currentShowIndex = showIndex;
                    postInvalidate();
                }
//            }else{
//                currentShowIndex = -1;
//                postInvalidate();
//            }
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 单点接触屏幕时
                mode = MODE_DRAG;


                break;
            case MotionEvent.ACTION_POINTER_DOWN:// 第二个点接触屏幕时
                break;
            case MotionEvent.ACTION_UP:// 单点离开屏幕时
                Log.i(TAG, "手指离开");
                currentShowIndex = -1;
                postInvalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:// 第二个点离开屏幕时
                mode = MODE_NONE;
                break;
            case MotionEvent.ACTION_MOVE:// 触摸点移动时
                float dx = event.getRawX();
                float dy = event.getRawY();
//                Log.i(TAG, "dx" + dx + "   dy" + dy);
                break;
            case MotionEvent.ACTION_OUTSIDE:

                break;
        }


        return super.onTouchEvent(event);
//        return false;
    }


    /**
     * 获取离 touchX最近的点  用于显示
     *
     * @param touchX
     * @return
     */
    int getShowDataIndex(float touchX) {

        int showIndex = -1;//需要显示的点的序号
        int smallerIndex = -1;
        int biggerIndex = -1;

        for (int i = 0; i < dataPointList.size(); i++) {
            if (touchX > dataPointList.get(i).x) {
                smallerIndex = i;
            } else {
                if (i > 0)
                    biggerIndex = i;
                break;
            }
        }


        if (biggerIndex > 0) {
            if ((touchX - dataPointList.get(smallerIndex).x) < (dataPointList.get(biggerIndex).x - touchX)) {
                showIndex = smallerIndex;
            } else {
                showIndex = biggerIndex;
            }
        }

        return showIndex;
    }


    /**
     * 显示index数据
     *
     * @param dataIndex 数据序号
     */
    void drawData(int dataIndex, Canvas canvas) {
        if (dataIndex < 0 || dataIndex > dataPointList.size() - 1) return;

        PointF pointF = dataPointList.get(dataIndex);

        float toolTipWidth = Tools.fromDpToPx(100);
        float toolTipHeight = Tools.fromDpToPx(35);

        float triangleHeight = Tools.fromDpToPx(6);

        float radiusDotPx = Tools.fromDpToPx(radiusDot);

        //画小倒三角形
        dotPaint.setColor(Color.WHITE);
        Path path = new Path();
        path.moveTo(pointF.x, pointF.y - radiusDotPx);
        path.lineTo((float) (pointF.x - triangleHeight * Math.tan(Math.PI / 6)), pointF.y - triangleHeight - 1 - radiusDotPx);
        path.lineTo((float) (pointF.x + triangleHeight * Math.tan(Math.PI / 6)), pointF.y - triangleHeight - 1 - radiusDotPx);
        path.close();
        canvas.drawPath(path, dotPaint);

        //画toolTip显示区域
        dotPaint.setColor(toolTipBgColor1);
        RectF rectF1 = new RectF(pointF.x - toolTipWidth / 2,
                pointF.y - triangleHeight - toolTipHeight - radiusDotPx,
                pointF.x + toolTipWidth / 2,
                pointF.y - triangleHeight - radiusDotPx);
        canvas.drawRect(rectF1, dotPaint);
        dotPaint.setColor(toolTipBgColor2);
        RectF rectF2 = new RectF(pointF.x - toolTipWidth / 2,
                pointF.y - triangleHeight - toolTipHeight / 2 - radiusDotPx,
                pointF.x - toolTipWidth * 1 / 8,
                pointF.y - triangleHeight - radiusDotPx);
        canvas.drawRect(rectF2, dotPaint);
        dotPaint.setColor(toolTipBgColor3);
        RectF rectF3 = new RectF(pointF.x - toolTipWidth * 1 / 8,
                pointF.y - triangleHeight - toolTipHeight / 2 - radiusDotPx,
                pointF.x + toolTipWidth / 2,
                pointF.y - triangleHeight - radiusDotPx);
        canvas.drawRect(rectF3, dotPaint);


        //确定数据点颜色 画选中数据点
        for (int i = 0; i < valueAxisY.length; i++) {
            if (dataY[dataIndex] < valueAxisY[i]) {
                dotPaint.setColor(colorGridLineY[i]);
                canvas.drawCircle(pointF.x, pointF.y, radiusDotPx, dotPaint);
                break;
            }
        }


        float offset = (mTextPaint.descent() + mTextPaint.ascent()) / 2;
        RectF rectFText1 = new RectF(rectF1.left, rectF1.top, rectF1.right, rectF1.bottom - rectF2.height());
        mTextPaint.setColor(toolTipTextColor1);
        canvas.drawText(dataStrX[dataIndex], rectFText1.centerX() - offset, rectFText1.centerY() - offset, mTextPaint);

        mTextPaint.setColor(toolTipTextColor2);
        canvas.drawText(dataLineTitle, rectF2.centerX(), rectF2.centerY() - offset, mTextPaint);

        mTextPaint.setColor(toolTipTextColor3);
        canvas.drawText(dataStrY[dataIndex], rectF3.centerX(), rectF3.centerY() - offset, mTextPaint);
    }


    static boolean PtInRect(RectF rectF, PointF pointF){
        boolean b =false;

        if(pointF.x>rectF.left&&pointF.x<rectF.right&&pointF.y<rectF.bottom&&pointF.y>rectF.top){
            b = true;
        }
        return b;
    }
}
