package com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout;

import com.nightmare.library.RadioPuzzleLayout;

/**
 * @author wupanjie
 */
public class SecondPuzzleLayout extends RadioPuzzleLayout {
  public SecondPuzzleLayout(float radio) {
    super(radio);
  }

  @Override public void layout() {
    cutBlockEqualPart(0, 1, 2);
  }
}
