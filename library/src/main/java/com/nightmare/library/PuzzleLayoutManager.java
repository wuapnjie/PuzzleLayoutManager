package com.nightmare.library;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO 在滑动过程中产生了太多的HashMap的Key迭代器
 *
 * @author wupanjie
 */
public class PuzzleLayoutManager extends RecyclerView.LayoutManager {
  private static final String TAG = "PuzzleLayoutManager";

  public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;
  public static final int VERTICAL = OrientationHelper.VERTICAL;

  private int verticalScrollOffset;
  private int horizontalScrollOffset;

  private int totalLength;
  private int totalPuzzleSize = 0;

  @NonNull private List<RadioPuzzleLayout> puzzleLayouts = new ArrayList<>();
  @NonNull private Map<Range, RadioPuzzleLayout> rangePuzzleLayoutMap = new HashMap<>();
  @NonNull private SparseArray<View> currentViews = new SparseArray<>();

  private Rect tempRect = new Rect();
  private Range viewRange = new Range();

  private int orientation = VERTICAL;

  @Override public RecyclerView.LayoutParams generateDefaultLayoutParams() {
    return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
  }

  @Override public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    //Log.d(TAG, "onLayoutChildren: ");
    if (getItemCount() == 0) {//没有Item，界面空着吧
      detachAndScrapAttachedViews(recycler);
      return;
    }
    if (getChildCount() == 0 && state.isPreLayout()) {//state.isPreLayout()是支持动画的
      return;
    }

    totalLength = 0;
    layoutPuzzle();
    totalLength = calculateTotalLength();
    //如果所有子View的高度和没有填满RecyclerView的高度，
    // 则将高度设置为RecyclerView的高度
    totalLength = Math.max(totalLength, getVerticalSpace());

    //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
    detachAndScrapAttachedViews(recycler);

