package com.xiaopo.flying.puzzlelayoutmanager.layout;

import android.graphics.Point;
import android.util.Log;

/**
 * the line to divide the rect border
 * @author wupanjie
 */
public class Line {

  public enum Direction {
    HORIZONTAL, VERTICAL;

    public static Direction get(int direction) {
      if (direction == 0) {
        return HORIZONTAL;
      } else {
        return VERTICAL;
      }
    }
  }

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

  public Line(Point start, Point end) {
    this.start = start;
    this.end = end;

    if (start.x == end.x) {
      direction = Direction.VERTICAL;
    } else if (start.y == end.y) {
      direction = Direction.HORIZONTAL;
    } else {
      Log.d("Line", "Line: current only support two direction");
    }
  }

  public int length() {
    return (int) Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2));
  }

  public Point centerPoint() {
    return new Point((end.x - start.x) / 2, (end.y - start.y) / 2);
  }

  public int getPosition() {
    if (direction == Direction.HORIZONTAL) {
      return start.y;
    } else {
      return start.x;
    }
  }


  public void update() {
    if (direction == Direction.HORIZONTAL) {
      if (attachLineStart != null) {
        start.x = attachLineStart.getPosition();
      }
      if (attachLineEnd != null) {
        end.x = attachLineEnd.getPosition();
      }
    } else if (direction == Direction.VERTICAL) {
      if (attachLineStart != null) {
        start.y = attachLineStart.getPosition();
      }
      if (attachLineEnd != null) {
        end.y = attachLineEnd.getPosition();
      }
    }
  }

  public void moveTo(int position, int extra) {
    if (direction == Direction.HORIZONTAL) {

      if (position < lowerLine.start.y + extra || position > upperLine.start.y - extra) return;

      start.y = position;
      end.y = position;
    } else if (direction == Direction.VERTICAL) {

      if (position < lowerLine.start.x + extra || position > upperLine.start.x - extra) return;

      start.x = position;
      end.x = position;
    }
  }

  public Line getAttachLineStart() {
    return attachLineStart;
  }

  public void setAttachLineStart(Line attachLineStart) {
    this.attachLineStart = attachLineStart;
  }

  public Line getAttachLineEnd() {
    return attachLineEnd;
  }

  public void setAttachLineEnd(Line attachLineEnd) {
    this.attachLineEnd = attachLineEnd;
  }

  public Direction getDirection() {
    return direction;
  }

  public Line getUpperLine() {
    return upperLine;
  }

  public void setUpperLine(Line upperLine) {
    this.upperLine = upperLine;
  }

  public Line getLowerLine() {
    return lowerLine;
  }

  public void setLowerLine(Line lowerLine) {
    this.lowerLine = lowerLine;
  }
}
