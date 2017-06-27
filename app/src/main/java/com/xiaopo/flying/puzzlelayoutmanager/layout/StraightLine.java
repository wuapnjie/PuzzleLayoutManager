package com.xiaopo.flying.puzzlelayoutmanager.layout;

import android.graphics.Point;
import android.util.Log;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * the line to divide the rect border
 * @author wupanjie
 */
public class StraightLine implements Line {

  /**
   * for horizontal line, start means left, end means right
   * for vertical line, start means top, end means bottom
   */
  final Point start;
  final Point end;

  private Direction direction = Direction.HORIZONTAL;

  private Line attachLineStart;
  private Line attachLineEnd;

  private Line upperLine;
  private Line lowerLine;

  @Override public String toString() {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("The line is ")
        .append(direction.name())
        .append(",start point is ")
        .append(start)
        .append(",end point is ")
        .append(end)
        .append(",length is ")
        .append(length())
        .append("\n");

    if (attachLineStart != null) {
      stringBuilder.append("\n").append("attachLineStart is ").append(attachLineStart.toString());
    }

    if (attachLineEnd != null) {
      stringBuilder.append("\n").append("attachLineEnd is ").append(attachLineEnd.toString());
    }

    return stringBuilder.append("\n").toString();
  }

  public StraightLine(Point start, Point end) {
    this.start = start;
    this.end = end;

    if (start.x == end.x) {
      direction = Direction.VERTICAL;
    } else if (start.y == end.y) {
      direction = Direction.HORIZONTAL;
    } else {
      Log.d("StraightLine", "StraightLine: current only support two direction");
    }
  }

  public int length() {
    return (int) Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2));
  }

  @Override public Line upperLine() {
    return upperLine;
  }

  @Override public Line lowerLine() {
    return lowerLine;
  }

  @Override public Line attachStartLine() {
    return attachLineStart;
  }

  @Override public Line attachEndLine() {
    return attachLineEnd;
  }

  @Override public void setLowerLine(Line lowerLine) {
    this.lowerLine = lowerLine;
  }

  @Override public void setUpperLine(Line upperLine) {
    this.upperLine = upperLine;
  }

  @Override public Direction direction() {
    return direction;
  }

  public Point centerPoint() {
    return new Point((end.x - start.x) / 2, (end.y - start.y) / 2);
  }

  @Override public int position() {
    if (direction == Direction.HORIZONTAL) {
      return start.y;
    } else {
      return start.x;
    }
  }

  @Override public void setAttachStartLine(Line attachStartLine) {
    this.attachLineStart = attachStartLine;
  }

  @Override public void setAttachEndLine(Line attachEndLine) {
    this.attachLineEnd = attachEndLine;
  }

  @Override public int minX() {
    return min(start.x, end.x);
  }

  @Override public int maxX() {
    return max(start.x, end.x);
  }

  @Override public int minY() {
    return min(start.y, end.y);
  }

  @Override public int maxY() {
    return max(start.y, end.y);
  }

}