    fill(recycler, state);
  }

  private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
    int visibleChildCount = getVisibleChildCount();
    int startIndex = calculateFirstArea();
    int endIndex = calculateLastArea();
    //Log.d(TAG, "fill: first area --> " + startIndex);
    //Log.d(TAG, "fill: last area --> " + endIndex);

    viewRange.set(startIndex, endIndex);
    for (int i = 0; i < currentViews.size(); i++) {
      int key = currentViews.keyAt(i);
      if (!viewRange.contains(key)) {
        recycler.recycleView(currentViews.get(key));
      }
    }

    currentViews.clear();

    for (int i = startIndex; i < endIndex; i++) {
      Range range = getContainsRange(i);
      RadioPuzzleLayout puzzleLayout = rangePuzzleLayoutMap.get(range);

      if (range == null || puzzleLayout == null) continue;

      int positionInPuzzle = i - range.start;
      Area area = puzzleLayout.getArea(positionInPuzzle);

      // 这里就是从缓存里面取出
      View view = recycler.getViewForPosition(i);

      // 回收在序列中但不可见区域的View
      if (!isAreaVisible(area)) {
        recycler.recycleView(view);
        continue;
      }
      //将View加入到RecyclerView中
      addView(view);

      currentViews.put(i, view);

      RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
      layoutParams.width = area.width();
      layoutParams.height = area.height();

      measureChildWithMargins(view, 0, 0);
      layoutDecorated(view, area.left(), area.top() - verticalScrollOffset, area.right(),
          area.bottom() - verticalScrollOffset);
    }

    // Log.d(TAG, "fill: area --> "
    //    + "left : "
    //    + area.left()
    //    + ",top : "
    //    + area.top()
    //    + ",right : "
    //    + area.right()
    //    + ",bottom : "
    //    + area.bottom());
  }

  /**
   * 获取当前可见的View数量
   *
   * @return 当前可见的View数量
   */
  private int getVisibleChildCount() {
    int first = 0;
    int last = 0;
    int height = 0;
    int visibleCount = 0;

    for (int i = 0; i < puzzleLayouts.size(); i++) {
      PuzzleLayout puzzleLayout = puzzleLayouts.get(i);
      height += puzzleLayout.height();
      if (height > verticalScrollOffset) {
        first = i;
        break;
      }
    }

    for (int i = first; i < puzzleLayouts.size(); i++) {
      if (height > verticalScrollOffset + getVerticalSpace()) {
        last = i;
        break;
      }
      PuzzleLayout puzzleLayout = puzzleLayouts.get(i);
      height += puzzleLayout.height();
    }

    for (int i = first; i <= last; i++) {
      PuzzleLayout puzzleLayout = puzzleLayouts.get(i);
      for (int j = 0; j < puzzleLayout.getAreaCount(); j++) {
        if (isAreaVisible(puzzleLayout.getArea(j))) {
          visibleCount++;
        }
      }
    }

    //Log.d(TAG, "getVisibleChildCount: first --> " + first);
    //Log.d(TAG, "getVisibleChildCount: last --> " + last);
    //Log.d(TAG, "getVisibleChildCount: visible --> " + visibleCount);

    return visibleCount;
  }

  /**
   * 如下，1是第一个可见的Area，但此时2可能不可见
   * -----------
   * |   |  2  |
   * | 1 |-----|
   * |   |  3  |
   * -----------
   *
   * @return 第一个可见的Area
   */
  private int calculateFirstArea() {
    int height = 0;
    int index = 0;
    for (RadioPuzzleLayout puzzleLayout : puzzleLayouts) {
      height += puzzleLayout.height();
      if (height > verticalScrollOffset) {
        for (int i = 0; i < puzzleLayout.getAreaCount(); i++) {
          Area area = puzzleLayout.getArea(i);
          if (area.bottom() > verticalScrollOffset) {
            return index + i;
          }
        }
      }
      index += puzzleLayout.getAreaCount();
    }
    return index;
  }

  /**
   * 返回最后一个可见的Area的position
   *
   * @return 最后一个可见的Area
   */
  private int calculateLastArea() {
    int height = 0;
    int index = 0;
    for (RadioPuzzleLayout puzzleLayout : puzzleLayouts) {
      height += puzzleLayout.height();
      if (height > verticalScrollOffset + getVerticalSpace()) {
        for (int i = 0; i < puzzleLayout.getAreaCount(); i++) {
          Area area = puzzleLayout.getArea(i);
          if (area.top() < verticalScrollOffset + getVerticalSpace()) {
            continue;
          }

          return index + i;
        }
      }
      index += puzzleLayout.getAreaCount();
    }
    return index;
  }

  private boolean isAreaVisible(Area area) {
    if (orientation == VERTICAL) {
      return (area.top() - verticalScrollOffset > 0
          && area.top() - verticalScrollOffset < getVerticalSpace()) || (area.bottom()
          - verticalScrollOffset > 0 && area.bottom() - verticalScrollOffset < getVerticalSpace());
    } else {
      // TODO 横向滑动
      return false;
    }
  }

  @Override
  public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
    //Log.d(TAG, "scrollVerticallyBy: dy --> " + dy);
    //实际要滑动的距离
    int travel = dy;

    //如果滑动到最顶部
    if (verticalScrollOffset + dy < 0) {
      travel = -verticalScrollOffset;
    } else if (verticalScrollOffset + dy > totalLength - getVerticalSpace()) {//如果滑动到最底部
      travel = totalLength - getVerticalSpace() - verticalScrollOffset;
    }

    //将竖直方向的偏移量+travel
    verticalScrollOffset += travel;
    // 平移容器内的item
    offsetChildrenVertical(-travel);

    detachAndScrapAttachedViews(recycler);
    fill(recycler, state);

    Log.d(TAG, "fill: scrapSize -->" + recycler.getScrapList().size());
    Log.d(TAG, "fill: childCount -->" + getChildCount());

    return travel;
  }

  @Override public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler,
      RecyclerView.State state) {
    // TODO 横向滑动支持
    return super.scrollHorizontallyBy(dx, recycler, state);
  }

  private int getVerticalSpace() {
    return getHeight() - getPaddingBottom() - getPaddingTop();
  }

  private int getHorizontalSpace() {
    return getWidth() - getPaddingLeft() - getPaddingRight();
  }

  @Override public boolean canScrollVertically() {
    return orientation == VERTICAL;
  }

  @Override public boolean canScrollHorizontally() {
    return orientation == HORIZONTAL;
  }

  public void addPuzzleLayout(RadioPuzzleLayout puzzleLayout) {
    Range range = new Range(totalPuzzleSize, totalPuzzleSize + puzzleLayout.getAreaCount() - 1);
    totalPuzzleSize += puzzleLayout.getAreaCount();
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

  public RadioPuzzleLayout getPuzzleLayout(int position) {
    Range range = getContainsRange(position);
    return range == null ? null : rangePuzzleLayoutMap.get(range);
  }

  private int calculateTotalLength() {
    return puzzleLayouts.isEmpty() ? 0 : puzzleLayouts.get(puzzleLayouts.size() - 1).bottom();
  }

  private void layoutPuzzle() {
    int left = getPaddingLeft();
    int top = getPaddingTop();

    int length = orientation == VERTICAL ? getHorizontalSpace() : getVerticalSpace();

    for (RadioPuzzleLayout layout : puzzleLayouts) {

      if (orientation == VERTICAL) {
        tempRect.set(left, top, left + length, top + (int) (length * layout.getRadio()));
        top += (int) (length * layout.getRadio());
      } else {
        tempRect.set(left, top, left + (int) (length * layout.getRadio()), top + length);
        left += (int) (length * layout.getRadio());
      }
      layout.setOuterBounds(tempRect);
      layout.reset();
      layout.layout();
    }
  }

  /**
   * Returns the current orientation of the layout.
   *
   * @return Current orientation,  either {@link #HORIZONTAL} or {@link #VERTICAL}
   * @see #setOrientation(int)
   */
  public int getOrientation() {
    return orientation;
  }

  /**
   * Sets the orientation of the layout. {@link android.support.v7.widget.LinearLayoutManager}
   * will do its best to keep scroll position.
   *
   * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
   */
  public void setOrientation(int orientation) {
    if (orientation != HORIZONTAL && orientation != VERTICAL) {
      throw new IllegalArgumentException("invalid orientation:" + orientation);
    }
    assertNotInLayoutOrScroll(null);
    if (this.orientation == orientation) {
      return;
    }
    this.orientation = orientation;
    requestLayout();
  }

  private static class Range {
    int start;
    int end;

    Range() {

    }

    Range(int start, int end) {
      this.start = start;
      this.end = end;
    }

    void set(int start, int end) {
      this.start = start;
      this.end = end;
    }

    boolean contains(int value) {
      return value >= start && value <= end;
    }

    @Override public boolean equals(Object obj) {
      if (obj == null) return false;
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
