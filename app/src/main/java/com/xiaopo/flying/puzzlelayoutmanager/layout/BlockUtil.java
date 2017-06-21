package com.xiaopo.flying.puzzlelayoutmanager.layout;

import android.graphics.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wupanjie
 */
public class BlockUtil {
  private static final String TAG = "BlockUtil";

  public static Line createLine(final Block block, final Line.Direction direction, final float ratio) {
    Point one = new Point();
    Point two = new Point();
    if (direction == Line.Direction.HORIZONTAL) {
      one.x = block.left();
      one.y = (int) (block.height() * ratio + block.top());
      two.x = block.right();
      two.y = (int) (block.height() * ratio + block.top());
    } else if (direction == Line.Direction.VERTICAL) {
      one.x = (int) (block.width() * ratio + block.left());
      one.y = block.top();
      two.x = (int) (block.width() * ratio + block.left());
      two.y = block.bottom();
    }

    Line line = new Line(one, two);

    if (direction == Line.Direction.HORIZONTAL) {
      line.setAttachLineStart(block.lineLeft);
      line.setAttachLineEnd(block.lineRight);

      line.setUpperLine(block.lineBottom);
      line.setLowerLine(block.lineTop);
    } else if (direction == Line.Direction.VERTICAL) {
      line.setAttachLineStart(block.lineTop);
      line.setAttachLineEnd(block.lineBottom);

      line.setUpperLine(block.lineRight);
      line.setLowerLine(block.lineLeft);
    }

    return line;
  }

  public static List<Block> cutBorder(final Block block, final Line line) {
    List<Block> list = new ArrayList<>();
    if (line.getDirection() == Line.Direction.HORIZONTAL) {
      Block one = new Block(block);
      one.lineBottom = line;
      list.add(one);

      Block two = new Block(block);
      two.lineTop = line;
      list.add(two);
    } else if (line.getDirection() == Line.Direction.VERTICAL) {
      Block one = new Block(block);
      one.lineRight = line;
      list.add(one);

      Block two = new Block(block);
      two.lineLeft = line;
      list.add(two);
    }

    return list;
  }

  public static List<Block> cutBorder(final Block block, final Line l1, final Line l2, final Line l3,
      Line.Direction direction) {
    List<Block> list = new ArrayList<>();
    if (direction == Line.Direction.HORIZONTAL) {
      Block one = new Block(block);
      one.lineRight = l3;
      one.lineBottom = l1;
      list.add(one);

      Block two = new Block(block);
      two.lineLeft = l3;
      two.lineBottom = l1;
      list.add(two);

      Block three = new Block(block);
      three.lineRight = l3;
      three.lineTop = l1;
      three.lineBottom = l2;
      list.add(three);

      Block four = new Block(block);
      four.lineLeft = l3;
      four.lineTop = l1;
      four.lineBottom = l2;
      list.add(four);

      Block five = new Block(block);
      five.lineRight = l3;
      five.lineTop = l2;
      list.add(five);

      Block six = new Block(block);
      six.lineLeft = l3;
      six.lineTop = l2;
      list.add(six);
    } else if (direction == Line.Direction.VERTICAL) {

      Block one = new Block(block);
      one.lineRight = l1;
      one.lineBottom = l3;
      list.add(one);

      Block two = new Block(block);
      two.lineLeft = l1;
      two.lineBottom = l3;
      two.lineRight = l2;
      list.add(two);

      Block three = new Block(block);
      three.lineLeft = l2;
      three.lineBottom = l3;
      list.add(three);

      Block four = new Block(block);
      four.lineRight = l1;
      four.lineTop = l3;
      list.add(four);

      Block five = new Block(block);
      five.lineLeft = l1;
      five.lineRight = l2;
      five.lineTop = l3;
      list.add(five);

      Block six = new Block(block);
      six.lineLeft = l2;
      six.lineTop = l3;
      list.add(six);
    }

    return list;
  }

