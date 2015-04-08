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

  Modified by Curtis Gedak 2015
*/
package net.sourceforge.solitaire_cg;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import java.text.DecimalFormat;


public class DrawMaster {

  private Context mContext;

  // Background
  private int mScreenWidth;
  private int mScreenHeight;
  private Paint mBGPaint;

  // Card stuff
  private final Paint mSuitPaint = new Paint();
  private Bitmap[] mCardBitmap;
  private Bitmap mCardHidden;

  private Paint mEmptyAnchorPaint;
  private Paint mDoneEmptyAnchorPaint;
  private Paint mShadePaint;
  private Paint mLightShadePaint;
  
  private Paint mTimePaint;
  private int mLastSeconds;
  private String mTimeString;
  private Paint mScorePaint;

  private Bitmap mBoardBitmap;
  private Canvas mBoardCanvas;

  public DrawMaster(Context context) {

    mContext = context;
    // Default to this for simplicity
    mScreenWidth = 480;
    mScreenHeight = 295;

    // Background
    mBGPaint = new Paint();
    mBGPaint.setARGB(255, 0, 128, 0);

    mShadePaint = new Paint();
    mShadePaint.setARGB(200, 0, 0, 0);

    mLightShadePaint = new Paint();
    mLightShadePaint.setARGB(100, 0, 0, 0);

    // Card related stuff
    mEmptyAnchorPaint = new Paint();
    mEmptyAnchorPaint.setARGB(255, 0, 64, 0);
    mDoneEmptyAnchorPaint = new Paint();
    mDoneEmptyAnchorPaint.setARGB(128, 255, 0, 0);

    mTimePaint = new Paint();
    mTimePaint.setTextSize(18);
    mTimePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
    mTimePaint.setTextAlign(Paint.Align.RIGHT);
    mTimePaint.setAntiAlias(true);
    mLastSeconds = -1;

    mCardBitmap = new Bitmap[52];
    DrawCards(false);
    mBoardBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
    mBoardCanvas = new Canvas(mBoardBitmap);
  }

  public int GetWidth() { return mScreenWidth; }
  public int GetHeight() { return mScreenHeight; }
  public Canvas GetBoardCanvas() { return mBoardCanvas; }

  public void DrawCard(Canvas canvas, Card card) {
    float x = card.GetX();
    float y = card.GetY();
    int idx = card.GetSuit()*13+(card.GetValue()-1);
    canvas.drawBitmap(mCardBitmap[idx], x, y, mSuitPaint);
  }

  public void DrawHiddenCard(Canvas canvas, Card card) {
    float x = card.GetX();
    float y = card.GetY();
    canvas.drawBitmap(mCardHidden, x, y, mSuitPaint);
  }

  public void DrawEmptyAnchor(Canvas canvas, float x, float y, boolean done) {
    RectF pos = new RectF(x, y, x + Card.WIDTH, y + Card.HEIGHT);
    if (!done) {
      canvas.drawRoundRect(pos, 4, 4, mEmptyAnchorPaint);
    } else {
      canvas.drawRoundRect(pos, 4, 4, mDoneEmptyAnchorPaint);
    }
  }

  public void DrawBackground(Canvas canvas) {
    canvas.drawRect(0, 0, mScreenWidth, mScreenHeight, mBGPaint);
  }

  public void DrawShade(Canvas canvas) {
    canvas.drawRect(0, 0, mScreenWidth, mScreenHeight, mShadePaint);
  }

  public void DrawLightShade(Canvas canvas) {
    canvas.drawRect(0, 0, mScreenWidth, mScreenHeight, mLightShadePaint);
  }

  public void DrawLastBoard(Canvas canvas) {
    canvas.drawBitmap(mBoardBitmap, 0, 0, mSuitPaint);
  }

  public void SetScreenSize(int width, int height) {
    mScreenWidth = width;
    mScreenHeight = height;
    mBoardBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    mBoardCanvas = new Canvas(mBoardBitmap);
  }

  public void DrawCards(boolean bigCards) {
    if (bigCards) {
      DrawBigCards(mContext.getResources());
    } else {
      DrawCards(mContext.getResources());
    }
  }

