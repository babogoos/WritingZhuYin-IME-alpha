/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.tcime.unofficial;

import android.content.Context;
import android.util.Log;
import android.provider.UserDictionary;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * Reads a phrase dictionary and provides following-word suggestions as a list
 * of characters for the given character.
 */
public class PhraseDictionary {

  private static final int APPROX_DICTIONARY_SIZE = 131072;

  private final CountDownLatch loading = new CountDownLatch(1);
  private final DictionaryLoader loader;
  private Context mContext;
  private ContentObserver mObserver;
  private boolean mRequiresReload = true;
  private static final String[] QUERY_PROJECTION = {
      UserDictionary.Words._ID,
      UserDictionary.Words.WORD
  };
  private HashMap<Character, StringBuilder> userDic;

  public PhraseDictionary(Context context) {
    mContext = context;
    loader = new DictionaryLoader(
        context.getResources().openRawResource(R.raw.dict_phrases),
        APPROX_DICTIONARY_SIZE, loading);
    new Thread(loader).start();
    
    ContentResolver cres = context.getContentResolver();     
    cres.registerContentObserver(UserDictionary.Words.CONTENT_URI, true, mObserver = new ContentObserver(null) {
    	@Override
    	public void onChange(boolean self) {
    		// We need to reload the user dictionary
    		mRequiresReload = true;
    	}
    });
    userDic = new HashMap<Character, StringBuilder>();
    if(mRequiresReload) getUserDictionary();
  }

  public synchronized void close() {
	if (mObserver != null) {
		mContext.getContentResolver().unregisterContentObserver(mObserver);
		mObserver = null;
	}
  }

  /**
   * Returns a string containing the following-word suggestions of phrases for
   * the given word.
   * 
   * @param c the current word to look for its following words of phrases.
   * @return a concatenated string of characters, or an empty string if there
   *     is no following-word suggestions for that word.
   */
  public String getFollowingWords(char c) {
	StringBuilder candidateWord = new StringBuilder("");
    try {
      loading.await();
    } catch (InterruptedException e) {
      Log.e("PhraseDictionary", "Loading is interrupted: ", e);
    }
    
    // Check the user dictionary
    if(mRequiresReload) getUserDictionary();
    if(userDic.containsKey(c)){
    	candidateWord.append(userDic.get(c).toString());
    }
    
    // Phrases are stored in an array consisting of three character arrays. 
    // char[0][] contains a char[] of words to look for phrases.
    // char[2][] contains a char[] of following words for char[0][].
    // char[1][] contains offsets of char[0][] words to map its following words. 
    // For example, there are 5 phrases: Aa, Aa', Bb, Bb', Cc.
    // char[0][] { A, B, C }
    // char[1][] { 0, 2, 4 }
    // char[2][] { a, a', b, b', c}
    char[][] dictionary = loader.result();
    if (dictionary == null || dictionary.length != 3) {
      return candidateWord.toString();
    }

    int index = Arrays.binarySearch(dictionary[0], c);
    if (index >= 0) {
      int offset = dictionary[1][index];
      int count = (index < dictionary[1].length - 1) ?
          (dictionary[1][index + 1] - offset) : (dictionary[2].length - offset);
      String result = String.valueOf(dictionary[2], offset, count);
      // Delete the words that already appear in the phrase file
      // = Shift these words to the front position
      if(candidateWord.length() > 0){
    	  result = result.replaceAll("[" + candidateWord.toString() + "]", "");
      }
      candidateWord.append(result);
    }
    return candidateWord.toString();
  }
  
  /**
   * Load the content of user dictionary
   */
  private synchronized void getUserDictionary(){
	// Use ContentResolver to query the user dictionary
	Cursor cursor = mContext.getContentResolver()
		.query(UserDictionary.Words.CONTENT_URI, QUERY_PROJECTION, "(locale IS NULL) or (locale=?)", 
		new String[] { Locale.getDefault().toString() }, null);
	
	try {
		if (cursor == null)
			throw new ClassNotFoundException("getUserDictionary failed!");

		if (cursor.moveToFirst()) {
			userDic.clear();
			StringBuilder node;
	        while (!cursor.isAfterLast()) {
	            String word = cursor.getString(1);
	            int length = word.length();
	            if(length < 2) break; // 1 word is nonsense
	            // Recursive to build the phrase table for 3+word sentences
	            // Example: ABCD -> AB, BC, CD
	            for(int i = 0; i < length - 1; ++i){
		            char index = word.charAt(i);
		            if(userDic.containsKey(index)){
		            	node = userDic.get(index);
		            	node.append(word.substring(i + 1, i + 2));
		            }else{
		            	node = new StringBuilder(word.substring(i + 1, i + 2));
		            	userDic.put(index, node);
		            }
	            }
	            Log.d("UserDic", "Entry " + word.charAt(0) + ": " + word.substring(1));
	            cursor.moveToNext();
	        }
	        cursor.close();
	    }
	} catch(Exception e) {
		Log.e("UserDic", e.getMessage());
	} finally {
		mRequiresReload = false;
	}
  }
}
