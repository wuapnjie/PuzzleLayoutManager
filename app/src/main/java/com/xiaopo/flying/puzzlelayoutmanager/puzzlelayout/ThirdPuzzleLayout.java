package com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout;

import com.xiaopo.flying.puzzlelayoutmanager.layout.RadioPuzzleLayout;
import com.xiaopo.flying.puzzlelayoutmanager.layout.StraightLine;

/**
 * @author wupanjie
 */
public class ThirdPuzzleLayout extends RadioPuzzleLayout {
  @Override public void layout() {
    addLine(0, StraightLine.Direction.VERTICAL, 1f / 2);
    cutBlockEqualPart(1, 3, StraightLine.Direction.HORIZONTAL);
  }
}