  private void DrawBigCards(Resources r) {

    Paint cardFrontPaint = new Paint();
    Paint cardBorderPaint = new Paint();
    Bitmap[] bigSuit = new Bitmap[4];
    Bitmap[] suit = new Bitmap[4];
    Bitmap[] blackFont = new Bitmap[13];
    Bitmap[] redFont = new Bitmap[13];
    Canvas canvas;
    int width = Card.WIDTH;
    int height = Card.HEIGHT;
    int w = 0;   // Width  of actual bitmap image
    int h = 0;   // Height of actual bitmap image

    Drawable drawable = r.getDrawable(R.drawable.cardback);

    mCardHidden = Bitmap.createBitmap(Card.WIDTH, Card.HEIGHT,
                                      Bitmap.Config.ARGB_4444);
    canvas = new Canvas(mCardHidden);
    drawable.setBounds(0, 0, Card.WIDTH, Card.HEIGHT);
    drawable.draw(canvas);

    // Load suit bitmaps
    drawable = r.getDrawable(R.drawable.suits);
    w = drawable.getIntrinsicWidth();
    h = drawable.getIntrinsicHeight();
    int suitWidth = w/4;
    int suitHeight = h;
    for (int i = 0; i < 4; i++) {
      suit[i] = Bitmap.createBitmap(w/4, h, Bitmap.Config.ARGB_4444);
      canvas = new Canvas(suit[i]);
      drawable.setBounds(-i*w/4, 0, -i*w/4+w, h);
      drawable.draw(canvas);
    }

    // Load big suit bitmaps
    drawable = r.getDrawable(R.drawable.bigsuits);
    w = drawable.getIntrinsicWidth();
    h = drawable.getIntrinsicHeight();
    int bigSuitWidth = w/4;
    int bigSuitHeight = h;
    for (int i = 0; i < 4; i++) {
      bigSuit[i] = Bitmap.createBitmap(w/4, h, Bitmap.Config.ARGB_4444);
      canvas = new Canvas(bigSuit[i]);
      drawable.setBounds(-i*w/4, 0, -i*w/4+w, h);
      drawable.draw(canvas);
    }

    // Load big black font bitmaps
    drawable = r.getDrawable(R.drawable.bigblackfont);
    w = drawable.getIntrinsicWidth();
    h = drawable.getIntrinsicHeight();
    int fontWidth = w/13; // Same as for red below
    for (int i = 0; i < 13; i++) {
      blackFont[i] = Bitmap.createBitmap(w/13, h, Bitmap.Config.ARGB_4444);
      canvas = new Canvas(blackFont[i]);
      drawable.setBounds(-i*w/13, 0, -i*w/13+w, h);
      drawable.draw(canvas);
    }

    // Load big red font bitmaps
    drawable = r.getDrawable(R.drawable.bigredfont);
    for (int i = 0; i < 13; i++) {
      redFont[i] = Bitmap.createBitmap(w/13, h, Bitmap.Config.ARGB_4444);
      canvas = new Canvas(redFont[i]);
      drawable.setBounds(-i*w/13, 0, -i*w/13+w, h);
      drawable.draw(canvas);
    }

    // Create deck of cards
    cardBorderPaint.setARGB(255, 0, 0, 0);
    cardFrontPaint.setARGB(255, 255, 255, 255);
    RectF pos = new RectF();
    for (int suitIdx = 0; suitIdx < 4; suitIdx++) {
      for (int valueIdx = 0; valueIdx < 13; valueIdx++) {
        mCardBitmap[suitIdx*13+valueIdx] = Bitmap.createBitmap(
            width, height, Bitmap.Config.ARGB_4444);
        canvas = new Canvas(mCardBitmap[suitIdx*13+valueIdx]);
        // Draw card outline
        int cardOutline = 2;
        int roundEdge = 4;   // Round curve on each card corner
        pos.set(0, 0, width, height);
        canvas.drawRoundRect(pos, roundEdge, roundEdge, cardBorderPaint);
        pos.set(1, 1, width-1, height-1);
        canvas.drawRoundRect(pos, roundEdge, roundEdge, cardFrontPaint);

	// Draw font in upper-left
        if ((suitIdx & 1) == 1) {
          canvas.drawBitmap(redFont[valueIdx], cardOutline+1, cardOutline+2,
                            mSuitPaint);
        } else {
          canvas.drawBitmap(blackFont[valueIdx], cardOutline+1, cardOutline+2,
                            mSuitPaint);
        }

	// Draw suit in upper-right
        canvas.drawBitmap(suit[suitIdx], width-suitWidth-cardOutline-2,
                          cardOutline+2, mSuitPaint);
	// Draw big suit in center of card
        canvas.drawBitmap(bigSuit[suitIdx], (width-bigSuitWidth)/2+1,
                          (height-bigSuitHeight)/2+3, mSuitPaint);
      }
    }
  }

