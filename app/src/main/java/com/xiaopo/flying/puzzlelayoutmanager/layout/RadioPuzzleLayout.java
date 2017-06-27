package com.xiaopo.flying.puzzlelayoutmanager.layout;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import com.xiaopo.flying.puzzlelayoutmanager.PuzzleLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * contains all lines and borders.
 * we can add line to divider a border to number of borders.
 * <p>
 * <p>
 * to determine the which border to layout puzzle piece.
 *
 * @author wupanjie
 * @see Block
 * <p>
 */
public abstract class RadioPuzzleLayout implements PuzzleLayout {
  protected static final String TAG = "RadioPuzzleLayout";

  private Block outerBlock;

  private List<Block> blocks = new ArrayList<>();
  private List<Line> lines = new ArrayList<>();
  private List<Line> outerLines = new ArrayList<>(4);

  private Comparator<Block> borderComparator = new BlockComparator();

  public RadioPuzzleLayout() {
    setOuterBounds(new Rect());
    layout();
  }

  public RadioPuzzleLayout(Rect baseRect) {
    setOuterBounds(baseRect);
    layout();
  }

  @Override public void setOuterBounds(Rect bounds) {
    Point one = new Point(bounds.left, bounds.top);
    Point two = new Point(bounds.right, bounds.top);
    Point three = new Point(bounds.left, bounds.bottom);
    Point four = new Point(bounds.right, bounds.bottom);

    StraightLine lineLeft = new StraightLine(one, three);
    StraightLine lineTop = new StraightLine(one, two);
    StraightLine lineRight = new StraightLine(two, four);
    StraightLine lineBottom = new StraightLine(three, four);

    outerLines.clear();

    outerLines.add(lineLeft);
    outerLines.add(lineTop);
    outerLines.add(lineRight);
    outerLines.add(lineBottom);

    outerBlock = new Block(bounds);

    blocks.clear();
    blocks.add(outerBlock);
  }

  public abstract void layout();

  @Override public int getAreaCount() {
    return blocks.size();
  }

  protected List<Block> addLine(int position, StraightLine.Direction direction, float ratio) {
    Block block = blocks.get(position);
    return addLine(block, direction, ratio);
  }

  protected List<Block> addLine(Block block, StraightLine.Direction direction, float ratio) {
    blocks.remove(block);
    StraightLine line = BlockUtil.createLine(block, direction, ratio);
    lines.add(line);

    List<Block> blockList = BlockUtil.cutBorder(block, line);
    blocks.addAll(blockList);

    updateLineLimit();
    Collections.sort(blocks, borderComparator);

    return blockList;
  }

  protected void cutBlockEqualPart(int position, int part, StraightLine.Direction direction) {
    Block temp = blocks.get(position);
    for (int i = part; i > 1; i--) {
      temp = addLine(temp, direction, (float) (i - 1) / i).get(0);
    }
  }

  protected List<Block> addCross(int position, float radio) {
    return addCross(position, radio, radio);
  }

  protected List<Block> addCross(int position, float horizontalRadio, float verticalRadio) {
    Block block = blocks.get(position);
    blocks.remove(block);
    StraightLine horizontal =
        BlockUtil.createLine(block, StraightLine.Direction.HORIZONTAL, horizontalRadio);
    StraightLine vertical =
        BlockUtil.createLine(block, StraightLine.Direction.VERTICAL, verticalRadio);
    lines.add(horizontal);
    lines.add(vertical);

    List<Block> blockList = BlockUtil.cutBorderCross(block, horizontal, vertical);
    blocks.addAll(blockList);

    updateLineLimit();

    if (borderComparator == null) {
      borderComparator = new BlockComparator();
    }
    Collections.sort(blocks, borderComparator);

    return blockList;
  }

