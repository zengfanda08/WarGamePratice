package com.ltb.laer.waterview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

public class RandomLayout<T> extends RelativeLayout {

    public static final int ORIENTATION_LEFT = 1;
    public static final int ORIENTATION_RIGHT = 2;
    /**
     * 此列表用于保存随机的View视图
     * 在添加随机View的时候应当判断此视图是否有覆盖的
     * 有的话应该重新进行随机!
     */
    public ArrayList<View> randomViewList = new ArrayList<>();
    private OnRandomItemClickListener<T> onRandomItemClickListener;
    private OnRandomItemLongClickListener<T> onRandomItemLongClickListener;
    private boolean itemClickable = true;
    private boolean itemLongClickable = true;
    private Context context;
    private int orientation = ORIENTATION_LEFT;

    public RandomLayout(Context context) {
        super(context);
    }

    public RandomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setLayoutOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     * 添加到一个随机的XY位置，且不重复。
     */
    public void addViewAtRandomXY(final View view, final T t) {
        if (view == null) return;
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        post(new Runnable() {
            @Override
            public void run() {
                randomViewList.remove(view);
                // 100次随机上限
                for (int i = 0; i < 100; i++) {
                    int[] xy = createXY(
                            (int) Utils.dpToPixel(70),
                            (int) Utils.dpToPixel(70)
                    );

                    int layoutHeight = getMeasuredHeight();
                    int layoutWidth = getMeasuredWidth();

                    if (orientation == ORIENTATION_LEFT) {
                        if (xy[1] >= layoutHeight - Utils.dpToPixel(140) && xy[0] <= Utils.dpToPixel(140)) {
                            break;
                        }
                    } else  {
                        if (xy[0] >= layoutWidth - Utils.dpToPixel(140) && xy[1] <= Utils.dpToPixel(140)) {
                            break;
                        }
                    }

                    if (randomViewList.size() == 0) {
                        addViewAndSetXY(view, xy[0], xy[1], t);
                    } else {
                        boolean isRepeat = false;
                        // 迭代已经存在的View，判断是否重叠!
                        for (View subView : randomViewList) {
                            // 得到XY
                            int x = (int) subView.getX();
                            int y = (int) subView.getY();
                            int width = (int) Utils.dpToPixel(70);
                            int height = (int) Utils.dpToPixel(70);
                            // 创建矩形
                            Rect v1Rect = new Rect(x, y, width + x, height + y);
                            Rect v2Rect = new Rect(xy[0], xy[1], (int) Utils.dpToPixel(70) + xy[0], (int) Utils.dpToPixel(70) + xy[1]
                            );
                            if (Rect.intersects(v1Rect, v2Rect)) {
                                isRepeat = true;
                                break;
                            }
                        }
                        if (!isRepeat) {
                            addViewAndSetXY(view, xy[0], xy[1], t);
                            return;
                        }
                    }
                }
            }
        });
    }

    public void addViewAndSetXY(View view, int x, int y, final T t) {

        removeView(view);
        addView(view);
        randomViewList.add(view);
        view.setX(x);
        view.setY(y);
        // 设置单击事件!
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRandomItemClickListener != null && isItemClickable()) {
                    float x = v.getX();
                    float y = v.getY();
                    onRandomItemClickListener.onRandomItemClick(v, t);
                }
            }
        });
        // 设置长按事件!
        view.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onRandomItemLongClickListener != null && itemLongClickable)
                    return onRandomItemLongClickListener.onRandomItemLongClick(v, t);
                return false;
            }
        });
    }


    /**
     * 添加一个View到随机列表中，以此达到防止覆盖的效果!
     */
    public void addViewToRandomList(View view) {
        randomViewList.add(view);
    }

    /**
     * 清除所有的随机视图!
     */
    public void removeAllRandomView() {
        for (View v : randomViewList) {
            removeView(v);
        }
        randomViewList.clear();
    }

    /**
     * 从列表中移除一个随机视图!
     */
    public void removeRandomViewFromList(View view) {
        randomViewList.remove(view);
    }

    /**
     * 随机生成一个 0 到指定区间的值!
     *
     * @param max 0到max但是不包括max
     * @return 同上
     */
    private int randomWidth(int max, int width) {
        // LogUtils.d("Max是：" + max);

        if (orientation == ORIENTATION_RIGHT) {
            return new Random().nextInt(max - width / 2) + width / 2;
        } else {
            return new Random().nextInt(max);
        }
    }

    private int randomHight(int max) {
        // LogUtils.d("Max是：" + max);
        return new Random().nextInt(max);
    }


    /**
     * 根据传入的宽和高返回一个随机的坐标!
     */
    @SuppressLint("NewApi")
    private int[] createXY(int height, int width) {
        int[] xyRet = new int[]{0, 0};
        // 初始化我们当前布局的屏幕XY!
        int layoutHeight = getMeasuredHeight();
        int layoutWidth = getMeasuredWidth();
        int rowX = (int) getX();
        // 先随机一个X，注意一下就是，X轴是从View的左向右延申的
        // 注意，要减去内部填充!!!
        // LogUtils.d("paddingEnd: " + paddingEnd);

        if (orientation == ORIENTATION_RIGHT) {
            xyRet[0] = randomWidth(
                    layoutWidth - (width + getPaddingStart() + getPaddingEnd()
                    ), layoutWidth
            );
        } else {
            xyRet[0] = randomWidth(
                    layoutWidth - (
                            width + getPaddingStart() + getPaddingEnd()
                    ), layoutWidth
            );
        }
        // LogUtils.d(" 布局宽度：" + layoutWidth + "，X轴：" + xyRet[0] + "，最终宽度：" + (xyRet[0] + width + paddingEnd + paddingStart));
        // 然后从Y是从View的上向下延申，所以我们需要进行下限值限制，避免越界!
        xyRet[1] = randomHight(
                layoutHeight - (
                        height + getPaddingBottom() + getPaddingTop()
                )
        );
        return xyRet;
    }

    public boolean isItemClickable() {
        return itemClickable;
    }

    public void setItemClickable(boolean itemClickable) {
        this.itemClickable = itemClickable;
    }

    public boolean isItemLongClickable() {
        return itemLongClickable;
    }

    public void setItemLongClickable(boolean itemLongClickable) {
        this.itemLongClickable = itemLongClickable;
    }

    public OnRandomItemClickListener getOnRandomItemClickListener() {
        return onRandomItemClickListener;
    }

    public void setOnRandomItemClickListener(OnRandomItemClickListener<T> onRandomItemClickListener) {
        this.onRandomItemClickListener = onRandomItemClickListener;
    }

    public OnRandomItemLongClickListener<T> getOnRandomItemLongClickListener() {
        return onRandomItemLongClickListener;
    }

    public void setOnRandomItemLongClickListener(OnRandomItemLongClickListener<T> onRandomItemLongClickListener) {
        this.onRandomItemLongClickListener = onRandomItemLongClickListener;
    }

    public interface OnRandomItemClickListener<T> {
        void onRandomItemClick(View view, T t);
    }

    public interface OnRandomItemLongClickListener<T> {
        boolean onRandomItemLongClick(View view, T t);
    }
}
