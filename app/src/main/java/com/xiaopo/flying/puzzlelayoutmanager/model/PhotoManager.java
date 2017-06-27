package com.xiaopo.flying.puzzlelayoutmanager.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Photo manager
 * Created by Flying SnowBean on 2015/11/19.
 */
public class PhotoManager {
  private ContentResolver contentResolver;

  public PhotoManager(Context context) {
    contentResolver = context.getContentResolver();
  }

  public List<Photo> getAllPhoto() {
    List<Photo> photos = new ArrayList<>();

    Cursor cursor =
        contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] {
            MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATE_MODIFIED
        }, null, null, MediaStore.Images.Media.DATE_MODIFIED);
    if (cursor != null && cursor.moveToFirst()) {
      do {

        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        Long dataAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
        Long dataModified =
            cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));

        Photo photo = new Photo(path, dataAdded, dataModified);

        photos.add(photo);
      } while (cursor.moveToNext());
      cursor.close();
    }

    Collections.sort(photos, new Comparator<Photo>() {
      @Override public int compare(Photo lhs, Photo rhs) {
        long l = lhs.getDataModified();
        long r = rhs.getDataModified();
        return l > r ? -1 : (l == r ? 0 : 1);
      }
    });

    return photos;
  }
}