  protected List<Block> cutBlockEqualPart(int position, int hSize, int vSize) {
    Block block = blocks.get(position);
    if ((hSize + 1) * (vSize + 1) > 9) {
      Log.e(TAG, "cutBorderEqualPart: the size can not be so great");
      return null;
    }
    blocks.remove(block);
    List<Block> blockList = new ArrayList<>();
    switch (hSize) {
      case 1:
        switch (vSize) {
          case 1:
            blockList.addAll(addCross(position, 1f / 2));
            break;
          case 2:
            StraightLine l1 = BlockUtil.createLine(block, StraightLine.Direction.VERTICAL, 1f / 3);
            StraightLine l2 = BlockUtil.createLine(block, StraightLine.Direction.VERTICAL, 2f / 3);
            StraightLine l3 =
                BlockUtil.createLine(block, StraightLine.Direction.HORIZONTAL, 1f / 2);

            lines.add(l1);
            lines.add(l2);
            lines.add(l3);

            blockList.addAll(
                BlockUtil.cutBorder(block, l1, l2, l3, StraightLine.Direction.VERTICAL));
            break;

          case 3:
            StraightLine ll1 = BlockUtil.createLine(block, StraightLine.Direction.VERTICAL, 1f / 4);
            StraightLine ll2 = BlockUtil.createLine(block, StraightLine.Direction.VERTICAL, 2f / 4);
            StraightLine ll3 = BlockUtil.createLine(block, StraightLine.Direction.VERTICAL, 3f / 4);
            StraightLine ll4 =
                BlockUtil.createLine(block, StraightLine.Direction.HORIZONTAL, 1f / 2);

            lines.add(ll1);
            lines.add(ll2);
            lines.add(ll3);
            lines.add(ll4);

            blockList.addAll(
                BlockUtil.cutBorder(block, ll1, ll2, ll3, ll4, StraightLine.Direction.VERTICAL));

            break;
        }
        break;

      case 2:
        switch (vSize) {
          case 1:
            StraightLine l1 =
                BlockUtil.createLine(block, StraightLine.Direction.HORIZONTAL, 1f / 3);
            StraightLine l2 =
                BlockUtil.createLine(block, StraightLine.Direction.HORIZONTAL, 2f / 3);
            StraightLine l3 = BlockUtil.createLine(block, StraightLine.Direction.VERTICAL, 1f / 2);

            lines.add(l1);
            lines.add(l2);
            lines.add(l3);

            blockList.addAll(
                BlockUtil.cutBorder(block, l1, l2, l3, StraightLine.Direction.HORIZONTAL));

            break;
          case 2:
            StraightLine ll1 =
                BlockUtil.createLine(block, StraightLine.Direction.HORIZONTAL, 1f / 3);
            StraightLine ll2 =
                BlockUtil.createLine(block, StraightLine.Direction.HORIZONTAL, 2f / 3);
            StraightLine ll3 = BlockUtil.createLine(block, StraightLine.Direction.VERTICAL, 1f / 3);
            StraightLine ll4 = BlockUtil.createLine(block, StraightLine.Direction.VERTICAL, 2f / 3);

            lines.add(ll1);
            lines.add(ll2);
            lines.add(ll3);
            lines.add(ll4);

            blockList.addAll(BlockUtil.cutBorder(block, ll1, ll2, ll3, ll4));
            break;
        }
        break;

      case 3:
        switch (vSize) {
          case 1:
            StraightLine ll1 =
                BlockUtil.createLine(block, StraightLine.Direction.HORIZONTAL, 1f / 4);
            StraightLine ll2 =
                BlockUtil.createLine(block, StraightLine.Direction.HORIZONTAL, 2f / 4);
            StraightLine ll3 =
                BlockUtil.createLine(block, StraightLine.Direction.HORIZONTAL, 3f / 4);
            StraightLine ll4 = BlockUtil.createLine(block, StraightLine.Direction.VERTICAL, 1f / 2);

            lines.add(ll1);
            lines.add(ll2);
            lines.add(ll3);
            lines.add(ll4);

            blockList.addAll(
                BlockUtil.cutBorder(block, ll1, ll2, ll3, ll4, StraightLine.Direction.HORIZONTAL));
            break;
        }
    }

    blocks.addAll(blockList);

    updateLineLimit();
    Collections.sort(blocks, borderComparator);

    return blockList;
  }

