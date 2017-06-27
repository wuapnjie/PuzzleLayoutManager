package com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout;

import com.nightmare.library.RadioPuzzleLayout;

/**
 * @author wupanjie
 */
public class FirstPuzzleLayout extends RadioPuzzleLayout {
  public FirstPuzzleLayout(float radio) {
    super(radio);
  }

  @Override public void layout() {
    addCross(0,0.5f);
  }
}
