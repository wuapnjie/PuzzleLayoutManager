package com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout;

import com.nightmare.library.RadioPuzzleLayout;

/**
 * @author wupanjie
 */
public class ForthPuzzleLayout extends RadioPuzzleLayout {
  public ForthPuzzleLayout(float radio) {
    super(radio);
  }

  @Override public void layout() {
    cutSpiral(0);
  }
}