  protected List<Block> cutSpiral(int position) {
    Block block = blocks.get(position);
    blocks.remove(block);
    List<Block> blockList = new ArrayList<>();

    int width = block.width();
    int height = block.height();

    Point one = new Point(0, height / 3);
    Point two = new Point(width / 3 * 2, 0);
    Point three = new Point(width, height / 3 * 2);
    Point four = new Point(width / 3, height);
    Point five = new Point(width / 3, height / 3);
    Point six = new Point(width / 3 * 2, height / 3);
    Point seven = new Point(width / 3 * 2, height / 3 * 2);
    Point eight = new Point(width / 3, height / 3 * 2);

    StraightLine l1 = new StraightLine(one, six);
    StraightLine l2 = new StraightLine(two, seven);
    StraightLine l3 = new StraightLine(eight, three);
    StraightLine l4 = new StraightLine(five, four);

    l1.setAttachStartLine(block.lineLeft);
    l1.setAttachEndLine(l2);
    l1.setUpperLine(block.lineTop);
    l1.setLowerLine(l3);

    l2.setAttachStartLine(block.lineTop);
    l2.setAttachEndLine(l3);
    l2.setUpperLine(block.lineRight);
    l2.setLowerLine(l4);

    l3.setAttachStartLine(l4);
    l3.setAttachEndLine(block.lineRight);
    l3.setUpperLine(l1);
    l3.setLowerLine(block.lineBottom);

    l4.setAttachStartLine(l1);
    l4.setAttachEndLine(block.lineBottom);
    l4.setUpperLine(l2);
    l4.setLowerLine(block.lineLeft);

    lines.add(l1);
    lines.add(l2);
    lines.add(l3);
    lines.add(l4);

    Block b1 = new Block(block);
    b1.lineRight = l2;
    b1.lineBottom = l1;
    blockList.add(b1);

    Block b2 = new Block(block);
    b2.lineLeft = l2;
    b2.lineBottom = l3;
    blockList.add(b2);

    Block b3 = new Block(block);
    b3.lineRight = l4;
    b3.lineTop = l1;
    blockList.add(b3);

    Block b4 = new Block(block);
    b4.lineTop = l1;
    b4.lineRight = l2;
    b4.lineLeft = l4;
    b4.lineBottom = l3;
    blockList.add(b4);

    Block b5 = new Block(block);
    b5.lineLeft = l4;
    b5.lineTop = l3;
    blockList.add(b5);

    blocks.addAll(blockList);

    updateLineLimit();
    Collections.sort(blocks, borderComparator);

    return blockList;
  }

  private void updateLineLimit() {
    for (Line line : lines) {
      updateUpperLine(line);
      updateLowerLine(line);
    }
  }

  private void updateLowerLine(final Line line) {
    for (Line l : lines) {
      if (l == line) {
        continue;
      }

      if (l.direction() != line.direction()) {
        continue;
      }

      if (l.attachStartLine() != line.attachStartLine()
          || l.attachEndLine() != line.attachEndLine()) {
        continue;
      }

      if (l.direction() == Line.Direction.HORIZONTAL) {
        if (l.minY() > line.lowerLine().maxY() && l.maxY() < line.minY()) {
          line.setLowerLine(l);
        }
      } else {
        if (l.minX() > line.lowerLine().maxX() && l.maxX() < line.minX()) {
          line.setLowerLine(l);
        }
      }
    }
  }

  private void updateUpperLine(final Line line) {
    for (Line l : lines) {
      if (l == line) {
        continue;
      }

      if (l.direction() != line.direction()) {
        continue;
      }

      if (l.attachStartLine() != line.attachStartLine()
          || l.attachEndLine() != line.attachEndLine()) {
        continue;
      }

      if (l.direction() == Line.Direction.HORIZONTAL) {
        if (l.maxY() < line.upperLine().minY() && l.minY() > line.maxY()) {
          line.setUpperLine(l);
        }
      } else {
        if (l.maxX() < line.upperLine().minX() && l.minX() > line.maxX()) {
          line.setUpperLine(l);
        }
      }
    }
  }

  @Override public void reset() {
    lines.clear();
    blocks.clear();
    blocks.add(outerBlock);
  }

  @Override public Area getArea(int position) {
    return blocks.get(position);
  }

  @Override public float width() {
    return outerBlock.width();
  }

  @Override public float height() {
    return outerBlock.height();
  }

  @Override public List<Line> getLines() {
    return lines;
  }

  @Override public Area getOuterArea() {
    return outerBlock;
  }

  @Override public void update() {

  }

  @Override public List<Line> getOuterLines() {
    return outerLines;
  }

  private static class BlockComparator implements Comparator<Block> {
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
}
