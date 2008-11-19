/*
  Copyright 2008 Google Inc.
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/ 
package com.kmagic.solitaire;

import android.graphics.Canvas;
import android.util.Log;
import java.lang.InterruptedException;
import java.lang.Runnable;
import java.lang.Thread;
import java.lang.Math;
import java.util.concurrent.Semaphore;

public class AnimateCard {

  private static final float PPF = 40;

  protected SolitaireView mView;
  private Card[] mCard;
  private int mCount;
  private int mFrames;
  private float mDx;
  private float mDy;
  private boolean mAnimate;

  public AnimateCard(SolitaireView view) {
    mView = view;
    mAnimate = true;
    mCard = new Card[104];
  }

  public void SetAnimate(boolean animate) { mAnimate = animate; }

  public void Draw(DrawMaster drawMaster, Canvas canvas) {
    for (int j = 0; j < mCount; j++) {
      mCard[j].MovePosition(-mDx, -mDy);
    }
    for (int i = 0; i < mCount; i++) {
      drawMaster.DrawCard(canvas, mCard[i]);
    }
    mFrames--;
    if (mFrames <= 0) {
      mView.StopAnimate();
    }
  }

  public void MoveCards(Card[] card, int count, float x, float y) {
    for (int i = 0; i < count; i++) {
      mCard[i] = card[i];
    }
    mCount = count;
    Move(mCard[0], x, y);
  }
  public void MoveCard(Card card, float x, float y) {
    mCard[0] = card;
    mCount = 1;
    Move(card, x, y);
  }

  private void Move(Card card, float x, float y) {
    float dx = x - card.GetX(); 
    float dy = y - card.GetY(); 

    mFrames = Math.round((float)Math.sqrt(dx * dx + dy * dy) / PPF);
    if (mFrames == 0) {
      mFrames = 1;
    }
    mDx = dx / mFrames;
    mDy = dy / mFrames;

    mView.StartAnimate();
    if (!mAnimate) {
      mView.StopAnimate();
    }
  }

}
