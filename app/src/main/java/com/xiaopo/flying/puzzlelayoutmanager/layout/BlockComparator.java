package com.xiaopo.flying.puzzlelayoutmanager.layout;

import java.util.Comparator;

/**
 * @author wupanjie
 */
public class BlockComparator implements Comparator<Block> {
  @Override public int compare(Block lhs, Block rhs) {
    if (lhs.getRect().top < rhs.getRect().top) {
      return -1;
    } else if (lhs.getRect().top == rhs.getRect().top) {
      if (lhs.getRect().left < rhs.left()) {
        return -1;
      } else {
        return 1;
      }
    } else {
      return 1;
    }
  }
}
