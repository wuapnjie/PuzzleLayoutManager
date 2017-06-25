package com.xiaopo.flying.puzzlelayoutmanager;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.Random;

/**
 * @author wupanjie
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
  private Random random = new Random();

  @Override public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
    return new CardViewHolder(itemView);
  }

  @Override public void onBindViewHolder(CardViewHolder holder, int position) {
    holder.ivCard.setBackgroundColor(
        Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
  }

  @Override public int getItemCount() {
    return 14;
  }

  static class CardViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivCard;

    public CardViewHolder(View itemView) {
      super(itemView);
      ivCard = (ImageView) itemView.findViewById(R.id.iv_card);
    }
  }
}
