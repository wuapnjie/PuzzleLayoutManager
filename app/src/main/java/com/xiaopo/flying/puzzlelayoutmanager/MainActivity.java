package com.xiaopo.flying.puzzlelayoutmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout.FirstPuzzleLayout;
import com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout.SecondPuzzleLayout;
import com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout.ThirdPuzzleLayout;

public class MainActivity extends AppCompatActivity {
  private RecyclerView puzzleList;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    puzzleList = (RecyclerView) findViewById(R.id.puzzle_list);

    PuzzleLayoutManager puzzleLayoutManager = new PuzzleLayoutManager();
    puzzleLayoutManager.addPuzzleLayout(new FirstPuzzleLayout());
    puzzleLayoutManager.addPuzzleLayout(new SecondPuzzleLayout());
    puzzleLayoutManager.addPuzzleLayout(new ThirdPuzzleLayout());
    puzzleList.setLayoutManager(puzzleLayoutManager);
    puzzleList.setAdapter(new CardAdapter());
  }
}
