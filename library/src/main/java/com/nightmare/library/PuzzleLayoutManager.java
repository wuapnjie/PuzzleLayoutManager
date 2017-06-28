package com.nightmare.library;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO PuzzleLayout重复直至AreaCount > ItemCount
 * 自定义的LayoutManager，其布局效果由一系列的PuzzleLayout决定
 *
 * @author wupanjie
 */
public class PuzzleLayoutManager extends RecyclerView.LayoutManager {
  private static final String TAG = "PuzzleLayoutManager";

  public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;
  public static final int VERTICAL = OrientationHelper.VERTICAL;

  @NonNull private List<RadioPuzzleLayout> puzzleLayouts = new ArrayList<>();
  @NonNull private List<Range> ranges = new ArrayList<>();
  @NonNull private SparseArray<View> viewCache = new SparseArray<>();
  @NonNull private Rect tempRect = new Rect();

  private int verticalScrollOffset;
  private int horizontalScrollOffset;

  private int totalLength;
  private int totalPuzzleSize = 0;

  private int orientation = VERTICAL;

  public PuzzleLayoutManager() {
    this(VERTICAL);
  }

  /**
   * @param orientation Layout orientation. Should be {@link #HORIZONTAL} or {@link
   * #VERTICAL}.@param orientation
   */
  public PuzzleLayoutManager(int orientation) {
    setOrientation(orientation);
  }

  @Override public RecyclerView.LayoutParams generateDefaultLayoutParams() {
    return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
  }

  @Override public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    if (getItemCount() == 0) {
      removeAndRecycleAllViews(recycler);
      return;
    }
    if (getChildCount() == 0 && state.isPreLayout()) {//state.isPreLayout()是支持动画的
      return;
    }

    totalLength = 0;
    layoutPuzzle();
    totalLength = calculateTotalLength();
    // 如果所有子View的高度和没有填满RecyclerView的高度
    // 则将高度设置为RecyclerView的高度
    totalLength = Math.max(totalLength, getVerticalSpace());

    // onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
    detachAndScrapAttachedViews(recycler);

