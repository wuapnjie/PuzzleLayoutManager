package com.xiaopo.flying.puzzlelayoutmanager.layout;

import android.graphics.Point;

/**
 * @author wupanjie
 */
public interface Line {
  enum Direction {
    HORIZONTAL, VERTICAL;

    public static Direction get(int direction) {
      if (direction == 0) {
        return HORIZONTAL;
      } else {
        return VERTICAL;
      }
    }
  }

  int length();

  Line upperLine();

  Line lowerLine();

  Line attachStartLine();

  Line attachEndLine();

  void setLowerLine(Line lowerLine);

  void setUpperLine(Line upperLine);

  void setAttachStartLine(Line attachStartLine);

  void setAttachEndLine(Line attachEndLine);

  Direction direction();

  Point centerPoint();

  int position();

  int minX();

  int maxX();

  int minY();

  int maxY();
}
