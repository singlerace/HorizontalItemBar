package com.example.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HorizontalItemBar extends View {
    //控制显示多少个条目
    private int showItemNum;
    //是否显示指示的图标
    private boolean isShowIndicateIcon = true;

    private float paddingLeft;
    private float paddingTop;
    private float paddingRight;
    private float paddingBottom;
    private float average;
    private int drawNumber;

    private float indicator_width = 10;
    private float indicator_height = 5;


    private int indicator_color;
    //指示器的默认位置，从零开始，默认选中第一个条目，最大不能超过要显示的条目个数
    private int indicator_default_position;
    private int indicator_position = -1;

    //条目正常文字颜色
    private int normalTextColor;
    //条目选中时文字颜色
    private int checkedItemTextColor;
    private float checkedItemTextSize;
    private Paint indicatorPaint;
    private float textSize;

    private ArrayList<String> dataList = new ArrayList<>();
    private LinkedList<String> showList = new LinkedList<>();
    private Paint textPaint;
    private MoveListener moveListener;

    private Style indicator = Style.INDICATOR1;
    private Rect textRect;
    private Rect indicatorRect;
    private int measureWidth;
    private int measureHeight;
    private OnConfirmListener onConfirmListener;
    private int startIndex;


    private boolean isLoop = false;
    private Boolean isFocused = true;
    private boolean itemAlignIndicator;
    private boolean isFirst = true;

    public void setRespondUpAndDown(boolean respondingUpAndDown) {
        RespondingUpAndDown = respondingUpAndDown;
    }

    private boolean RespondingUpAndDown = true;


    public HorizontalItemBar(Context context) {
        this(context, null);
    }

    public HorizontalItemBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalItemBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizontalItemBar);
        showItemNum = typedArray.getInteger(R.styleable.HorizontalItemBar_show_ItemNumber, 2);
        isShowIndicateIcon = typedArray.getBoolean(R.styleable.HorizontalItemBar_isShow_indicateIcon, true);

        normalTextColor = typedArray.getColor(R.styleable.HorizontalItemBar_normalTextColor, Color.WHITE);
        checkedItemTextColor = typedArray.getColor(R.styleable.HorizontalItemBar_checkItemTextColor, Color.BLACK);
        textSize = typedArray.getDimension(R.styleable.HorizontalItemBar_android_textSize, 15);
        checkedItemTextSize = typedArray.getDimension(R.styleable.HorizontalItemBar_checkItemTextSize, (int) textSize);

        paddingLeft = typedArray.getDimension(R.styleable.HorizontalItemBar_android_paddingLeft, 0);
        paddingRight = typedArray.getDimension(R.styleable.HorizontalItemBar_android_paddingRight, 0);
        paddingTop = typedArray.getDimension(R.styleable.HorizontalItemBar_android_paddingTop, 0);
        paddingBottom = typedArray.getDimension(R.styleable.HorizontalItemBar_android_paddingBottom, 0);

        indicator_width = typedArray.getDimension(R.styleable.HorizontalItemBar_indicator_width, 10);
        indicator_height = typedArray.getDimension(R.styleable.HorizontalItemBar_indicator_height, 5);
        indicator_color = typedArray.getColor(R.styleable.HorizontalItemBar_indicator_color, 0);
        indicator_default_position = typedArray.getInteger(R.styleable.HorizontalItemBar_indicator_default_position, 0);

        itemAlignIndicator = typedArray.getBoolean(R.styleable.HorizontalItemBar_itemAlignIndicator, false);

        if (indicator_default_position > showItemNum) {
            indicator_default_position = showItemNum - 1;
        }
        isLoop = typedArray.getBoolean(R.styleable.HorizontalItemBar_isLoop, false);
        typedArray.recycle();

        textPaint = new Paint();
        textPaint.setColor(normalTextColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(10);

        indicatorPaint = new Paint();
        indicatorPaint.setColor(indicator_color);
        indicatorPaint.setTextSize(textSize);
        indicatorPaint.setStrokeWidth(5);

        textRect = new Rect();
        indicatorRect = new Rect();
        if (indicator_position == -1) {
            indicator_position = indicator_default_position;
            startIndex = indicator_default_position;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;
        if (widthMode == MeasureSpec.AT_MOST) {
            width = MeasureSpec.makeMeasureSpec(200 * 2, MeasureSpec.EXACTLY);
            height = MeasureSpec.makeMeasureSpec(40 * 2, MeasureSpec.EXACTLY);
            setMeasuredDimension(width, height);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int left = 0;
    private int top = 0;
    private int right = 0;
    private int bottom = 0;


    private void initData() {
        if (showItemNum == dataList.size()) {
            average = (measureWidth - paddingLeft - paddingRight) / dataList.size();
            drawNumber = dataList.size();
        } else if (showItemNum < dataList.size()) {
            average = (measureWidth - paddingLeft - paddingRight) / showItemNum;
            drawNumber = showItemNum;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initData();
        int offx = 0;
        float averageHalf = average / 2;

        //根据样式来绘制指示块
        switch (indicator) {
            case INDICATOR1: //第一次View绘制完成后指示器位置为默认的位置
                //计算指示器位置数据
                left = (int) (paddingLeft + indicator_position * average + (averageHalf - indicator_width / 2));
                top = (int) (measureHeight - indicator_height);
                break;
            case INDICATOR2:
                left = (int) (paddingLeft + indicator_default_position * average + (averageHalf - indicator_width / 2));
                top = (int) (paddingTop + (measureHeight - indicator_height) / 2);
                break;
        }
        if (isShowIndicateIcon) {
            right = (int) (left + indicator_width);
            bottom = (int) (top + indicator_height);
            indicatorRect.set(left, top, right, bottom);
            canvas.drawRect(indicatorRect, indicatorPaint);
        }


        //绘制文字
        if (showList != null && showList.size() != 0) {
            for (int i = 0; i < drawNumber; i++) {
                textPaint.getTextBounds(showList.get(i), 0, showList.get(i).length(), textRect);
                float textWidth = textRect.width();
                float textHeight = textRect.height();
                float textLengthHalf = textWidth / 2;
                if (i == indicator_position) {
                    textPaint.setColor(checkedItemTextColor);
                    textPaint.setTextSize(checkedItemTextSize);
                } else {
                    textPaint.setTextSize(textSize);
                    textPaint.setColor(normalTextColor);
                }
                canvas.drawText(showList.get(i), paddingLeft + offx * average + (averageHalf - textLengthHalf), measureHeight / 2 + textHeight / 2, textPaint);
                offx++;
            }
        }
    }

    /**
     * @param dataList 数据
     * @param same     再次设置数据时条目位置是否要跟之前保持一致
     * @throws Exception
     */
    public void setData(List<String> dataList, boolean same) throws Exception {
        if (showItemNum > dataList.size()) {
            throw new Exception("显示条目的数量请勿大于list.size");
        }
        if (dataList != null) {
            this.dataList.clear();
            if (showList != null) {
                showList.clear();
            }
            this.dataList.addAll(dataList);
            switch (indicator) {
                case INDICATOR1:
                    for (int i = 0; i < showItemNum; i++) {
                        showList.add(dataList.get(i));
                    }
                    break;
                case INDICATOR2:
                    this.showList.addAll(dataList);
                    //是不是第一次传入数据
                    if (isFirst) {
                        isFirst = false;
                        //第一个元素要跟指示器位置对齐
                        if (itemAlignIndicator) {
                            startIndex = 0;
                            for (int i = 0; i < indicator_default_position; i++) {
                                String remove = showList.removeLast();
                                showList.addFirst(remove);
                            }
                        }
                    } else {
                        //中英文切换时,重新传入数据,显示的条目位置要不要跟之前一样
                        if (same) {
                            for (int i = 0; i <= startIndex + indicator_default_position + 1; i++) {
                                String remove = showList.removeFirst();
                                showList.addLast(remove);
                            }
                        }
                    }
                    break;
            }
            if (moveListener != null) {
                moveListener.move(startIndex, dataList.get(startIndex), -1);
            }
            invalidate();
        }
    }

    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    public boolean isFocused() {
        return isFocused;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (isFocused) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                int keyCode = event.getKeyCode();
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    switch (indicator) {
                        case INDICATOR1:
                            if (!in) {
                                startIndex = calculateArrangement(false, keyCode);
                                in = false;
                            }
                            break;
                        case INDICATOR2:
                            String remove = showList.removeLast();
                            showList.addFirst(remove);
                            executeMove(keyCode);
                            break;
                    }
                    invalidate();
                    return true;
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    switch (indicator) {
                        case INDICATOR1:
                            if (!in) {
                                startIndex = calculateArrangement(true, keyCode);

                                in = false;
                            }
                            break;
                        case INDICATOR2:
                            String remove = showList.removeFirst();
                            showList.addLast(remove);
                            executeMove(keyCode);
                            break;
                    }

                    invalidate();
                    return true;
                }
                if (RespondingUpAndDown) {
                    if (indicator == Style.INDICATOR1) {
                        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                            if (moveListener != null) {
                                moveListener.move(startIndex, dataList.get(startIndex), keyCode);
                            }
                            return true;
                        }
                        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                            if (!in) {
                                if (moveListener != null) {
                                    moveListener.move(startIndex, dataList.get(startIndex), keyCode);
                                }
                                in = false;
                            }

                            this.setFocusable(false);
                            return true;
                        }

                    }

                }


                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                    Log.e("================", "确认键");
                    if (onConfirmListener != null) {
                        onConfirmListener.click(startIndex, dataList.get(startIndex));
                    }
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private int calculateArrangement(boolean lr, int keyCode) {
        String s = "";
        in = true;
        int temp = startIndex;
        if (!lr) {
            temp--;
            indicator_position--;
            if (indicator_position < 0) {
                if (temp < 0) {
                    if (isLoop) {
                        temp = dataList.size() - 1;
                    } else {
                        temp = 0;
                        indicator_position = 0;
                        return temp;
                    }

                }
                s = dataList.get(temp);
                showList.addFirst(s);
                showList.removeLast();
                indicator_position = 0;
            } else {
                if (temp < 0) {
                    temp = dataList.size() - 1;
                }
                s = dataList.get(temp);
            }
        } else {
            temp++;
            indicator_position++;
            if (indicator_position > showItemNum - 1) {
                if (temp > dataList.size() - 1) {
                    if (isLoop) {
                        temp = 0;
                    } else {
                        temp = dataList.size() - 1;
                        indicator_position = showItemNum - 1;
                        return temp;
                    }

                }
                s = dataList.get(temp);
                showList.addLast(s);
                showList.removeFirst();
                indicator_position = showItemNum - 1;
            } else {
                if (temp > dataList.size() - 1) {
                    temp = 0;
                }
                s = dataList.get(temp);
            }
        }
        if (moveListener != null) {
            moveListener.move(temp, s, keyCode);
        }
        return temp;
    }

    //执行样式2左右移动回调方法
    private void executeMove(int keyCode) {
        String checkString = showList.get(indicator_default_position);
        startIndex = dataList.indexOf(checkString);
        if (moveListener != null) {
            moveListener.move(startIndex, checkString, keyCode);
        }
    }

    @Override
    public void setFocusable(boolean focusable) {
        this.isFocused = focusable;
        isShowIndicateIcon = focusable;
        requestFocus();
        invalidate();
        super.setFocusable(focusable);
    }


    /**
     * 用来计算整个显示的条目的排列
     *
     * @param lr 向左移动传入false，向右移动为true
     */

    private boolean in;


    private void setIsFocused(Boolean b) {
        this.isFocused = b;
    }

    /**
     * 是否显示指示器，只对样式1生效
     *
     * @param keyCode
     * @return
     */
    private boolean isShowIndicateIcon(int keyCode) {
        return isShowIndicateIcon;
    }

    /**
     * 设置左右移动回调监听
     *
     * @param moveListener
     */
    public void setMoveListener(MoveListener moveListener) {
        this.moveListener = moveListener;
    }

    public interface MoveListener {
        void move(int index, String name, int keyCode);
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public interface OnConfirmListener {
        void click(int index, String name);
    }


    public void setIndicatorChecedPosition(int indicatorDefaultPosition) {
        this.indicator_default_position = indicatorDefaultPosition;
    }

    /**
     * 设置样式
     *
     * @param style
     */
    public void setIndicatorStyle(Style style) {
        this.indicator = style;
    }

    /**
     * 获取是否可以循环
     *
     * @return
     */
    public boolean isLoop() {
        return isLoop;
    }

    /**
     * 设置是否循环
     *
     * @param loop
     */
    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    /**
     * 两种样式，默认第一种
     * 1、指示器左右移动，来选择条目
     * 2、指示器不动，条目左右移动
     */
    public enum Style {
        INDICATOR1, INDICATOR2
    }
}
