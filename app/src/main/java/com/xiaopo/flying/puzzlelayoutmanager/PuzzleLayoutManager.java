package com.xiaopo.flying.puzzlelayoutmanager;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.xiaopo.flying.puzzlelayoutmanager.layout.Block;
import com.xiaopo.flying.puzzlelayoutmanager.layout.PuzzleLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wupanjie
 */
public class PuzzleLayoutManager extends RecyclerView.LayoutManager {
  private static final String TAG = "PuzzleLayoutManager";

  private int verticalScrollOffset;
  private int totalHeight;
  private int totalPuzzleSize = 0;
  private List<PuzzleLayout> puzzleLayouts = new ArrayList<>();
  private Map<Range, PuzzleLayout> rangePuzzleLayoutMap = new HashMap<>();
  private Rect tempRect = new Rect();

  @Override public RecyclerView.LayoutParams generateDefaultLayoutParams() {
    return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
  }

  @Override public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    if (getItemCount() == 0) {//没有Item，界面空着吧
      detachAndScrapAttachedViews(recycler);
      return;
    }
    if (getChildCount() == 0 && state.isPreLayout()) {//state.isPreLayout()是支持动画的
      return;
    }
    //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
    detachAndScrapAttachedViews(recycler);

    fill(recycler, state);
  }

  private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
    //定义竖直方向的偏移量
    //int offsetY = 0;
    totalHeight = 0;
    layoutPuzzle();
    for (int i = 0; i < getItemCount(); i++) {
      Range range = getContainsRange(i);
      PuzzleLayout puzzleLayout = getPuzzleLayout(i);

      if (range == null || puzzleLayout == null) continue;

      int positionInPuzzle = i - range.start;
      Block block = puzzleLayout.getBlock(positionInPuzzle);

      //这里就是从缓存里面取出
      View view = recycler.getViewForPosition(i);
      //将View加入到RecyclerView中
      addView(view);
      // TODO
      view.measure(View.MeasureSpec.makeMeasureSpec(block.width(), View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(block.height(), View.MeasureSpec.EXACTLY));
      //measureChildWithMargins(view, 0, 0);
      //int width = getDecoratedMeasuredWidth(view);
      //int height = getDecoratedMeasuredHeight(view);
      //最后，将View布局
      //layoutDecorated(view, 0, offsetY, width, offsetY + height);
      layoutDecorated(view, block.left(), block.top(), block.right(), block.bottom());
      Log.d(TAG, "fill: block --> "
          + "left : "
          + block.left()
          + ",top : "
          + block.top()
          + ",right : "
          + block.right()
          + ",top : "
          + block.top());
      //将竖直方向偏移量增大height
      //offsetY += height;
      //
      //totalHeight += height;
    }

    totalHeight = calculateTotalLength();
    //如果所有子View的高度和没有填满RecyclerView的高度，
    // 则将高度设置为RecyclerView的高度
    totalHeight = Math.max(totalHeight, getVerticalSpace());
  }

  @Override
  public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
    //实际要滑动的距离
    int travel = dy;

    //如果滑动到最顶部
    if (verticalScrollOffset + dy < 0) {
      travel = -verticalScrollOffset;
    } else if (verticalScrollOffset + dy > totalHeight - getVerticalSpace()) {//如果滑动到最底部
      travel = totalHeight - getVerticalSpace() - verticalScrollOffset;
    }

    //将竖直方向的偏移量+travel
    verticalScrollOffset += travel;

    // 平移容器内的item
    offsetChildrenVertical(-travel);

    return travel;
  }

  private int getVerticalSpace() {
    return getHeight() - getPaddingBottom() - getPaddingTop();
  }

  private int getHorizontalSpace() {
    return getWidth() - getPaddingLeft() - getPaddingRight();
  }

  @Override public boolean canScrollVertically() {
    return true;
  }

  public void addPuzzleLayout(PuzzleLayout puzzleLayout) {
    Range range = new Range(totalPuzzleSize, totalPuzzleSize + puzzleLayout.getBlockSize() - 1);
    totalPuzzleSize += puzzleLayout.getBlockSize();
    rangePuzzleLayoutMap.put(range, puzzleLayout);
    puzzleLayouts.add(puzzleLayout);
  }

  private Range getContainsRange(int position) {
    for (Range range : rangePuzzleLayoutMap.keySet()) {
      if (range.contains(position)) {
        return range;
      }
    }
    return null;
  }

  private PuzzleLayout getPuzzleLayout(int position) {
    Range range = getContainsRange(position);
    return range == null ? null : rangePuzzleLayoutMap.get(range);
  }

  // TODO this is just simple method
  private int calculateTotalLength() {
    return getHorizontalSpace() * puzzleLayouts.size();
  }

  private void layoutPuzzle() {
    int left = getPaddingLeft();
    int top = getPaddingTop();
    // TODO 暂时每个PuzzleLayout为正方形
    int width = getHorizontalSpace();
    int height = getHorizontalSpace();

    for (PuzzleLayout layout : puzzleLayouts) {
      tempRect.set(left, top, left + width, top + height);
      top += height;
      layout.setOuterBlock(tempRect);
      layout.layout();
    }
  }

  static class Range {
    int start;
    int end;

    public Range(int start, int end) {
      this.start = start;
      this.end = end;
    }

    public boolean contains(int value) {
      return value >= start && value <= end;
    }

    @Override public boolean equals(Object obj) {
      if (obj instanceof Range) {
        Range other = (Range) obj;
        return this.start == other.start && this.end == other.end;
      }
      return false;
    }

    @Override public int hashCode() {
      int result = 17;
      result = 31 * result + start;
      result = 31 * result + end;
      return result;
    }
  }
}