  public static List<Block> cutBorder(final Block block, final Line l1, final Line l2, final Line l3,
      final Line l4, Line.Direction direction) {
    List<Block> list = new ArrayList<>();
    if (direction == Line.Direction.HORIZONTAL) {

      Block one = new Block(block);
      one.lineRight = l4;
      one.lineBottom = l1;
      list.add(one);

      Block two = new Block(block);
      two.lineLeft = l4;
      two.lineBottom = l1;
      list.add(two);

      Block three = new Block(block);
      three.lineRight = l4;
      three.lineTop = l1;
      three.lineBottom = l2;
      list.add(three);

      Block four = new Block(block);
      four.lineLeft = l4;
      four.lineTop = l1;
      four.lineBottom = l2;
      list.add(four);

      Block five = new Block(block);
      five.lineRight = l4;
      five.lineTop = l2;
      five.lineBottom = l3;
      list.add(five);

      Block six = new Block(block);
      six.lineLeft = l4;
      six.lineTop = l2;
      six.lineBottom = l3;
      list.add(six);

      Block seven = new Block(block);
      seven.lineRight = l4;
      seven.lineTop = l3;
      list.add(seven);

      Block eight = new Block(block);
      eight.lineLeft = l4;
      eight.lineTop = l3;
      list.add(eight);
    } else if (direction == Line.Direction.VERTICAL) {

      Block one = new Block(block);
      one.lineRight = l1;
      one.lineBottom = l4;
      list.add(one);

      Block two = new Block(block);
      two.lineLeft = l1;
      two.lineBottom = l4;
      two.lineRight = l2;
      list.add(two);

      Block three = new Block(block);
      three.lineLeft = l2;
      three.lineRight = l3;
      three.lineBottom = l4;
      list.add(three);

      Block four = new Block(block);
      four.lineLeft = l3;
      four.lineBottom = l4;
      list.add(four);

      Block five = new Block(block);
      five.lineRight = l1;
      five.lineTop = l4;
      list.add(five);

      Block six = new Block(block);
      six.lineLeft = l1;
      six.lineRight = l2;
      six.lineTop = l4;
      list.add(six);

      Block seven = new Block(block);
      seven.lineLeft = l2;
      seven.lineRight = l3;
      seven.lineTop = l4;
      list.add(seven);

      Block eight = new Block(block);
      eight.lineLeft = l3;
      eight.lineTop = l4;
      list.add(eight);
    }

    return list;
  }

  public static List<Block> cutBorder(final Block block, final Line l1, final Line l2, final Line l3,
      final Line l4) {
    List<Block> list = new ArrayList<>();

    Block one = new Block(block);
    one.lineRight = l3;
    one.lineBottom = l1;
    list.add(one);

    Block two = new Block(block);
    two.lineLeft = l3;
    two.lineRight = l4;
    two.lineBottom = l1;
    list.add(two);

    Block three = new Block(block);
    three.lineLeft = l4;
    three.lineBottom = l1;
    list.add(three);

    Block four = new Block(block);
    four.lineRight = l3;
    four.lineTop = l1;
    four.lineBottom = l2;
    list.add(four);

    Block five = new Block(block);
    five.lineRight = l4;
    five.lineLeft = l3;
    five.lineTop = l1;
    five.lineBottom = l2;
    list.add(five);

    Block six = new Block(block);
    six.lineLeft = l4;
    six.lineTop = l1;
    six.lineBottom = l2;
    list.add(six);

    Block seven = new Block(block);
    seven.lineRight = l3;
    seven.lineTop = l2;
    list.add(seven);

    Block eight = new Block(block);
    eight.lineRight = l4;
    eight.lineLeft = l3;
    eight.lineTop = l2;
    list.add(eight);

    Block nine = new Block(block);
    nine.lineLeft = l4;
    nine.lineTop = l2;
    list.add(nine);

    return list;
  }

  public static List<Block> cutBorderCross(final Block block, final Line horizontal,
      final Line vertical) {
    List<Block> list = new ArrayList<>();

    Block one = new Block(block);
    one.lineBottom = horizontal;
    one.lineRight = vertical;
    list.add(one);

    Block two = new Block(block);
    two.lineBottom = horizontal;
    two.lineLeft = vertical;
    list.add(two);

    Block three = new Block(block);
    three.lineTop = horizontal;
    three.lineRight = vertical;
    list.add(three);

    Block four = new Block(block);
    four.lineTop = horizontal;
    four.lineLeft = vertical;
    list.add(four);

    return list;
  }
}
