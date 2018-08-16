package com.ppjun.android.vivonex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;

/**
 * 菜单按钮变为叉的Drawable
 * @author lollipop
 */
public class Menu2CloseDrawable extends Drawable {

    /**
     * 画笔
     */
    private Paint paint = new Paint();

    /**
     * 指定的宽度，如果没有，那么获取整个画板宽度
     */
    private float width = -1;
    /**
     * 指定高度，如果没有，那么获取整个画板高度
     */
    private float height = -1;

    /**
     * 线条宽度，如果没有，那么获取整体高度的1/5
     */
    private float barThickness = -1;

    /**
     * 绘制的边框
     */
    private Rect drawBounds = new Rect();

    /**
     * 绘制的线条宽度
     */
    private float strokeWidth = 0;

    /**
     * 绘制的路径
     */
    private Path drawPath = new Path();

    /**
     * 绘制的进度位置
     */
    private float drawPosition = 0F;

    /**
     * 是否同时旋转
     */
    private boolean isRotate = true;

    /**
     * 总的旋转角度
     */
    private int rotateAngle = 180;

    public Menu2CloseDrawable(){
        //设置边缘光滑
        paint.setAntiAlias(true);
        //设置抖动，用来做边缘光滑
        paint.setDither(true);
        //设置画笔模式为描边
        paint.setStyle(Paint.Style.STROKE);
    }

    public Menu2CloseDrawable setDP(Context context,float dp){
        float size = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics());
        setSize(size,size);
        return this;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        if(isRotate){
            //保存坐标系状态
            canvas.save();
            //以绘制范围的中心为圆心旋转指定角度
            canvas.rotate(drawPosition * rotateAngle,drawBounds.centerX(),drawBounds.centerY());
            //绘制图案
            canvas.drawPath(drawPath,paint);
            //还原坐标系状态
            canvas.restore();
        }else{
            canvas.drawPath(drawPath,paint);
        }

    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        //如果设置的高度为有效值，那么使用有效值并且居中
        if(width > 0){
            drawBounds.left = (int) (bounds.centerX() - (width / 2));
            drawBounds.right = (int) (drawBounds.left + width);
        }else{
            //否则使用容器尺寸
            drawBounds.left = bounds.left;
            drawBounds.right = bounds.right;
        }

        //同上
        if (height > 0){
            drawBounds.top = (int) (bounds.centerY() - (height / 2));
            drawBounds.bottom = (int) (drawBounds.top + height);
        }else{
            drawBounds.top = bounds.top;
            drawBounds.bottom = bounds.bottom;
        }

        if(barThickness > 0){
            strokeWidth = barThickness;
        }else{
            strokeWidth = drawBounds.height() / 5;
        }

        paint.setStrokeWidth(strokeWidth);

        onPositionChange();
    }

    /**
     * 设置需要的绘制大小
     * @param w 宽度的像素值
     * @param h 高度的像素值
     * @return 当前对象
     */
    public Menu2CloseDrawable setSize(float w,float h){
        width = w;
        height = h;
        //重新设置尺寸后，需要重新计算并且排版，重绘
        onBoundsChange(getBounds());
        return this;
    }

    /**
     * 设置图案的颜色
     * @param color 颜色值
     * @return 当前对象
     */
    public Menu2CloseDrawable setColor(int color){
        paint.setColor(color);
        invalidateSelf();
        return this;
    }

    /**
     * 进度变化时，更新图案并且发起重绘
     * @param p 进度值，区间为0-1
     * @return 当前对象
     */
    public Menu2CloseDrawable onPositionChange(@FloatRange(from = 0.0,to = 1.0) float p){
        if(p == drawPosition){
            //如果进度一样，那么不再计算图案，直接重绘
            invalidateSelf();
            return this;
        }
        drawPosition = p;
        onPositionChange();
        return this;
    }

    /**
     * 设置图案的路径形状
     */
    private void onPositionChange(){

        //重置路径，可以理解为清空图案
        drawPath.reset();

        //这个偏移量是上下2条线在变成X时，左右的偏移，否则会像是扁的
        float xOffset = (strokeWidth / 2) * drawPosition;

        //移动到图案的左上角，因为图案可能小于画板尺寸，因此此处使用计算得到的需要的尺寸。
        //高度之所以加上线条宽度的一半，是因为绘制的时候，
        // 画笔的有宽度，但是真实位置却是在线条中心，因此此处做相应偏移
        float leftX = drawBounds.left;
        float topY = drawBounds.top + (strokeWidth / 2);
        //计算左上角点与进度相关的偏移量，
        // （drawBounds.height() - strokeWidth）的含义是绘制高度（移动高度）减去线条宽度的占用，计算得到真实的移动距离
        // (* drawPosition)的含义是将总的移动距离和进度关联，根据进度来移动距离，以此来定位和产生动画
        float leftYOffset = (drawBounds.height() - strokeWidth) * drawPosition;
        //将起点移动至第一条线左端点
        drawPath.moveTo(leftX + xOffset,topY + leftYOffset);
        //连接至右上角，保持不变
        drawPath.lineTo(drawBounds.right - xOffset,topY);

        //中间线条的X轴起点以及终点
        float leftCenterX = drawBounds.left;
        float rightCenterX = drawBounds.right;
        //高度不变，因此直接取中点
        float centerY = drawBounds.centerY();
        //因为两侧同时收缩，因此两侧总的收缩长度为宽度一半
        float centerXOffset = drawBounds.width() / 2 * drawPosition;
        //设置第二条线，同时为X轴加上偏移量
        drawPath.moveTo(leftCenterX + centerXOffset,centerY);
        drawPath.lineTo(rightCenterX - centerXOffset,centerY);

        float bottomY = drawBounds.bottom - (strokeWidth / 2);
        //第三条线，左右与第一条相同，左侧高度偏移与第一条线相同，方向相反，因此可以直接用第一条线的计算结果
        //将起点移动至第三条线左端点
        drawPath.moveTo(leftX + xOffset,bottomY - leftYOffset);
        //连接至右下角，保持不变
        drawPath.lineTo(drawBounds.right - xOffset,bottomY);

        //形状确定后发起重绘
        invalidateSelf();
    }

    /**
     * 设置是否旋转，默认为true
     * @param r 是否旋转
     * @return 返回当前对象
     */
    public Menu2CloseDrawable setRotate(boolean r){
        isRotate = r;
        //记录状态后重绘刷新
        invalidateSelf();
        return this;
    }

    /**
     * 设置旋转的总得角度，默认为180
     * @param angle 角度值
     * @return 当前对象
     */
    public Menu2CloseDrawable setRotateAngle(int angle){
        rotateAngle = angle;
        //记录状态后重绘刷新
        invalidateSelf();
        return this;
    }

    /**
     * 设置线条宽度，默认为1/5
     * @param size 宽度像素值
     * @return 当前对象
     */
    public Menu2CloseDrawable setBarThickness(float size){
        barThickness = size;
        //记录后，需要重新计算并且排版，发起重绘
        onBoundsChange(getBounds());
        return this;
    }

    @Override
    public void setAlpha(int i) {
        paint.setAlpha(i);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
