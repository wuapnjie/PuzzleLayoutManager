package com.xiaopo.flying.puzzlelayoutmanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.nightmare.library.PuzzleLayout;
import com.nightmare.library.PuzzleLayoutManager;
import com.xiaopo.flying.puzzlelayoutmanager.model.Photo;
import com.xiaopo.flying.puzzlelayoutmanager.model.PhotoManager;
import com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout.FirstPuzzleLayout;
import com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout.ForthPuzzleLayout;
import com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout.SecondPuzzleLayout;
import com.xiaopo.flying.puzzlelayoutmanager.puzzlelayout.ThirdPuzzleLayout;
import java.io.File;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private RecyclerView puzzleList;
  private PhotoAdapter adapter;
  private PuzzleLayoutManager layoutManager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    puzzleList = (RecyclerView) findViewById(R.id.puzzle_list);

    layoutManager = new PuzzleLayoutManager();
    layoutManager.setOrientation(PuzzleLayoutManager.VERTICAL);
    layoutManager.addPuzzleLayout(new FirstPuzzleLayout(1.2f));
    layoutManager.addPuzzleLayout(new SecondPuzzleLayout(1.4f));
    layoutManager.addPuzzleLayout(new ThirdPuzzleLayout(1.3f));
    layoutManager.addPuzzleLayout(new ForthPuzzleLayout(1.1f));
    puzzleList.setLayoutManager(layoutManager);
    adapter = new PhotoAdapter();
    puzzleList.setAdapter(adapter);
    puzzleList.addItemDecoration(new RecyclerView.ItemDecoration() {
      @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        outRect.left = 10;
        outRect.right = 10;
        outRect.bottom = 10;
        outRect.top = 10;
      }
    });
    //puzzleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
    //  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    //    super.onScrolled(recyclerView, dx, dy);
    //    Log.d("Puzzle", "onScrolled: childCount --> " + recyclerView.getChildCount());
    //  }
    //});

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[] {
          Manifest.permission.READ_EXTERNAL_STORAGE
      }, 119);
    } else {
      startLoad();
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 119 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      startLoad();
    }
  }

  private void startLoad() {
    new GetAllPhotoTask() {
      @Override protected void onPostExecute(List<Photo> photos) {
        Iterator<Photo> iterator = photos.iterator();
        while (iterator.hasNext()){
          Photo photo = iterator.next();
          File file = new File(photo.getPath());
          if (!file.exists()){
            iterator.remove();
          }
        }
        adapter.refreshData(photos);
      }
    }.execute(new PhotoManager(this));
  }

  public void change(View view) {
    layoutManager.setOrientation(layoutManager.getOrientation() == PuzzleLayoutManager.VERTICAL
        ? PuzzleLayoutManager.HORIZONTAL : PuzzleLayoutManager.VERTICAL);
  }

  private static class GetAllPhotoTask extends AsyncTask<PhotoManager, Integer, List<Photo>> {
    @Override protected List<Photo> doInBackground(PhotoManager... params) {
      return params[0].getAllPhoto();
    }
  }
}