    fill(recycler, state);
  }

  private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
    viewCache.clear();

    for (int i = 0; i < getChildCount(); i++) {
      View view = getChildAt(i);
      int position = getPosition(view);
      viewCache.put(position, view);
    }

    for (int i = 0; i < viewCache.size(); i++) {
      detachView(viewCache.valueAt(i));
    }

    int first = findFirstItemInFirstVisiblePuzzleLayout();
    int last = findLastItemInLastVisiblePuzzleLayout();

    for (int i = first; i <= last; i++) {
      Range range = getContainsRange(i);
      if (range == null || range.puzzleLayout == null) continue;

      PuzzleLayout puzzleLayout = range.puzzleLayout;

      int positionInPuzzle = i - range.start;
      Area area = puzzleLayout.getArea(positionInPuzzle);

      if (!isAreaVisible(area)) {
        continue;
      }

      View view = viewCache.get(i);
      if (view != null) {
        attachView(view);
        viewCache.remove(i);
      } else {
        view = recycler.getViewForPosition(i);
        addView(view);

        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        layoutParams.width = area.width();
        layoutParams.height = area.height();

        measureChildWithMargins(view, 0, 0);

        if (orientation == VERTICAL) {
          layoutDecoratedWithMargins(view, area.left(), area.top() - verticalScrollOffset,
              area.right(), area.bottom() - verticalScrollOffset);
        } else {
          layoutDecoratedWithMargins(view, area.left() - horizontalScrollOffset, area.top(),
              area.right() - horizontalScrollOffset, area.bottom());
        }
      }
    }

    for (int i = 0; i < viewCache.size(); i++) {
      recycler.recycleView(viewCache.valueAt(i));
    }
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

    return visibleCount;
  }

  /**
   * 找到第一个可见的PuzzleLayout中的最后一块区域索引
   *
   * @return 第一个可见的PuzzleLayout中的第一块区域索引
   */
  private int findFirstItemInFirstVisiblePuzzleLayout() {
    int index = 0;
    int length = 0;
    int limit = orientation == VERTICAL ? verticalScrollOffset : horizontalScrollOffset;

    for (int i = 0; i < puzzleLayouts.size(); i++) {
      PuzzleLayout puzzleLayout = puzzleLayouts.get(i);
      length += orientation == VERTICAL ? puzzleLayout.height() : puzzleLayout.width();
      if (length >= limit) {
        break;
      }
      index += puzzleLayout.getAreaCount();
    }

    return index;
  }

  /**
   * 找到最后一个可见的PuzzleLayout中的最后一块区域索引
   *
   * @return 最后一个可见的PuzzleLayout中的最后一块区域索引
   */
  private int findLastItemInLastVisiblePuzzleLayout() {
    int length = 0;
    int index = 0;
    int limit = orientation == VERTICAL ? verticalScrollOffset + getVerticalSpace()
        : horizontalScrollOffset + getHorizontalSpace();

    for (int i = 0; i < puzzleLayouts.size(); i++) {
      PuzzleLayout puzzleLayout = puzzleLayouts.get(i);
      length += orientation == VERTICAL ? puzzleLayout.height() : puzzleLayout.width();
      index += puzzleLayout.getAreaCount();
      if (length >= limit) {
        break;
      }
    }
    return index - 1;
  }

  /**
   * 如下，如果1是第一个可见的Area，但此时2可能不可见
   * -----------
   * |   |  2  |
   * | 1 |-----|
   * |   |  3  |
   * -----------
   *
   * @return 第一个可见的Area
   */
  private int calculateFirstArea() {
    int length = 0;
    int index = 0;
    int offset = orientation == VERTICAL ? verticalScrollOffset : horizontalScrollOffset;
    for (RadioPuzzleLayout puzzleLayout : puzzleLayouts) {
      int puzzleLength = orientation == VERTICAL ? puzzleLayout.height() : puzzleLayout.width();
      length += puzzleLength;
      if (length > offset) {
        for (int i = 0; i < puzzleLayout.getAreaCount(); i++) {
          Area area = puzzleLayout.getArea(i);
          int farthest = orientation == VERTICAL ? area.bottom() : area.right();
          if (farthest > offset) {
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
    int length = 0;
    int index = 0;
    int offset = orientation == VERTICAL ? verticalScrollOffset : horizontalScrollOffset;
    int space = orientation == VERTICAL ? getVerticalSpace() : getHorizontalSpace();
    for (RadioPuzzleLayout puzzleLayout : puzzleLayouts) {
      int puzzleLength = orientation == VERTICAL ? puzzleLayout.height() : puzzleLayout.width();
      length += puzzleLength;
      if (length > offset + space) {
        for (int i = 0; i < puzzleLayout.getAreaCount(); i++) {
          Area area = puzzleLayout.getArea(i);
          int farthest = orientation == VERTICAL ? area.top() : area.left();
          if (farthest < offset + space) {
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
      return !((area.bottom() - verticalScrollOffset) <= 0
          || (area.top() - verticalScrollOffset) >= getVerticalSpace());
    } else {
      return !((area.right() - horizontalScrollOffset) <= 0
          || (area.left() - horizontalScrollOffset) >= getHorizontalSpace());
    }
  }

  @Override
  public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
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

    fill(recycler, state);

    return travel;
  }

  @Override public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler,
      RecyclerView.State state) {
    //实际要滑动的距离
    int travel = dx;

    //如果滑动到最顶部
    if (horizontalScrollOffset + dx < 0) {
      travel = -horizontalScrollOffset;
    } else if (horizontalScrollOffset + dx > totalLength - getHorizontalSpace()) {//如果滑动到最底部
      travel = totalLength - getHorizontalSpace() - horizontalScrollOffset;
    }

    //将竖直方向的偏移量+travel
    horizontalScrollOffset += travel;
    // 平移容器内的item
    offsetChildrenHorizontal(-travel);

    fill(recycler, state);

    return travel;
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
    range.attach(puzzleLayout);
    ranges.add(range);
    puzzleLayouts.add(puzzleLayout);
  }

  private Range getContainsRange(int position) {
    for (int i = 0; i < ranges.size(); i++) {
      Range range = ranges.get(i);
      if (range.contains(position)) {
        return range;
      }
    }
    return null;
  }

  public PuzzleLayout getPuzzleLayout(int position) {
    Range range = getContainsRange(position);
    return range == null ? null : range.puzzleLayout;
  }

  // TODO 总长度计算
  private int calculateTotalLength() {
    if (orientation == VERTICAL) {
      return puzzleLayouts.isEmpty() ? 0 : puzzleLayouts.get(puzzleLayouts.size() - 1).bottom();
    } else {
      return puzzleLayouts.isEmpty() ? 0 : puzzleLayouts.get(puzzleLayouts.size() - 1).right();
    }
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
    if (this.orientation == VERTICAL) {
      horizontalScrollOffset = 0;
    } else {
      verticalScrollOffset = 0;
    }
    requestLayout();
  }

  private static class Range {
    int start;
    int end;
    PuzzleLayout puzzleLayout;

    Range() {

    }

    Range(int start, int end) {
      if (start > end) {
        throw new IllegalArgumentException("start can't greater than end");
      }
      this.start = start;
      this.end = end;
    }

    void set(int start, int end) {
      this.start = start;
      this.end = end;
    }

    void attach(PuzzleLayout puzzleLayout) {
      this.puzzleLayout = puzzleLayout;
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
