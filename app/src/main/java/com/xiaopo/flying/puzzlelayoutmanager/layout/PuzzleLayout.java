package com.xiaopo.flying.puzzlelayoutmanager.layout;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
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
public abstract class PuzzleLayout {
  protected static final String TAG = "PuzzleLayout";

  private Block outerBlock;

  private List<Block> blocks = new ArrayList<>();
  private List<Line> lines = new ArrayList<>();
  private List<Line> outerLines = new ArrayList<>(4);

  private Comparator<Block> borderComparator = new BlockComparator();

  public PuzzleLayout() {
    setOuterBlock(new Rect());
    layout();
  }

  public PuzzleLayout(Rect baseRect) {
    setOuterBlock(baseRect);
    layout();
  }

  public void setOuterBlock(Rect baseRect) {
    Point one = new Point(baseRect.left, baseRect.top);
    Point two = new Point(baseRect.right, baseRect.top);
    Point three = new Point(baseRect.left, baseRect.bottom);
    Point four = new Point(baseRect.right, baseRect.bottom);

    Line lineLeft = new Line(one, three);
    Line lineTop = new Line(one, two);
    Line lineRight = new Line(two, four);
    Line lineBottom = new Line(three, four);

    outerLines.clear();

    outerLines.add(lineLeft);
    outerLines.add(lineTop);
    outerLines.add(lineRight);
    outerLines.add(lineBottom);

    outerBlock = new Block(baseRect);

    blocks.clear();
    blocks.add(outerBlock);
  }

  public abstract void layout();

  public int getHeight() {
    return outerBlock.height();
  }

  public int getWidth() {
    return outerBlock.width();
  }

  protected List<Block> addLine(int position, Line.Direction direction, float ratio) {
    Block block = blocks.get(position);
    return addLine(block, direction, ratio);
  }

  protected List<Block> addLine(Block block, Line.Direction direction, float ratio) {
    blocks.remove(block);
    Line line = BlockUtil.createLine(block, direction, ratio);
    lines.add(line);

    List<Block> blockList = BlockUtil.cutBorder(block, line);
    blocks.addAll(blockList);

    updateLineLimit();
    Collections.sort(blocks, borderComparator);

    return blockList;
  }

  protected void cutBlockEqualPart(int position, int part, Line.Direction direction) {
    Block temp = getBlock(position);
    for (int i = part; i > 1; i--) {
      temp = addLine(temp, direction, (float) (i - 1) / i).get(0);
    }
  }

  protected List<Block> addCross(int position, float radio) {
    return addCross(position, radio, radio);
  }

  protected List<Block> addCross(int position, float horizontalRadio, float verticalRadio) {
    Block block = getBlock(position);
    blocks.remove(block);
    Line horizontal = BlockUtil.createLine(block, Line.Direction.HORIZONTAL, horizontalRadio);
    Line vertical = BlockUtil.createLine(block, Line.Direction.VERTICAL, verticalRadio);
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
    Block block = getBlock(position);
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
            Line l1 = BlockUtil.createLine(block, Line.Direction.VERTICAL, 1f / 3);
            Line l2 = BlockUtil.createLine(block, Line.Direction.VERTICAL, 2f / 3);
            Line l3 = BlockUtil.createLine(block, Line.Direction.HORIZONTAL, 1f / 2);

            lines.add(l1);
            lines.add(l2);
            lines.add(l3);

            blockList.addAll(BlockUtil.cutBorder(block, l1, l2, l3, Line.Direction.VERTICAL));
            break;

          case 3:
            Line ll1 = BlockUtil.createLine(block, Line.Direction.VERTICAL, 1f / 4);
            Line ll2 = BlockUtil.createLine(block, Line.Direction.VERTICAL, 2f / 4);
            Line ll3 = BlockUtil.createLine(block, Line.Direction.VERTICAL, 3f / 4);
            Line ll4 = BlockUtil.createLine(block, Line.Direction.HORIZONTAL, 1f / 2);

            lines.add(ll1);
            lines.add(ll2);
            lines.add(ll3);
            lines.add(ll4);

            blockList.addAll(
                BlockUtil.cutBorder(block, ll1, ll2, ll3, ll4, Line.Direction.VERTICAL));

            break;
        }
        break;

