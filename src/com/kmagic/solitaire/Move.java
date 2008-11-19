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


public class Move {
  private int mFrom;
  private int mToBegin;
  private int mToEnd;
  private int mCount;
  private boolean mInvert;
  private boolean mUnhide;

  public Move() {
    mFrom = -1;
    mToBegin = -1;
    mToEnd = -1;
    mCount = 0;
    mInvert = false;
    mUnhide = false;
  }
  public Move(Move move) {
    mFrom = move.mFrom;
    mToBegin = move.mToBegin;
    mToEnd = move.mToEnd;
    mCount = move.mCount;
    mInvert = move.mInvert;
    mUnhide = move.mUnhide;
  }
  public Move(int from, int toBegin, int toEnd, int count, boolean invert,
              boolean unhide) {
    mFrom = from;
    mToBegin = toBegin;
    mToEnd = toEnd;
    mCount = count;
    mInvert = invert;
    mUnhide = unhide;
  }

  public Move(int from, int to, int count, boolean invert,
              boolean unhide) {
    mFrom = from;
    mToBegin = to;
    mToEnd = to;
    mCount = count;
    mInvert = invert;
    mUnhide = unhide;
  }

  public int GetFrom() { return mFrom; }
  public int GetToBegin() { return mToBegin; }
  public int GetToEnd() { return mToEnd; }
  public int GetCount() { return mCount; }
  public boolean GetInvert() { return mInvert; }
  public boolean GetUnhide() { return mUnhide; }
}
