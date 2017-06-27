package com.xiaopo.flying.puzzlelayoutmanager;

import android.support.v7.widget.RecyclerView;
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
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.CardViewHolder> {
  private List<Photo> data;

  @Override public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
    return new CardViewHolder(itemView);
  }

  @Override public void onBindViewHolder(CardViewHolder holder, int position) {
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

  static class CardViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivCard;

    public CardViewHolder(View itemView) {
      super(itemView);
      ivCard = (ImageView) itemView.findViewById(R.id.iv_card);
    }
  }
}
