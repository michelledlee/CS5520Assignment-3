package edu.neu.madcourse.michellelee.numad18s_michellelee;

import android.app.Fragment;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GameFragment extends Fragment {
    // Data structures go here...
    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9,};
    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};

    private Tile mEntireBoard = new Tile(this);
    private Tile mLargeTiles[] = new Tile[9];
    private Tile mSmallTiles[][] = new Tile[9][9];
    private Tile.Owner mPlayer = Tile.Owner.X;
    private Set<Tile> mAvailable = new HashSet<Tile>();
    private int mLastLarge;
    private int mLastSmall;

    // DICTIONARY  VARIABLES
    private HashSet<String> foundWords = new HashSet<String>();

    // SELECTED SMALL TILES
    private int tile1Int[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int tile2Int[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int tile3Int[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int tile4Int[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int tile5Int[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int tile6Int[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int tile7Int[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int tile8Int[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int tile9Int[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int allTilesInt[][] = {tile1Int, tile2Int, tile3Int, tile4Int, tile5Int, tile6Int, tile7Int, tile8Int, tile9Int};

    // PLAYER WORD GUESSES
    private StringBuilder tile1 = new StringBuilder();
    private StringBuilder tile2 = new StringBuilder();
    private StringBuilder tile3 = new StringBuilder();
    private StringBuilder tile4 = new StringBuilder();
    private StringBuilder tile5 = new StringBuilder();
    private StringBuilder tile6 = new StringBuilder();
    private StringBuilder tile7 = new StringBuilder();
    private StringBuilder tile8 = new StringBuilder();
    private StringBuilder tile9 = new StringBuilder();
    private StringBuilder allTiles[] = {tile1, tile2, tile3, tile4, tile5, tile6, tile7, tile8, tile9};

    // BUTTON LIST
    public Button buttonList[][] = new Button[9][9];

    // LIST VIEW SET UP
    private ListView wordListView;          // listview for words
    private ArrayList<String> itemList;     // word list
    private ArrayAdapter<String> adapter;   // string adapter

    // SOUND MANAGEMENT
    private int mSoundClick, mSoundCorrect, mSoundIncorrect;
    private SoundPool mSoundPool;
    private float mVolume = 1f;

    // SCOREBOARD
    private TextView scoreBoard;            // score board
    private int pointsScore;                // points

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);    // retain this fragment across configuration changes.
        initGame();
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundClick = mSoundPool.load(getActivity(), R.raw.click, 1);
        mSoundCorrect = mSoundPool.load(getActivity(), R.raw.correct, 1);
        mSoundIncorrect = mSoundPool.load(getActivity(), R.raw.wrong, 1);
        dictionary = new HashSet<String>();
        AssetManager assetM = getActivity().getAssets();                        // get asset manager to access dictionary file
        try {
            InputStream is = assetM.open("dictionary");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));   // read from input stream
            String line;
            while ((line = r.readLine()) != null) {     // while line is not null
                if (line.length() >= 3) {               // if the word is 3 letters long
                    dictionary.add(line);               // take only the 3 letter words
                }
            }
        } catch (IOException e) {
        }
    }

    private void clearAvailable() {
        mAvailable.clear();
    }

    private void addAvailable(Tile tile) {
        mAvailable.add(tile);
    }

    public boolean isAvailable(Tile tile) {
        return mAvailable.contains(tile);
    }

    HashSet<String> dictionary;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.large_board, container, false);
        initViews(rootView);
        updateAllTiles();


        return rootView;
    }

    /**
     * Initialize the board with words
     * @param rootView the board layout
     */
    private void initViews(View rootView) {
        mEntireBoard.setView(rootView);                             // set view outer board

        // CREATE WORD BANK
        final ArrayList<String> wordBank = createWordBank();        // 9 letter word bank

        // INITIALIZE THE BOARDS
        for (int large = 0; large < 9; large++) {                   // intitialize each 1/9 board
            View outer = rootView.findViewById(mLargeIds[large]);   // get view for inner board
            mLargeTiles[large].setView(outer);                      // set view for 1/9 board
            int r = (int) (Math.random() * wordBank.size());        // get random number of index in wordBank
            String word = wordBank.get(r);                          // get word from wordBank
//            Log.e("word = ", word);
            char[] shuffled = shuffle(word).toCharArray();          // shuffle the letters of the word

            for (int small = 0; small < 9; small++) {                                   // small boards within the large board
                final Button inner = (Button) outer.findViewById(mSmallIds[small]);     // initialize the button
                char letter = Character.toUpperCase(shuffled[small]);            // get current letter in word
                inner.setText(""+letter);                                           // set text of the button to be the current letter
                final Tile smallTile = mSmallTiles[large][small];                // small tile within large tile
                buttonList[large][small] = inner;                                // put this button into the button array

                smallTile.setView(inner);           // set view of the inner tile to be the button view
                smallTile.setLargeTileNo(large);    //
                smallTile.setSmallTileNo(small);    //
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // make a sound when clicked
                        mSoundPool.play(mSoundClick, mVolume, mVolume, 1, 0, 1f);

                        // get tile coordinates
                        int largeI = smallTile.getLargeTileNo();
//                        Log.e("large tile no", String.valueOf(largeI));
                        int smallI = smallTile.getSmallTileNo();
//                        Log.e("small tile no", String.valueOf(smallI));

                        // change background color
                        inner.setSelected(!inner.isSelected());
                        if (inner.isSelected()) {
                            // selected state change
                            inner.setBackgroundResource(R.drawable.tile_blue);  // selected background turns blue
                            String letter = (inner.getText()).toString();
//                            Log.e("letter", String.valueOf(letter));
                            allTiles[largeI].append(letter);                    // add new letter selected to current string
//                            Log.e("character appended", String.valueOf(letter));
                            allTilesInt[largeI][smallI] = 1;                    // set background highlighted

                        } else {
                            // de-selected state change
                            inner.setBackgroundResource(R.drawable.tile_gray);          // deselected background is grey
                            String letter = (inner.getText()).toString();               // get letter from inner tile clicked
//                            Log.e("letter", String.valueOf(letter));
                            allTiles[largeI].deleteCharAt(allTiles[largeI].length()-1); // remove most recently selected
                            allTilesInt[largeI][smallI] = 0;                            // deselect background highlighted
                        }
                    }
                }
                );}

        }

        // INITIALIZE SUBMISSION BUTTONS FOR CHECKING
        Button b1 = (Button) rootView.findViewById(R.id.B1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWord(1);
            }
        });
        Button b2 = (Button) rootView.findViewById(R.id.B2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWord(2);
            }
        });
        Button b3 = (Button) rootView.findViewById(R.id.B3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWord(3);
            }
        });
        Button b4 = (Button) rootView.findViewById(R.id.B4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWord(4);
            }
        });
        Button b5 = (Button) rootView.findViewById(R.id.B5);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWord(5);
            }
        });
        Button b6 = (Button) rootView.findViewById(R.id.B6);
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWord(6);
            }
        });
        Button b7 = (Button) rootView.findViewById(R.id.B7);
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWord(7);
            }
        });
        Button b8 = (Button) rootView.findViewById(R.id.B8);
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWord(8);
            }
        });
        Button b9 = (Button) rootView.findViewById(R.id.B9);
        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkWord(9);
            }
        });

        // SETTING UP LIST VIEW
        wordListView = (ListView) rootView.findViewById(R.id.accepted_words);
        itemList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.word_list_row, R.id.listRowTextView, itemList);
        wordListView.setAdapter(adapter);

        // SETTING UP SCOREBOARD
        scoreBoard = (TextView) rootView.findViewById(R.id.score);
    }

    /**
     * Create word bank for 9 letter words
     * @return array list with all the 9 letter words
     */
    public ArrayList<String> createWordBank() {
        ArrayList<String> wordBank = new ArrayList<String>();
        AssetManager assetM = getActivity().getAssets();                        // get asset manager to access dictionary file
        try {
            InputStream is = assetM.open("dictionary");                // open input stream to read dictionary
            BufferedReader r = new BufferedReader(new InputStreamReader(is));   // read from input stream
            String line;
            while ((line = r.readLine()) != null) {     // while line is not null
                if (line.length() == 9) {               // if the word is 9 letters long
                    wordBank.add(line);                 // take only the 9 letter words
                }
            }
        } catch (IOException e) {
        }
        return wordBank;
    }

    /**
     * Create look up dictionary for all words > 3 letters
     */
    public HashSet<String> createDictionary() {
        HashSet<String> dictionary = new HashSet<String>();
        AssetManager assetM = getActivity().getAssets();                        // get asset manager to access dictionary file
        try {
            InputStream is = assetM.open("dictionary");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));   // read from input stream
            String line;
            while ((line = r.readLine()) != null) {     // while line is not null
                if (line.length() >= 3) {               // if the word is 3 letters long
                    dictionary.add(line);               // take only the 3 letter words
                }
            }
        } catch (IOException e) {
        }
        return dictionary;
    }

    /**
     * If a guess is greater than or equal to three words, the dictionary is checked
     * to see if the word is in there. If the word is not in the found words set, the
     * word is added.
     *
     * @param largeTile the large tile for the word to check
     */
    public void checkWord(int largeTile) {
        String guess = "";
        int length = 0;
        int score = 0;
        boolean correct = false;                    // flag for whether the word is valid

        // get word string based on which button was pressed
        switch(largeTile) {
            case 1:
                guess = tile1.toString();

                break;
            case 2:
                guess = tile2.toString();
                break;
            case 3:
                guess = tile3.toString();
                break;
            case 4:
                guess = tile4.toString();
                break;
            case 5:
                guess = tile5.toString();
                break;
            case 6:
                guess = tile6.toString();
                break;
            case 7:
                guess = tile7.toString();
                break;
            case 8:
                guess = tile8.toString();
                break;
            case 9:
                guess = tile9.toString();
                break;
            default:
                guess = "";
        }
//        Log.e("guess= ", guess);
        length = guess.length();
//        Log.e("dictionary length = ", Integer.toString(dictionary.size()));

        // check if valid word
        if (length >= 3) {                  // check if the length of the word is >= 3
//            Log.e("length= ", Integer.toString(guess.length()));
            if (dictionary.contains(guess.toLowerCase())) {       // check if the dictionary contains the word
                foundWords.add(guess);              // add to set
                correct = true;                     // true if found
//                Log.e("correct", "= true");
                pointsScore += length;
                scoreBoard.setText(""+pointsScore);
            }
        } else {
            correct = false;                        // false if not found
//            Log.e("correct", "= false");
        }

        // if word is in the dictionary
        if (correct) {
            mSoundPool.play(mSoundCorrect, mVolume, mVolume, 1, 0, 1f);    // chime when correct
            adapter.add(guess);         // add to list
//            Log.e("word", String.valueOf(tile1));

            // set unused tiles to be blank
            for (int small = 0; small < 9; small++) {           // go through entire list
//                Log.e("tile # ", Integer.toString(small));
//                Log.e("selected? ", allTilesInt[largeTile-1][small] == 0 ? "no" : "yes");
                if (allTilesInt[largeTile-1][small] == 0) {     // if the tile was not selected
                    buttonList[largeTile-1][small].setText(""); // remove the letter from the button
                } else {
                    continue;
                }
            }

        // if word was not in the dictionary
        } else {
            mSoundPool.play(mSoundIncorrect, mVolume, mVolume, 1, 0, 1f);   // quack when incorrect
        }
    }

    /**
     * Shuffle the word pulled from the word bank
     * @param text word from the word bank to be shuffled
     * @return the shuffled string
     */
    public static String shuffle(String text) {
        char[] characters = text.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = (int)(Math.random() * characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }

//    private void switchTurns() {
//        mPlayer = mPlayer == Tile.Owner.X ? Tile.Owner.O : Tile
//                .Owner.X;
//    }
//
//    /**
//     * Set owner of small tile to current player
//     * @param large index of large tile being moved
//     * @param small index of small tile being moved
//     */
//    private void makeMove(int large, int small) {
//        mLastLarge = large;
//        mLastSmall = small;
//        Tile smallTile = mSmallTiles[large][small];
//        Tile largeTile = mLargeTiles[large];
//        smallTile.setOwner(mPlayer);                // set owner of small tile to current player
//        setAvailableFromLastMove(small);
//        Tile.Owner oldWinner = largeTile.getOwner();
//        Tile.Owner winner = largeTile.findWinner(); // see if there's a winner for the board containing the small tile
//        if (winner != oldWinner) {
//            largeTile.setOwner(winner);
//        }
//        winner = mEntireBoard.findWinner();         // see if someone has won the entire board
//        mEntireBoard.setOwner(winner);
//        updateAllTiles();
//        if (winner != Tile.Owner.NEITHER) {         // if someone has won
//            ((GameActivity)getActivity()).reportWinner(winner);
//        }
//    }

//    /**
//     * Redraw all boards
//     */
//    public void restartGame() {
//        initGame();
//        initViews(getView());
//        updateAllTiles();
//
//    }


//                                    // REPLACE???
////                        if (isAvailable(smallTile)) {
////                            makeMove(fLarge, fSmall);
////                            switchTurns();
////                        }
    /**
     * Sets up data structures
     */
    public void initGame() {
        Log.d("UT3", "init game");
        mEntireBoard = new Tile(this);
        // Create all the tiles
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large] = new Tile(this);
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small] = new Tile(this);
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);

        // If the player moves first, set which spots are available
        mLastSmall = -1;
        mLastLarge = -1;
        setAvailableFromLastMove(mLastSmall);
    }

    /**
     * Clears the available list and then marks unoccupied tiles
     * at the available large board as available
     * @param small
     */
    private void setAvailableFromLastMove(int small) {
        clearAvailable();
        // Make all the tiles at the destination available
        if (small != -1) {
            for (int dest = 0; dest < 9; dest++) {
                Tile tile = mSmallTiles[small][dest];
                if (tile.getOwner() == Tile.Owner.NEITHER)
                    addAvailable(tile);
            }
        }
        // If there were none available, make all squares available
        if (mAvailable.isEmpty()) {
            setAllAvailable();
        }
    }

    /**
     * If there are no possible moves, mark all unoccupied tiles as available
     */
    private void setAllAvailable() {
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                Tile tile = mSmallTiles[large][small];
                if (tile.getOwner() == Tile.Owner.NEITHER)
                    addAvailable(tile);
            }
        }
    }

    /**
     * Updates the state of the tile representing the overall board,
     * then each of the large tiles for the first 9 level boards,
     * then the small tiles that contain the letters
     */
    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large].updateDrawableState();
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small].updateDrawableState();
            }
        }
    }

    /**
     * Create a string containing the state of the game.
     * @return
     */
    public String getState() {
        StringBuilder builder = new StringBuilder();
        builder.append(mLastLarge);
        builder.append(',');
        builder.append(mLastSmall);
        builder.append(',');
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                builder.append(mSmallTiles[large][small].getOwner().name());
                builder.append(',');
            }
        }
        return builder.toString();
    }

    /**
     * Restore the state of the game from the given string.
     * @param gameData
     */
    public void putState(String gameData) {
        String[] fields = gameData.split(",");
        int index = 0;
        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);
        for (int large = 0; large < 9; large++) {
            for (int small = 0; small < 9; small++) {
                Tile.Owner owner = Tile.Owner.valueOf(fields[index++]);
                mSmallTiles[large][small].setOwner(owner);
            }
        }
        setAvailableFromLastMove(mLastSmall);
        updateAllTiles();
    }
}
