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

import android.os.Bundle;


class RulesFortyThieves extends Rules {

  public void Init(Bundle map) {
    mIgnoreEvents = true;

    mCardCount = 104;
    mCardAnchorCount = 20;
    mCardAnchor = new CardAnchor[mCardAnchorCount];

    // Anchor stacks
    for (int i = 0; i < 10; i++) {
      mCardAnchor[i] = CardAnchor.CreateAnchor(CardAnchor.GENERIC_ANCHOR, i, this);
      mCardAnchor[i].SetBuildSeq(GenericAnchor.SEQ_DSC);
      mCardAnchor[i].SetMoveSeq(GenericAnchor.SEQ_ASC);
      mCardAnchor[i].SetSuit(GenericAnchor.SUIT_SAME);
      mCardAnchor[i].SetWrap(false);
      mCardAnchor[i].SetPickup(GenericAnchor.PACK_LIMIT_BY_FREE);
      mCardAnchor[i].SetDropoff(GenericAnchor.PACK_LIMIT_BY_FREE);
      mCardAnchor[i].SetDisplay(GenericAnchor.DISPLAY_ALL);
    }
    // Bottom anchors for holding cards
    for (int i = 0; i < 8; i++) {
      mCardAnchor[i+10] = CardAnchor.CreateAnchor(CardAnchor.SEQ_SINK, i+10, this);
    }

    mCardAnchor[18] = CardAnchor.CreateAnchor(CardAnchor.DEAL_FROM, 18, this);
    mCardAnchor[19] = CardAnchor.CreateAnchor(CardAnchor.DEAL_TO, 19, this);

    if (map != null) {
      // Do some assertions, default to a new game if we find an invalid state
      if (map.getInt("cardAnchorCount") == 20 &&
          map.getInt("cardCount") == 104) {
        int[] cardCount = map.getIntArray("anchorCardCount");
        int[] hiddenCount = map.getIntArray("anchorHiddenCount");
        int[] value = map.getIntArray("value");
        int[] suit = map.getIntArray("suit");
        int cardIdx = 0;

        for (int i = 0; i < 20; i++) {
          for (int j = 0; j < cardCount[i]; j++, cardIdx++) {
            Card card = new Card(value[cardIdx], suit[cardIdx]);
            mCardAnchor[i].AddCard(card);
          }
          mCardAnchor[i].SetHiddenCount(hiddenCount[i]);
        }

        mIgnoreEvents = false;
        // Return here so an invalid save state will result in a new game
        return;
      }
    }

    mDeck = new Deck(2);
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 4; j++){
        mCardAnchor[i].AddCard(mDeck.PopCard());
      }
    }
    while (!mDeck.Empty()) {
      mCardAnchor[18].AddCard(mDeck.PopCard());
    }
    mIgnoreEvents = false;
  }

  public void Resize(int width, int height) {
    int rem = (width - (Card.WIDTH * 10)) / 10;
    for (int i = 0; i < 10; i++) {
      mCardAnchor[i].SetMaxHeight(height - 30 - Card.HEIGHT);
      mCardAnchor[i].SetPosition(rem/2 + i * (rem + Card.WIDTH), 30 + Card.HEIGHT);

      mCardAnchor[i+10].SetPosition(rem/2 + i * (rem + Card.WIDTH), 10);
    }

    // Setup edge cards (Touch sensor loses sensitivity towards the edge).
    mCardAnchor[0].SetLeftEdge(0);
    mCardAnchor[9].SetRightEdge(width);
    mCardAnchor[10].SetLeftEdge(0);
    mCardAnchor[19].SetRightEdge(width);
    for (int i = 0; i < 10; i++) {
      mCardAnchor[i].SetBottom(height);
    }
  }

  @Override
  public boolean Fling(MoveCard moveCard) {
    if (moveCard.GetCount() == 1) {
      CardAnchor anchor = moveCard.GetAnchor();
      Card card = moveCard.DumpCards(false)[0];
      for (int i = 0; i < 8; i++) {
        if (mCardAnchor[i+10].DropSingleCard(card)) {
          mEventPoster.PostEvent(EVENT_FLING, anchor, card);
          return true;
        }
      }
      anchor.AddCard(card);
    } else {
      moveCard.Release();
    }
    return false;
  }

  @Override
  public void EventProcess(int event, CardAnchor anchor, Card card) {
    if (mIgnoreEvents) {
      anchor.AddCard(card);
      return;
    }
    if (event == EVENT_FLING) {
      mWasFling = true;
      if (!TryToSinkCard(anchor, card)) {
        anchor.AddCard(card);
        mWasFling = false;
      }
    } else {
      anchor.AddCard(card);
    }
  }

  private boolean TryToSink(CardAnchor anchor) {
    Card card = anchor.PopCard();
    boolean ret = TryToSinkCard(anchor, card);
    if (!ret) {
      anchor.AddCard(card);
    }
    return ret;
  }

  private boolean TryToSinkCard(CardAnchor anchor, Card card) {
    for (int i = 0; i < 8; i++) {
      if (mCardAnchor[i+10].DropSingleCard(card)) {
        mAnimateCard.MoveCard(card, mCardAnchor[i+10]);
        mMoveHistory.push(new Move(anchor.GetNumber(), i+10, 1, false, false));
        return true;
      }
    }
    return false;
  }

  @Override
  public void EventProcess(int event, CardAnchor anchor) {
    if (mIgnoreEvents) {
      return;
    }
    if (event == EVENT_DEAL) {
      if (mCardAnchor[18].GetCount()>0){
        mCardAnchor[19].AddCard(mCardAnchor[18].PopCard());
        if (mCardAnchor[18].GetCount() == 0) {
          mCardAnchor[18].SetDone(true);
        }
        mMoveHistory.push(new Move(18, 19, 1, true, false));
      }
    } else if (event == EVENT_STACK_ADD) {
      if (anchor.GetNumber() >= 10 && anchor.GetNumber() < 18) {
        if (mCardAnchor[10].GetCount() == 13 && mCardAnchor[11].GetCount() == 13 &&
            mCardAnchor[12].GetCount() == 13 && mCardAnchor[13].GetCount() == 13 &&
            mCardAnchor[14].GetCount() == 13 && mCardAnchor[15].GetCount() == 13 &&
            mCardAnchor[16].GetCount() == 13 && mCardAnchor[17].GetCount() == 13) {
          SignalWin();
        } else {
          if (mAutoMoveLevel == AUTO_MOVE_ALWAYS ||
              (mAutoMoveLevel == AUTO_MOVE_FLING_ONLY && mWasFling)) {
            mEventPoster.PostEvent(EVENT_SMART_MOVE);
          } else {
            mView.StopAnimating();
            mWasFling = false;
          }
        }
      }
    }
  }

  @Override
  public void EventProcess(int event) {
    if (mIgnoreEvents == true) {
      return;
    }
    if (event == EVENT_SMART_MOVE) {
      for (int i = 0; i < 10; i++) {
        if (mCardAnchor[i].GetCount() > 0 &&
            TryToSink(mCardAnchor[i])) {
          return;
        }
      }
      mWasFling = false;
      mView.StopAnimating();
    }
  }

  @Override
  public int CountFreeSpaces() {
    int free = 0;
    for (int i = 0; i < 10; i++) {
      if (mCardAnchor[i].GetCount() == 0) {
        free++;
      }
    }
    return free;
  }

  @Override
  public String GetGameTypeString() {
    return "Forty Thieves";
  }
  @Override
  public String GetPrettyGameTypeString() {
    return "Forty Thieves";
  }

  @Override
  public boolean HasString() {
    return true;
  }

  @Override
  public String GetString() {
    int cardsLeft = mCardAnchor[18].GetCount();
    if (cardsLeft == 1) {
      return "1 card left";
    }
    return cardsLeft + " cards left";
  }

}
