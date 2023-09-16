package com.example.halloweenswheelgame.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.halloweenswheelgame.R;
import com.example.halloweenswheelgame.adapter.StatsAdapter;
import com.example.halloweenswheelgame.model.Stat;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halloweenswheelgame.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //wheel values
    final int[] sectors = {0, -1000, -2000, -3000, -4000, -5000, -10000, -5000, -4000, -3000, -2000, -1000, 0, 1000, 2000, 3000, 4000, 5000, 10000, 5000, 4000, 3000, 2000, 1000};
    //sector degrees variable
    final int[] sectorDegrees = new int[sectors.length];

    //random index from sectors
    int randomSectorIndex = 0;

    //more resources
    ImageView wheel;
    boolean spinning = false; //either spinning or not
    long totalPoints = 0; //earned points so far
    long minPointsPerDollar = 1000; //used for point conversion to real money

    //random to help generate random index etc
    Random random = new Random();

    //statistics recyclerview
    RecyclerView recyclerView;
    StatsAdapter statsAdapter; //adapter to hold stats
    List<Stat> stats = new ArrayList<>(); //list of score stats
    TextView statusTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize variables
        wheel = findViewById(R.id.wheel);
//        ImageView belt = findViewById(R.id.belt);
        TextView spinBtn = findViewById(R.id.spinBtn);
        TextView resetBtn = findViewById(R.id.resetBtn);
        statusTv = findViewById(R.id.status);
        TextView conversionTV = findViewById(R.id.conversion);
        String convText = formatPoints(minPointsPerDollar) + " points = 1.00 $";
        //set conversion note
        conversionTV.setText(convText);
        //generate sector degrees to use.
        generateSectorDegress();

        //generate sector degrees to use
        generateSectorDegress();

        //spin wheel when spin btn is clicked
        spinBtn.setOnClickListener(v -> {
            //only if not spinning
            if (!spinning) {
                //spin
                spin(); //create this method
                spinning = true; // now spinning
            }
        });

        //you can also spin when belt is clicked
//        belt.setOnClickListener(v -> spinBtn.performClick());
        //you can also spin when wheel is clicked
        wheel.setOnClickListener(v -> spinBtn.performClick());

        //reset game
        resetBtn.setOnClickListener(v -> resetGame());

        //initial the stats adapter
        initializeStatsAdapter();

        //convert points to money
        convertPoints();
    }

    private void convertPoints() {
        TextView convertBtn = findViewById(R.id.convertBtn);
        convertBtn.setOnClickListener(v -> {
            //only if erarned points worth a dollar or more
            if (totalPoints < minPointsPerDollar) {
                toast("Can't convert, Your points don't worth a dollar");
            } else {
                double money = (double)totalPoints / minPointsPerDollar;
                TextView earningsTv = findViewById(R.id.earnings);
                String earnings = "Earnings = $" + money;
                //show earnings
                earningsTv.setText(earnings);
                toast("Earnings: $" + money);
            }
        });
    }

    private void initializeStatsAdapter() {
        //recycler view and layout manager
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);  //scroll horizontally
        recyclerView.setLayoutManager(layoutManager);

        //default index wor and points word
        Stat stat = new Stat("Index", "Points");
        stats.add(stat);

        //adapter
        statsAdapter = new StatsAdapter(stats);
        recyclerView.setAdapter(statsAdapter);
    }

    private void resetGame() {
        //only if not spinning
        if (spinning) return;

        //reset below
        statusTv.setText("-");
        int size = stats.size();
        Stat defaultStat = stats.get(0); //keep the default stat
        //clear the stats list
        stats.clear();
        //notify adapter about the changes
        statsAdapter.notifyItemRangeRemoved(0, size);

        ///keep the default
        stats.add(defaultStat);
        statsAdapter.notifyItemInserted(0);

        //reset any wheel animation/spinning
        wheel.clearAnimation();
    }
    private void spin() {
        //reset any game status
        statusTv.setText("-");

        //get any random sector index
        randomSectorIndex = random.nextInt(sectors.length); //the bound is exclusive

        //generate a random degree to spin to

        int randomDegree = generateRandomDegreeToSpinTo();

        //do the actual spinning
        RotateAnimation rotateAnimation = new RotateAnimation(0, randomDegree, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        //duration of animation
        rotateAnimation.setDuration(3600);
        rotateAnimation.setFillAfter(true); //keeps the record

        //interpolator speed at start, slow down at end
        rotateAnimation.setInterpolator(new DecelerateInterpolator());

        //spinning listener
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //only interested when animation ends
                //get earned points
                int earnedPoints = sectors[sectors.length - (randomSectorIndex + 1)];

                //update/feed the stats adapter
                feedStatsAdapter(earnedPoints);
                calculateTotalPoints(earnedPoints);
                //end spinning
                spinning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //apply the animateion to our wheel
        wheel.startAnimation(rotateAnimation);
    }

    private void calculateTotalPoints(long points) {
        totalPoints = totalPoints + points;
        //show them
        TextView totalPointsTv = findViewById(R.id.totalPoints);
        totalPointsTv.setText(formatPoints(totalPoints));

        //game status won or lost?
        String status = "";

        if (points < 0) {
            status = "Lost";
            // toast a message
            toast("You have lost: " + points + " points.");
        }
        else if (points > 0) {
            status = "Win";
            toast("You have a win: " + points + " points.");
        }
        else {
            status = "Draw";
            toast("You have a draw: 0 points");
        }

        //show game status
        statusTv.setText(status);
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void feedStatsAdapter(long points) {
        //show the current earned value points
        int index = stats.size(); //the feeding index
        //stat
        Stat stat = new Stat(String.valueOf(index), formatPoints(points));
        stats.add(stat); //add stat to stats list

        //notify the adapter about the changes in list
        statsAdapter.notifyItemInserted(index);
        recyclerView.scrollToPosition(index); //make sure you show the latest index
    }

    private int generateRandomDegreeToSpinTo() {
        //let's return a higher degree as much as possible
        return (360 * sectors.length) + sectorDegrees[randomSectorIndex];
    }

    private void generateSectorDegress() {
        // for 1 sector
        int sectorDegree = 360 / sectors.length;

        //fill the sectorDegress array
        for(int i = 0; i < sectors.length; i++) {
            // let's make it higher as much as possible
            sectorDegrees[i] = (i + 1) * sectorDegree; //saving the degree at index i
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatPoints(long points) {
        return String.format("%,d", points);
    }
}