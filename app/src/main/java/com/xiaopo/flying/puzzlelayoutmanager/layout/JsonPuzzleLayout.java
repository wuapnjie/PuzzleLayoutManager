package com.xiaopo.flying.puzzlelayoutmanager.layout;

import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author wupanjie
 */
public class JsonPuzzleLayout extends PuzzleLayout {
  private final String layoutJsonString;

  public JsonPuzzleLayout(String layoutJsonString) {
    this.layoutJsonString = layoutJsonString;
  }

  @Override
  public void layout() {
    try {
      JSONObject layoutJson = new JSONObject(layoutJsonString);

      JSONArray steps = layoutJson.getJSONArray("steps");

      for (int i = 0; i < steps.length(); i++) {
        JSONObject step = steps.optJSONObject(i);
        String method = step.optString("method");
        int position = step.optInt("position");
        if (TextUtils.equals(method, "addLine")) {
          int direction = step.optInt("direction");
          double radio = step.optDouble("radio");
          addLine(position, Line.Direction.get(direction), (float) radio);
        } else if (TextUtils.equals(method, "addCross")) {
          double hRadio = step.optDouble("hRadio");
          double vRadio = step.optDouble("vRadio");
          addCross(position, (float) hRadio, (float) vRadio);
        } else if (TextUtils.equals(method, "cutEqual1")) {
          int direction = step.optInt("direction");
          int part = step.optInt("part");
          cutBlockEqualPart(position, part, Line.Direction.get(direction));
        } else if (TextUtils.equals(method, "cutEqual2")) {
          int hSize = step.optInt("hSize");
          int vSize = step.optInt("vSize");
          cutBlockEqualPart(position, hSize, vSize);
        } else if (TextUtils.equals(method, "cutSpiral")) {
          cutSpiral(position);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
