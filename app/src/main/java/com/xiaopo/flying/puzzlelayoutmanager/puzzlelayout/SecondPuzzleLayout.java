package com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout;

import com.xiaopo.flying.puzzlelayoutmanager.layout.PuzzleLayout;

/**
 * @author wupanjie
 */
public class SecondPuzzleLayout extends PuzzleLayout {
  @Override public void layout() {
    cutBlockEqualPart(0, 1, 2);
  }
}
