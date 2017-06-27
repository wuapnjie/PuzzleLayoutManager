package com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout;

import com.nightmare.library.Line;
import com.nightmare.library.RadioPuzzleLayout;

/**
 * @author wupanjie
 */
public class ThirdPuzzleLayout extends RadioPuzzleLayout {
  @Override public void layout() {
    addLine(0, Line.Direction.VERTICAL, 1f / 2);
    cutBlockEqualPart(1, 3, Line.Direction.HORIZONTAL);
  }
}