      case 2:
        switch (vSize) {
          case 1:
            Line l1 = BlockUtil.createLine(block, Line.Direction.HORIZONTAL, 1f / 3);
            Line l2 = BlockUtil.createLine(block, Line.Direction.HORIZONTAL, 2f / 3);
            Line l3 = BlockUtil.createLine(block, Line.Direction.VERTICAL, 1f / 2);

            lines.add(l1);
            lines.add(l2);
            lines.add(l3);

            blockList.addAll(BlockUtil.cutBorder(block, l1, l2, l3, Line.Direction.HORIZONTAL));

            break;
          case 2:
            Line ll1 = BlockUtil.createLine(block, Line.Direction.HORIZONTAL, 1f / 3);
            Line ll2 = BlockUtil.createLine(block, Line.Direction.HORIZONTAL, 2f / 3);
            Line ll3 = BlockUtil.createLine(block, Line.Direction.VERTICAL, 1f / 3);
            Line ll4 = BlockUtil.createLine(block, Line.Direction.VERTICAL, 2f / 3);

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
            Line ll1 = BlockUtil.createLine(block, Line.Direction.HORIZONTAL, 1f / 4);
            Line ll2 = BlockUtil.createLine(block, Line.Direction.HORIZONTAL, 2f / 4);
            Line ll3 = BlockUtil.createLine(block, Line.Direction.HORIZONTAL, 3f / 4);
            Line ll4 = BlockUtil.createLine(block, Line.Direction.VERTICAL, 1f / 2);

            lines.add(ll1);
            lines.add(ll2);
            lines.add(ll3);
            lines.add(ll4);

            blockList.addAll(
                BlockUtil.cutBorder(block, ll1, ll2, ll3, ll4, Line.Direction.HORIZONTAL));
            break;
        }
    }

    blocks.addAll(blockList);

    updateLineLimit();
    Collections.sort(blocks, borderComparator);

    return blockList;
  }

  protected List<Block> cutSpiral(int position) {
    Block block = getBlock(position);
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

    Line l1 = new Line(one, six);
    Line l2 = new Line(two, seven);
    Line l3 = new Line(eight, three);
    Line l4 = new Line(five, four);

    l1.setAttachLineStart(block.lineLeft);
    l1.setAttachLineEnd(l2);
    l1.setUpperLine(block.lineTop);
    l1.setLowerLine(l3);

    l2.setAttachLineStart(block.lineTop);
    l2.setAttachLineEnd(l3);
    l2.setUpperLine(block.lineRight);
    l2.setLowerLine(l4);

    l3.setAttachLineStart(l4);
    l3.setAttachLineEnd(block.lineRight);
    l3.setUpperLine(l1);
    l3.setLowerLine(block.lineBottom);

    l4.setAttachLineStart(l1);
    l4.setAttachLineEnd(block.lineBottom);
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
      if (l.getPosition() > line.getLowerLine().getPosition()
          && l.getPosition() < line.getPosition()
          && l.getDirection() == line.getDirection()) {

        if (l.getDirection() == Line.Direction.HORIZONTAL && (l.end.x <= line.start.x
            || l.start.x >= line.end.x)) {
          continue;
        }

        if (l.getDirection() == Line.Direction.VERTICAL && (l.end.y <= line.start.y
            || l.start.y >= line.end.y)) {
          continue;
        }

        line.setLowerLine(l);
      }
    }
  }

  private void updateUpperLine(final Line line) {
    for (Line l : lines) {
      if (l.getPosition() < line.getUpperLine().getPosition()
          && l.getPosition() > line.getPosition()
          && l.getDirection() == line.getDirection()) {

        if (l.getDirection() == Line.Direction.HORIZONTAL && (l.end.x <= line.start.x
            || l.start.x >= line.end.x)) {
          continue;
        }

        if (l.getDirection() == Line.Direction.VERTICAL && (l.end.y <= line.start.y
            || l.start.y >= line.end.y)) {
          continue;
        }

        line.setUpperLine(l);
      }
    }
  }

  public void reset() {
    lines.clear();
    blocks.clear();
    blocks.add(outerBlock);
  }

  public void update() {
    for (Line line : lines) {
      line.update();
    }
  }

  public int getBlockSize() {
    return blocks.size();
  }

  public Block getBlock(int index) {
    return blocks.get(index);
  }

  public List<Line> getLines() {
    return lines;
  }

  public List<Block> getBlocks() {
    return blocks;
  }

  public Block getOuterBlock() {
    return outerBlock;
  }

  public List<Line> getOuterLines() {
    return outerLines;
  }
}
