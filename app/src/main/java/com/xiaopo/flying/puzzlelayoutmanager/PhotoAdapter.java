package com.xiaopo.flying.puzzlelayoutmanager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.xiaopo.flying.puzzlelayoutmanager.model.Photo;
import java.io.File;
import java.util.List;

/**
 * @author wupanjie
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
  private List<Photo> data;

  @Override public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    Log.d("Puzzle", "onCreateViewHolder: ");
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
    return new PhotoViewHolder(itemView);
  }

  @Override public void onBindViewHolder(PhotoViewHolder holder, int position) {
    Log.d("Puzzle", "onBindViewHolder: ");
    Picasso.with(holder.itemView.getContext())
        .load(new File(data.get(position).getPath()))
        .centerInside()
        .fit()
        .into(holder.ivCard);
  }

  @Override public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public void refreshData(List<Photo> photos) {
    this.data = photos;
    notifyDataSetChanged();
  }

  static class PhotoViewHolder extends RecyclerView.ViewHolder {
    ImageView ivCard;

    public PhotoViewHolder(View itemView) {
      super(itemView);
      ivCard = (ImageView) itemView.findViewById(R.id.iv_card);
    }
  }
}
