package com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout;

import com.xiaopo.flying.puzzlelayoutmanager.layout.RadioPuzzleLayout;

/**
 * @author wupanjie
 */
public class SecondPuzzleLayout extends RadioPuzzleLayout {
  @Override public void layout() {
    cutBlockEqualPart(0, 1, 2);
  }
}
