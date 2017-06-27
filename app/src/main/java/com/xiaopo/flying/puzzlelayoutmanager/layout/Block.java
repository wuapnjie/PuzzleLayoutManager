package com.xiaopo.flying.puzzlelayoutmanager.layout;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import java.util.Arrays;
import java.util.List;

/**
 * the block to layout puzzle piece
 *
 * each block consist of four lines : left,top,right,bottom
 *
 * @author wupanjie
 * @see StraightLine
 * <p>
 */
public class Block implements Area {
  StraightLine lineLeft;
  StraightLine lineTop;
  StraightLine lineRight;
  StraightLine lineBottom;

  private int paddingLeft;
  private int paddingTop;
  private int paddingRight;
  private int paddingBottom;

  public Block(Block src) {
    lineLeft = src.lineLeft;
    lineTop = src.lineTop;
    lineRight = src.lineRight;
    lineBottom = src.lineBottom;
  }

  public Block(Rect baseRect) {
    setBaseRect(baseRect);
  }

  private void setBaseRect(Rect baseRect) {
    Point one = new Point(baseRect.left, baseRect.top);
    Point two = new Point(baseRect.right, baseRect.top);
    Point three = new Point(baseRect.left, baseRect.bottom);
    Point four = new Point(baseRect.right, baseRect.bottom);

    lineLeft = new StraightLine(one, three);
    lineTop = new StraightLine(one, two);
    lineRight = new StraightLine(two, four);
    lineBottom = new StraightLine(three, four);
  }

  @Override public int width() {
    return right() - left();
  }

  @Override public int height() {
    return bottom() - top();
  }

  @Override public int left() {
    return lineLeft.start.x + paddingLeft;
  }

  @Override public int top() {
    return lineTop.start.y + paddingTop;
  }

  @Override public int right() {
    return lineRight.start.x - paddingRight;
  }

  @Override public int bottom() {
    return lineBottom.start.y - paddingTop;
  }

  public int centerX() {
    return (right() + left()) / 2;
  }

  public int centerY() {
    return (bottom() + top()) / 2;
  }

  public int getPaddingLeft() {
    return paddingLeft;
  }

  public int getPaddingTop() {
    return paddingTop;
  }

  public int getPaddingRight() {
    return paddingRight;
  }

  public int getPaddingBottom() {
    return paddingBottom;
  }

  public void setPadding(int padding) {
    setPadding(padding, padding, padding, padding);
  }

  public void setPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
    this.paddingLeft = paddingLeft;
    this.paddingTop = paddingTop;
    this.paddingRight = paddingRight;
    this.paddingBottom = paddingBottom;

    this.paddingLeft = paddingLeft;
    this.paddingTop = paddingTop;
    this.paddingRight = paddingRight;
    this.paddingBottom = paddingBottom;
  }

  public List<StraightLine> getLines() {
    return Arrays.asList(lineLeft, lineTop, lineRight, lineBottom);
  }

  public RectF getRect() {
    return new RectF(left(), top(), right(), bottom());
  }

  public boolean contains(StraightLine line) {
    return lineLeft == line || lineTop == line || lineRight == line || lineBottom == line;
  }

  @Override public String toString() {
    return "left line:\n"
        + lineLeft.toString()
        + "\ntop line:\n"
        + lineTop.toString()
        + "\nright line:\n"
        + lineRight.toString()
        + "\nbottom line:\n"
        + lineBottom.toString()
        + "\nthe rect is \n"
        + getRect().toString();  //TODO
  }
}