  private void DrawCards(Resources r) {

    Paint cardFrontPaint = new Paint();
    Paint cardBorderPaint = new Paint();
    Bitmap[] suit = new Bitmap[4];
    Bitmap[] revSuit = new Bitmap[4];
    Bitmap[] smallSuit = new Bitmap[4];
    Bitmap[] revSmallSuit = new Bitmap[4];
    Bitmap[] blackFont = new Bitmap[13];
    Bitmap[] revBlackFont = new Bitmap[13];
    Bitmap[] redFont = new Bitmap[13];
    Bitmap[] revRedFont = new Bitmap[13];
    Bitmap redJack;
    Bitmap redRevJack;
    Bitmap redQueen;
    Bitmap redRevQueen;
    Bitmap redKing;
    Bitmap redRevKing;
    Bitmap blackJack;
    Bitmap blackRevJack;
    Bitmap blackQueen;
    Bitmap blackRevQueen;
    Bitmap blackKing;
    Bitmap blackRevKing;
    Canvas canvas;
    int width = Card.WIDTH;
    int height = Card.HEIGHT;
    int w = 0;   // Width  of actual bitmap image
    int h = 0;   // Height of actual bitmap image

    Drawable drawable = r.getDrawable(R.drawable.cardback);

    mCardHidden = Bitmap.createBitmap(Card.WIDTH, Card.HEIGHT,
                                      Bitmap.Config.ARGB_4444);
    canvas = new Canvas(mCardHidden);
    drawable.setBounds(0, 0, Card.WIDTH, Card.HEIGHT);
    drawable.draw(canvas);

    // Load suit bitmaps
    drawable = r.getDrawable(R.drawable.suits);
    w = drawable.getIntrinsicWidth();
    h = drawable.getIntrinsicHeight();
    for (int i = 0; i < 4; i++) {
      suit[i] = Bitmap.createBitmap(w/4, h, Bitmap.Config.ARGB_4444);
      revSuit[i] = Bitmap.createBitmap(w/4, h, Bitmap.Config.ARGB_4444);
      canvas = new Canvas(suit[i]);
      drawable.setBounds(-i*w/4, 0, -i*w/4+w, h);
      drawable.draw(canvas);
      canvas = new Canvas(revSuit[i]);
      canvas.rotate(180);
      drawable.setBounds(-i*w/4-w/4, -h, -i*w/4+(w-w/4), 0);
      drawable.draw(canvas);
    }

    // Define faceBox line pairs now that we have suits (w)idth and (h)eight.
    // Allow 1 pixel for line.
    //                     x0,y0,x1,y1
    // float[] faceBox = { 9,8,width-10,8,               //  Top edge
    //                     width-10,8,width-10,height-9, //  Right edge
    //                     width-10,height-9,9,height-9, //  Bottom edge
    //                     9,height-9,9,8                //  Left edge
    //                   };
    int suitWidth = w/4;
    int suitHeight = h;
    float[] faceBox = {
      suitWidth-1, suitHeight-2, width-suitWidth, suitHeight-2,
      width-suitWidth, suitHeight-2, width-suitWidth, height-suitHeight+1,
      width-suitWidth, height-suitHeight+1, suitWidth-1, height-suitHeight+1,
      suitWidth-1, height-suitHeight+1, suitWidth-1, suitHeight-2
                      };

    // Load small suit bitmaps
    drawable = r.getDrawable(R.drawable.smallsuits);
    w = drawable.getIntrinsicWidth();
    h = drawable.getIntrinsicHeight();
    int smallSuitWidth = w/4;
    int smallSuitHeight = h;
    for (int i = 0; i < 4; i++) {
      smallSuit[i] = Bitmap.createBitmap(w/4, h, Bitmap.Config.ARGB_4444);
      revSmallSuit[i] = Bitmap.createBitmap(w/4, h, Bitmap.Config.ARGB_4444);
      canvas = new Canvas(smallSuit[i]);
      drawable.setBounds(-i*w/4, 0, -i*w/4+w, h);
      drawable.draw(canvas);
      canvas = new Canvas(revSmallSuit[i]);
      canvas.rotate(180);
      drawable.setBounds(-i*w/4-w/4, -h, -i*w/4+(w-w/4), 0);
      drawable.draw(canvas);
    }

    // Load medium black font bitmaps
    drawable = r.getDrawable(R.drawable.medblackfont);
    w = drawable.getIntrinsicWidth();
    h = drawable.getIntrinsicHeight();
    int fontWidth = w/13;   // Same as for red below
    int fontHeight = h;     // Same as for red below
    for (int i = 0; i < 13; i++) {
      blackFont[i] = Bitmap.createBitmap(fontWidth, fontHeight, Bitmap.Config.ARGB_4444);
      revBlackFont[i] = Bitmap.createBitmap(fontWidth, fontHeight, Bitmap.Config.ARGB_4444);
      canvas = new Canvas(blackFont[i]);
      drawable.setBounds(-i*fontWidth, 0, -i*fontWidth+13*fontWidth, fontHeight);
      drawable.draw(canvas);
      canvas = new Canvas(revBlackFont[i]);
      canvas.rotate(180);
      drawable.setBounds(-i*fontWidth-fontWidth, -fontHeight, -i*fontWidth+(12*fontWidth), 0);
      drawable.draw(canvas);
    }

    // Load medium red font bitmaps
    drawable = r.getDrawable(R.drawable.medredfont);
    for (int i = 0; i < 13; i++) {
      redFont[i] = Bitmap.createBitmap(fontWidth, fontHeight, Bitmap.Config.ARGB_4444);
      revRedFont[i] = Bitmap.createBitmap(fontWidth, fontHeight, Bitmap.Config.ARGB_4444);
      canvas = new Canvas(redFont[i]);
      drawable.setBounds(-i*fontWidth, 0, -i*fontWidth+13*fontWidth, fontHeight);
      drawable.draw(canvas);
      canvas = new Canvas(revRedFont[i]);
      canvas.rotate(180);
      drawable.setBounds(-i*fontWidth-fontWidth, -fontHeight, -i*fontWidth+(12*fontWidth), 0);
      drawable.draw(canvas);
    }

    int faceWidth = width - suitWidth*2;
    int faceHeight = height/2 - suitHeight+1;
    // Load red jack bitmap
    drawable = r.getDrawable(R.drawable.redjack);
    redJack = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    redRevJack = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    canvas = new Canvas(redJack);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(redRevJack);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);

