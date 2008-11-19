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

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.view.WindowManager;


public class Options {

  public Options(final Solitaire solitaire, final DrawMaster drawMaster) {
    final int type = solitaire.GetSettings().getInt("LastType", Rules.SOLITAIRE);

    solitaire.setContentView(R.layout.options);

    // Display stuff
    final boolean bigCards = solitaire.GetSettings().getBoolean("DisplayBigCards", false);
    ((RadioButton)solitaire.findViewById(R.id.normal_cards)).setChecked(!bigCards);
    ((RadioButton)solitaire.findViewById(R.id.big_cards)).setChecked(bigCards);

    // Solitaire stuff
    final boolean dealThree = solitaire.GetSettings().getBoolean("SolitaireDealThree", true);
    final boolean styleNormal = solitaire.GetSettings().getBoolean("SolitaireStyleNormal", true);
    ((RadioButton)solitaire.findViewById(R.id.deal_3)).setChecked(dealThree);
    ((RadioButton)solitaire.findViewById(R.id.deal_1)).setChecked(!dealThree);
    ((RadioButton)solitaire.findViewById(R.id.style_normal)).setChecked(styleNormal);
    ((RadioButton)solitaire.findViewById(R.id.style_vegas)).setChecked(!styleNormal);

    // Spider stuff
    final int suits = solitaire.GetSettings().getInt("SpiderSuits", 4);
    ((RadioButton)solitaire.findViewById(R.id.suits_4)).setChecked(suits == 4);
    ((RadioButton)solitaire.findViewById(R.id.suits_2)).setChecked(suits == 2);
    ((RadioButton)solitaire.findViewById(R.id.suits_1)).setChecked(suits == 1);

    final Button accept = (Button) solitaire.findViewById(R.id.button_accept);
    accept.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        boolean commit = false;
        boolean newGame = false;
        SharedPreferences.Editor editor = solitaire.GetSettings().edit();

        if (bigCards != ((RadioButton)solitaire.findViewById(R.id.big_cards)).isChecked()) {
          editor.putBoolean("DisplayBigCards", !bigCards);
          commit = true;
          drawMaster.DrawCards(!bigCards);
        }

        if (dealThree != ((RadioButton)solitaire.findViewById(R.id.deal_3)).isChecked()) {
          editor.putBoolean("SolitaireDealThree", !dealThree);
          commit = true;
          if (type == Rules.SOLITAIRE) {
            newGame = true;
          }
        }
        
        if (styleNormal != ((RadioButton)solitaire.findViewById(R.id.style_normal)).isChecked()) {
          editor.putBoolean("SolitaireStyleNormal", !styleNormal);
          commit = true;
          if (type == Rules.SOLITAIRE) {
            newGame = true;
          }
        }

        int newSuits = 1;
        if (((RadioButton)solitaire.findViewById(R.id.suits_4)).isChecked()) {
          newSuits = 4;
        } else if (((RadioButton)solitaire.findViewById(R.id.suits_2)).isChecked()) {
          newSuits = 2;
        }

        if (newSuits != suits) {
          editor.putInt("SpiderSuits", newSuits);
          commit = true;
          if (type == Rules.SPIDER) {
            newGame = true;
          }
        }

        if (commit) {
          editor.commit();
        }
        if (newGame) {
          solitaire.NewOptions();
        } else {
          solitaire.CancelOptions();
        }
      }
    });
    final Button decline = (Button) solitaire.findViewById(R.id.button_cancel);
    decline.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        solitaire.CancelOptions();
      }
    });
  }
}

