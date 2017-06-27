package com.nightmare.library;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import java.util.List;

/**
 * @author wupanjie
 */

public interface PuzzleLayout extends Area {
  void setOuterBounds(@NonNull Rect bounds);

  void layout();

  int getAreaCount();

  List<Line> getOuterLines();

  List<Line> getLines();

  Area getOuterArea();

  void update();

  void reset();

  Area getArea(int position);
}