    // Load red queen bitmap
    drawable = r.getDrawable(R.drawable.redqueen);
    redQueen = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    redRevQueen = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    canvas = new Canvas(redQueen);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(redRevQueen);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);

    // Load red king bitmap
    drawable = r.getDrawable(R.drawable.redking);
    redKing = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    redRevKing = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    canvas = new Canvas(redKing);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(redRevKing);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);

    // Load black jack bitmap
    drawable = r.getDrawable(R.drawable.blackjack);
    blackJack = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    blackRevJack = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    canvas = new Canvas(blackJack);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(blackRevJack);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);

    // Load black queen bitmap
    drawable = r.getDrawable(R.drawable.blackqueen);
    blackQueen = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    blackRevQueen = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    canvas = new Canvas(blackQueen);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(blackRevQueen);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);

    // Load black king bitmap
    drawable = r.getDrawable(R.drawable.blackking);
    blackKing = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    blackRevKing = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    canvas = new Canvas(blackKing);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(blackRevKing);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);

    // Create deck of cards
    cardBorderPaint.setARGB(255, 0, 0, 0);
    cardFrontPaint.setARGB(255, 255, 255, 255);
    RectF pos = new RectF();
    for (int suitIdx = 0; suitIdx < 4; suitIdx++) {
      for (int valueIdx = 0; valueIdx < 13; valueIdx++) {
        mCardBitmap[suitIdx*13+valueIdx] = Bitmap.createBitmap(
            width, height, Bitmap.Config.ARGB_4444);
        canvas = new Canvas(mCardBitmap[suitIdx*13+valueIdx]);
        // Draw card outline
        int cardOutline = 2;
        int roundEdge = 4;   // Round curve on each card corner
        pos.set(0, 0, width, height);
        canvas.drawRoundRect(pos, roundEdge, roundEdge, cardBorderPaint);
        pos.set(1, 1, width-1, height-1);
        canvas.drawRoundRect(pos, roundEdge, roundEdge, cardFrontPaint);

        if ((suitIdx & 1) == 1) {
          // Draw upper-left red card number
          canvas.drawBitmap(redFont[valueIdx], cardOutline,
                            cardOutline+2, mSuitPaint);
          // Draw lower-right red card number
          canvas.drawBitmap(revRedFont[valueIdx], width-fontWidth-cardOutline,
                            height-fontHeight-cardOutline-2, mSuitPaint);
        } else {
          // Draw upper-left black card number
          canvas.drawBitmap(blackFont[valueIdx], cardOutline,
                            cardOutline+2, mSuitPaint);
          // Draw lower-right black card number
          canvas.drawBitmap(revBlackFont[valueIdx], width-fontWidth-cardOutline,
                            height-fontHeight-cardOutline-2, mSuitPaint);
        }

	// Draw small suit centered below upper-left card number
        canvas.drawBitmap(smallSuit[suitIdx],
                          cardOutline+(fontWidth-smallSuitWidth)/2,
                          cardOutline+2+fontHeight+2,
                          mSuitPaint);
	// Draw small suit centered above lower-right card number
        canvas.drawBitmap(revSmallSuit[suitIdx],
                          width-cardOutline-fontWidth+(fontWidth-smallSuitWidth)/2,
                          height-cardOutline-2-fontHeight-2-smallSuitHeight,
                          mSuitPaint);

        // Add suit to face cards
        if (valueIdx >= 10) {
        // Draw suit in upper-left
          canvas.drawBitmap(suit[suitIdx],
                            cardOutline+1+fontWidth,
                            suitHeight,
                            mSuitPaint);
        // Draw suit in lower-right
          canvas.drawBitmap(revSuit[suitIdx],
                            width-cardOutline-fontWidth-suitWidth,
                            height-suitHeight-suitHeight,
                            mSuitPaint);
        }

        // Add multiple suits positioned geometrically on card
        int[] suitX = {suitWidth-1, (width-suitWidth)/2, width-suitWidth*2};
        int[] suitY = {suitHeight-3, 2*height/5-suitHeight/2,
                       3*height/5-suitHeight/2,height-suitHeight*2+2};
        int suitMidY = (height-suitHeight)/2-1;
        switch (valueIdx+1) {
          case 1:
            canvas.drawBitmap(suit[suitIdx], suitX[1], suitMidY, mSuitPaint);
            break;
          case 2:
            canvas.drawBitmap(suit[suitIdx], suitX[1], suitY[0], mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[1], suitY[3], mSuitPaint);
            break;
          case 3:
            canvas.drawBitmap(suit[suitIdx], suitX[1], suitY[0], mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[1], suitMidY, mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[1], suitY[3], mSuitPaint);
            break;
          case 4:
            canvas.drawBitmap(suit[suitIdx], suitX[0], suitY[0], mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[2], suitY[0], mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[0], suitY[3], mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[2], suitY[3], mSuitPaint);
            break;
          case 5:
            canvas.drawBitmap(suit[suitIdx], suitX[0], suitY[0], mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[2], suitY[0], mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[1], suitMidY, mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[0], suitY[3], mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[2], suitY[3], mSuitPaint);
            break;
          case 6:
            canvas.drawBitmap(suit[suitIdx], suitX[0], suitY[0], mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[2], suitY[0], mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[0], suitMidY, mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[2], suitMidY, mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[0], suitY[3], mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[2], suitY[3], mSuitPaint);
            break;
          case 7:
            canvas.drawBitmap(suit[suitIdx], suitX[0], suitY[0], mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[2], suitY[0], mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[0], suitMidY, mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[2], suitMidY, mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[1], (suitMidY+suitY[0])/2, mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[0], suitY[3], mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[2], suitY[3], mSuitPaint);
            break;
          case 8:
            canvas.drawBitmap(suit[suitIdx], suitX[0], suitY[0], mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[2], suitY[0], mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[0], suitMidY, mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[2], suitMidY, mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], suitX[1], (suitMidY+suitY[0])/2, mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[0], suitY[3], mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[2], suitY[3], mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[1], (suitY[3]+suitMidY)/2, mSuitPaint);
            break;
          case 9:
            for (int i = 0; i < 4; i++) {
              canvas.drawBitmap(suit[suitIdx], suitX[(i%2)*2], suitY[i/2], mSuitPaint);
              canvas.drawBitmap(revSuit[suitIdx], suitX[(i%2)*2], suitY[i/2+2], mSuitPaint);
            }
            canvas.drawBitmap(suit[suitIdx], suitX[1], suitMidY, mSuitPaint);
            break;
          case 10:
            for (int i = 0; i < 4; i++) {
              canvas.drawBitmap(suit[suitIdx], suitX[(i%2)*2], suitY[i/2], mSuitPaint);
              canvas.drawBitmap(revSuit[suitIdx], suitX[(i%2)*2], suitY[i/2+2], mSuitPaint);
            }
            canvas.drawBitmap(suit[suitIdx], suitX[1], (suitY[1]+suitY[0])/2, mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], suitX[1], (suitY[3]+suitY[2])/2, mSuitPaint);
            break;

          case Card.JACK:
            canvas.drawLines(faceBox, cardBorderPaint);
            if ((suitIdx & 1) == 1) {
              canvas.drawBitmap(redJack, suitWidth, suitHeight-1, mSuitPaint);
              canvas.drawBitmap(redRevJack, suitWidth, height-faceHeight-suitHeight+1, mSuitPaint);
            } else {
              canvas.drawBitmap(blackJack, suitWidth, suitHeight-1, mSuitPaint);
              canvas.drawBitmap(blackRevJack, suitWidth, height-faceHeight-suitHeight+1, mSuitPaint);
            }
            break;
          case Card.QUEEN:
            canvas.drawLines(faceBox, cardBorderPaint);
            if ((suitIdx & 1) == 1) {
              canvas.drawBitmap(redQueen, suitWidth, suitHeight-1, mSuitPaint);
              canvas.drawBitmap(redRevQueen, suitWidth, height-faceHeight-suitHeight+1, mSuitPaint);
            } else {
              canvas.drawBitmap(blackQueen, suitWidth, suitHeight-1, mSuitPaint);
              canvas.drawBitmap(blackRevQueen, suitWidth, height-faceHeight-suitHeight+1, mSuitPaint);
            }
            break;
          case Card.KING:
            canvas.drawLines(faceBox, cardBorderPaint);
            if ((suitIdx & 1) == 1) {
              canvas.drawBitmap(redKing, suitWidth, suitHeight-1, mSuitPaint);
              canvas.drawBitmap(redRevKing, suitWidth, height-faceHeight-suitHeight+1, mSuitPaint);
            } else {
              canvas.drawBitmap(blackKing, suitWidth, suitHeight-1, mSuitPaint);
              canvas.drawBitmap(blackRevKing, suitWidth, height-faceHeight-suitHeight+1, mSuitPaint);
            }
            break;
        }
      }
    }
  }

  public void DrawTime(Canvas canvas, int millis) {
    int seconds = (millis / 1000) % 60;
    int minutes = millis / 60000;
    if (seconds != mLastSeconds) {
      mLastSeconds = seconds;
      // String.format is insanely slow (~15ms)
      if (seconds < 10) {
        mTimeString = minutes + ":0" + seconds;
      } else {
        mTimeString = minutes + ":" + seconds;
      }
    }
    mTimePaint.setARGB(255, 20, 20, 20);
    canvas.drawText(mTimeString, mScreenWidth-9, mScreenHeight-9, mTimePaint);
    mTimePaint.setARGB(255, 0, 0, 0);
    canvas.drawText(mTimeString, mScreenWidth-10, mScreenHeight-10, mTimePaint);
  }

  public void DrawRulesString(Canvas canvas, String score) {
    mTimePaint.setARGB(255, 20, 20, 20);
    canvas.drawText(score, mScreenWidth-9, mScreenHeight-29, mTimePaint);
    if (score.charAt(0) == '-') {
      mTimePaint.setARGB(255, 255, 0, 0);
    } else {
      mTimePaint.setARGB(255, 0, 0, 0);
    }
    canvas.drawText(score, mScreenWidth-10, mScreenHeight-30, mTimePaint);

  }
}
