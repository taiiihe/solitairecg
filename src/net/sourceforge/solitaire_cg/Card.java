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

class Card {

  public static final int CLUBS = 0;
  public static final int DIAMONDS = 1;
  public static final int SPADES = 2;
  public static final int HEARTS = 3;

  public static final int ACE = 1;
  public static final int JACK = 11;
  public static final int QUEEN = 12;
  public static final int KING = 13;
  public static final String TEXT[] = {
    "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"
  };

  public static int WIDTH = 45;   // Card width
  public static int HEIGHT = 64;  // Card height
  public static int SMALL_SPACING = 7;  // Top shown portion of card on stack
  public static int HIDDEN_SPACING = 3; // Tip shown of hidden card on stack

  private int mValue;
  private int mSuit;
  private float mX;
  private float mY;

  public static void SetSize(int type, int screenWidth, int dpi) {
    int mdpi_card_width = 45;
    if (type == Rules.SOLITAIRE) {
      // 7 anchor columns
      mdpi_card_width = 51;
    } else if (type == Rules.FREECELL) {
      // 8 anchor columns
      mdpi_card_width = 49;
    } else {
      // 10 anchor columns
      mdpi_card_width = 45;
    }

    // Calculate card WIDTH
    if (screenWidth >= 480) {
      // Simulate Android scaling of original solitaire-for-android card
      //   width by using the average of high and low value of calculations
      //   due to loss of precision when using integer math.
      WIDTH = (   (screenWidth/480*mdpi_card_width) /* low precision */
                + (screenWidth*mdpi_card_width/480) /* high precision */
              ) / 2;
    } else {
      //   Multiply and divide by 4 to deal with 0.75x ldpi graphics
      WIDTH = (   (4*screenWidth/480*mdpi_card_width/4) /* low precision */
                + (4*screenWidth*mdpi_card_width/480/4) /* high precision */
              ) / 2;
    }

    // Calculate card HEIGHT
    //   Use integer math:  1.425 = 57/40, even number = /2*2
    //                      1.425 card height/width ratio -> even #
    HEIGHT = WIDTH * 57/40/2*2;

    SMALL_SPACING = 7 * dpi/160;
    HIDDEN_SPACING = 3 * dpi/160;
  }

  public Card(int value, int suit) {
    mValue = value;
    mSuit = suit;
    mX = 1;
    mY = 1;
  }

  public float GetX() { return mX; }
  public float GetY() { return mY; }
  public int GetValue() { return mValue; }
  public int GetSuit() { return mSuit; }

  public void SetPosition(float x, float y) {
    mX = x;
    mY = y;
  }

  public void MovePosition(float dx, float dy) {
    mX -= dx;
    mY -= dy;
  }
}


