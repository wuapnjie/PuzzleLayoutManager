package com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout;

import com.xiaopo.flying.puzzlelayoutmanager.layout.Line;
import com.xiaopo.flying.puzzlelayoutmanager.layout.PuzzleLayout;

/**
 * @author wupanjie
 */
public class ThirdPuzzleLayout extends PuzzleLayout {
  @Override public void layout() {
    addLine(0, Line.Direction.VERTICAL, 1f / 2);
    cutBlockEqualPart(1, 3, Line.Direction.HORIZONTAL);
  }
}
