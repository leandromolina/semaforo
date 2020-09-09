package com.cxm.android.semaforo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {

    private ArrayList<Button> controls;
    private ArrayList<Integer> path;
    private ArrayList<Integer> virtualPath;

    private Animation animationScaleUp;
    private Animation animationBlink;
    private Snackbar snackbar;
    private LinearLayout rootLayout;

    private static final int START_DIFFICULTY = 3;
    private int level = 1;
    private boolean lose = false;
    private int it = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ctrlRed = findViewById(R.id.button_red);
        Button ctrlYellow = findViewById(R.id.button_yellow);
        Button ctrlGreen = findViewById(R.id.button_green);
        Button ctrlBlue = findViewById(R.id.button_blue);
        Button ctrlViolet = findViewById(R.id.button_violet);

        controls = new ArrayList<>();
        controls.add(ctrlRed);
        controls.add(ctrlYellow);
        controls.add(ctrlGreen);
        controls.add(ctrlBlue);
        controls.add(ctrlViolet);

        rootLayout = findViewById(R.id.main);
        setup();
    }

    private void setup() {
        level = 1;
        lose = false;
        path = new ArrayList<>();
        virtualPath = new ArrayList<>();
        controls.get(4).setVisibility(View.GONE);
        disableControls();
        Random random = new Random();
        for (int i = 0; i < START_DIFFICULTY + level; i++) {
            path.add(random.nextInt(4));
        }
        printPath();
    }

    private void levelUp() {
        level++;
        if (level > 3){
            controls.get(4).setVisibility(View.VISIBLE);
        }
        path = new ArrayList<>();
        virtualPath = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < START_DIFFICULTY + level; i++) {
            path.add(random.nextInt(level > 3 ? 5 : 4));
        }
        printPath();
    }

    private void printPath() {
        it = 0;
        animationScaleUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_up);
        animationScaleUp.setAnimationListener(this);
        controls.get(path.get(it)).startAnimation(animationScaleUp);
    }

    private void loseGame() {
        if (!lose) {
            lose = true;
            disableControls();
            showSnackLose();
        }
    }


    public void onClickControl(View v) {
        int index = -1;
        Button button = (Button) v;
        switch (button.getId()){
            case R.id.button_red:
                index = 0;
                break;
            case R.id.button_yellow:
                index = 1;
                break;
            case R.id.button_green:
                index = 2;
                break;
            case R.id.button_blue:
                index = 3;
                break;
            case R.id.button_violet:
                index = 4;
                break;
        }

        virtualPath.add(index);
        if (virtualPath.get(virtualPath.size() - 1).equals(path.get(virtualPath.size() - 1)) && virtualPath.size() == path.size()){
            animateOnClick(index, true);
            disableControls();
            showSnackLevelUp();
        }else if (virtualPath.get(virtualPath.size() - 1).equals(path.get(virtualPath.size() - 1))){
            animateOnClick(index, true);
        }else {
            animateOnClick(index, false);
        }
    }

    public void animateOnClick(int idButton, boolean isGood) {
        if (isGood) {
            controls.get(idButton).clearAnimation();
            animationScaleUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_up);
            controls.get(idButton).startAnimation(animationScaleUp);
        } else {
            controls.get(idButton).clearAnimation();
            animationBlink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_blink);
            controls.get(idButton).startAnimation(animationBlink);
            loseGame();
        }
    }

    private void enableControls() {
        for (Button button : controls) {
            button.setEnabled(true);
            button.clearAnimation();
        }
    }

    private void disableControls() {
        for (Button button : controls) {
            button.setEnabled(false);
        }
    }

    private void showSnackLevelUp() {
        snackbar = Snackbar.make(rootLayout, "Nivel " + level + " completado.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Siguiente", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                levelUp();
            }
        });
        Vibrator vibrator = (Vibrator) getApplication().getSystemService(MainActivity.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(200);
        }
        snackbar.show();
    }

    private void showSnackLose() {
        snackbar = Snackbar.make(rootLayout, "Sorry! Camino incorrecto.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Reintentar", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                for (Button button : controls) {
                    button.clearAnimation();
                }
                setup();
            }
        });
        Vibrator vibrator = (Vibrator) getApplication().getSystemService(MainActivity.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(1000);
        }
        snackbar.show();
    }


    @Override
    public void onAnimationEnd(Animation animation) {
        it++;
        if (it < path.size()){
            animationScaleUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale_up);
            animationScaleUp.setAnimationListener(this);
            controls.get(path.get(it)).startAnimation(animationScaleUp);
        }else{
            enableControls();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
