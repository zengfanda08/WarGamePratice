package com.ltb.laer.waterview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class WarGameActivity extends AppCompatActivity {

    private ImageView mPhone, mGirlAvator, mBoyAvator, mLeftReady, mRightReady, mLeftStart, mRightStart;
    private TextView mLeftScore, mRightScore, mLeftCountDown1, mLeftCountDown2, mLeftCountDown3, mRightCountDown1, mRightCountDown2, mRightCountDown3;

    private RandomLayout<Integer> leftRandomLayout ,rightRadomLayout;
    private boolean mLeftReadyFlag, mRightReadyFlag;

    private AnimatorSet mCountDownAnimatorSet;

    private int leftScore,rightScore ;

    private MyHandler myHandler = new MyHandler(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.activity_war_game);

        leftRandomLayout = findViewById(R.id.left_randomLayout);
        leftRandomLayout.setLayoutParams(new ConstraintLayout.LayoutParams((int) (Utils.getScreenWidth(this) / 2), ConstraintLayout.LayoutParams.MATCH_PARENT));
        leftRandomLayout.setOnRandomItemClickListener(new RandomLayout.OnRandomItemClickListener<Integer>() {
            @Override
            public void onRandomItemClick(View view, Integer integer) {
                Toast.makeText(WarGameActivity.this, integer + "", Toast.LENGTH_SHORT).show();
                leftRandomLayout.removeAllRandomView();
                generateImageView();
                leftScore++;
                mLeftScore.setText(String.valueOf(leftScore));
            }
        });

        rightRadomLayout = findViewById(R.id.right_randomLayout);
        rightRadomLayout.setLayoutParams(new ConstraintLayout.LayoutParams((int) (Utils.getScreenWidth(this)), ConstraintLayout.LayoutParams.MATCH_PARENT));
        rightRadomLayout.setLayoutOrientation(RandomLayout.ORIENTATION_RIGHT);
        rightRadomLayout.setOnRandomItemClickListener(new RandomLayout.OnRandomItemClickListener<Integer>() {
            @Override
            public void onRandomItemClick(View view, Integer integer) {
                Toast.makeText(WarGameActivity.this, integer + "", Toast.LENGTH_SHORT).show();
                rightRadomLayout.removeAllRandomView();
                generateImageView();
                rightScore++;
                mRightScore.setText(String.valueOf(rightScore));
            }
        });

        generateImageView();

        mPhone = findViewById(R.id.iv_phone);
        mGirlAvator = findViewById(R.id.iv_girl_avator);
        mBoyAvator = findViewById(R.id.iv_boy_avator);
        mLeftStart = findViewById(R.id.iv_left_start);
        mRightStart = findViewById(R.id.iv_right_start);
        mLeftReady = findViewById(R.id.iv_left_ready);
        mRightReady = findViewById(R.id.iv_right_ready);
        mLeftScore = findViewById(R.id.tv_left_score);
        mRightScore = findViewById(R.id.tv_right_score);
        mLeftCountDown1 = findViewById(R.id.tv_left_countdown1);
        mLeftCountDown2 = findViewById(R.id.tv_left_countdown2);
        mLeftCountDown3 = findViewById(R.id.tv_left_countdown3);
        mRightCountDown1 = findViewById(R.id.tv_right_countdown1);
        mRightCountDown2 = findViewById(R.id.tv_right_countdown2);
        mRightCountDown3 = findViewById(R.id.tv_right_countdown3);

        mLeftScore.setTypeface(getArialBlack(this));
        mRightScore.setTypeface(getArialBlack(this));
        mLeftCountDown1.setTypeface(getArialBlack(this));
        mLeftCountDown2.setTypeface(getArialBlack(this));
        mLeftCountDown3.setTypeface(getArialBlack(this));
        mRightCountDown1.setTypeface(getArialBlack(this));
        mRightCountDown2.setTypeface(getArialBlack(this));
        mRightCountDown3.setTypeface(getArialBlack(this));

        initReadyAnimation();
        initCountDownAnimation();
        initAnimation();
        initListeners();
    }

    private void generateImageView() {
        for (int i = 0; i < 10; i++) {
            setLeftTableView(i);
            setRightTableView(i);
        }
    }

    private void initCountDownAnimation() {
        mLeftCountDown1.animate().rotation(90f);
        mLeftCountDown2.animate().rotation(90f);
        mLeftCountDown3.animate().rotation(90f);
        mRightCountDown1.animate().rotation(-90f);
        mRightCountDown2.animate().rotation(-90f);
        mRightCountDown3.animate().rotation(-90f);
//        myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startLeftCountDownAnimation(mLeftCountDown3,3);
//            }
//        }, 1000);
    }

    private void initReadyAnimation() {
        mLeftReady.setImageBitmap(ImageUtils.rotateBitmap(mLeftReady, 90));
        mRightReady.setImageBitmap(ImageUtils.rotateBitmap(mRightReady, -90));

        mLeftStart.setImageBitmap(ImageUtils.rotateBitmap(mLeftStart, 90));
        mRightStart.setImageBitmap(ImageUtils.rotateBitmap(mRightStart, -90));
    }

    private void startLeftCountDownAnimation(final TextView countDownView, final int curPosition) {
        countDownView.setVisibility(View.VISIBLE);

        float x = countDownView.getX();
        float y = countDownView.getY();
//        ObjectAnimator translation = ObjectAnimator.ofFloat(countDownView, "translationX", countDownView.getTranslationX(), (Utils.getScreenWidth(this) / 2) - countDownView.getTranslationX());
        float width = Utils.getScreenWidth(this);
        float height = Utils.getScreenHeight(this);
        float translationX = countDownView.getRight();

        countDownView.animate().scaleX(1.5f).scaleY(1.5f).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator translation = ObjectAnimator.ofFloat(countDownView, "translationX", Utils.dpToPixel(190));
                translation.setInterpolator(new DecelerateInterpolator());
                ObjectAnimator alpha = ObjectAnimator.ofFloat(countDownView, "alpha", 1, 0);
                ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(countDownView, "scaleX", 1.5f, 1f);
                ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(countDownView, "scaleY", 1.5f, 1f);

                mCountDownAnimatorSet = new AnimatorSet();
                mCountDownAnimatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        countDownView.setVisibility(View.GONE);
                        if (curPosition == 3) {
                            startLeftCountDownAnimation(mLeftCountDown2, 2);
                        } else if (curPosition == 2) {
                            startLeftCountDownAnimation(mLeftCountDown1, 1);
                        } else if (curPosition == 1) {
                            // 开始 start 动画
                            mLeftStart.setVisibility(View.VISIBLE);
                            mLeftStart.animate().scaleX(1.5f).scaleY(1.5f).setDuration(1200).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mLeftStart.setVisibility(View.GONE);
                                    leftRandomLayout.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                });
                mCountDownAnimatorSet.setDuration(1500).play(translation).with(alpha).with(scaleX2).with(scaleY2);
                mCountDownAnimatorSet.start();
            }
        });
    }

    private void startRightCountDownAnimation(final TextView countDownView, final int curPosition) {
        countDownView.setVisibility(View.VISIBLE);

        countDownView.animate().scaleX(1.5f).scaleY(1.5f).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator translation = ObjectAnimator.ofFloat(countDownView, "translationX", -Utils.dpToPixel(190));
                translation.setInterpolator(new DecelerateInterpolator());
                ObjectAnimator alpha = ObjectAnimator.ofFloat(countDownView, "alpha", 1, 0);
                ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(countDownView, "scaleX", 1.5f, 1f);
                ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(countDownView, "scaleY", 1.5f, 1f);

                mCountDownAnimatorSet = new AnimatorSet();
                mCountDownAnimatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        countDownView.setVisibility(View.GONE);
                        if (curPosition == 3) {
                            startRightCountDownAnimation(mRightCountDown2, 2);
                        } else if (curPosition == 2) {
                            startRightCountDownAnimation(mRightCountDown1, 1);
                        } else if (curPosition == 1) {
                            // 开始 start 动画
                            mRightStart.setVisibility(View.VISIBLE);
                            mRightStart.animate().scaleX(1.5f).scaleY(1.5f).setDuration(1200).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mRightStart.setVisibility(View.GONE);
                                    rightRadomLayout.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                });
                mCountDownAnimatorSet.setDuration(1500).play(translation).with(alpha).with(scaleX2).with(scaleY2);
                mCountDownAnimatorSet.start();
            }
        });
    }


    private void initListeners() {
        mLeftReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLeftReady.clearAnimation();
                if (mRightReadyFlag) {
                    myHandler.sendEmptyMessageDelayed(0, 500);
                }
                mLeftReadyFlag = true;
                mLeftReady.setImageResource(R.mipmap.icon_ready_pressed);
                mLeftReady.setImageBitmap(ImageUtils.rotateBitmap(mLeftReady, 90));
            }
        });

        mRightReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRightReady.clearAnimation();
                if (mLeftReadyFlag) {
                    myHandler.sendEmptyMessageDelayed(0, 500);
                }
                mRightReadyFlag = true;
                mRightReady.setImageResource(R.mipmap.icon_ready_pressed);
                mRightReady.setImageBitmap(ImageUtils.rotateBitmap(mRightReady, -90));
            }
        });
    }

    /*开始游戏倒计时动画*/
    private void startCountDownAnimation() {
        mRightReady.setVisibility(View.GONE);
        mLeftReady.setVisibility(View.GONE);

        mLeftScore.animate().rotation(90f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLeftScore.setVisibility(View.VISIBLE);
                // 开始左边3秒倒计时
                startLeftCountDownAnimation(mLeftCountDown3, 3);
            }
        });
        mRightScore.animate().rotation(-90f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRightScore.setVisibility(View.VISIBLE);
                // 开始右边3秒倒计时
                startRightCountDownAnimation(mRightCountDown3, 3);
            }
        });
        ;
    }

    private void initAnimation() {
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(mPhone, "rotation", 0f, 180f, 0f);
        rotationAnimator.setDuration(3000);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.setRepeatCount(1);
        rotationAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPhone.animate().scaleX(0).scaleY(0).setDuration(500).setStartDelay(2000);
                mGirlAvator.animate().scaleX(0).scaleY(0).setDuration(500).setStartDelay(2000).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Animation leftScaleAnimation = AnimationUtils.loadAnimation(WarGameActivity.this, R.anim.button_scale);
                        mLeftReady.startAnimation(leftScaleAnimation);
                        mLeftReady.setVisibility(View.VISIBLE);
                    }
                });
                mBoyAvator.animate().scaleX(0).scaleY(0).setDuration(500).setStartDelay(2000).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Animation rightScaleAnimation = AnimationUtils.loadAnimation(WarGameActivity.this, R.anim.button_scale);
                        mRightReady.startAnimation(rightScaleAnimation);
                        mRightReady.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        rotationAnimator.start();
    }

    public static Typeface getArialBlack(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/ArialBlack.ttf");
    }

    // 静态自定义 Handler 内部类
    private static class MyHandler extends Handler {

        private WeakReference<WarGameActivity> mReference;

        public MyHandler(WarGameActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // 更新UI等操作
            if (mReference.get() != null) {
                switch (msg.what) {
                    case 0:
                        mReference.get().startCountDownAnimation();
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }
        }
    }

    public void setLeftTableView(int pos) {
        ImageView imageView = (ImageView) LayoutInflater.from(this).inflate(R.layout.war_game_image_layout, null);
        imageView.setImageResource(R.mipmap.fish);
        imageView.setLayoutParams(new LinearLayout.LayoutParams((int) Utils.dpToPixel(70), (int) Utils.dpToPixel(70)));
        imageView.setImageBitmap(ImageUtils.rotateBitmap(imageView, 90));
        leftRandomLayout.addViewAtRandomXY(imageView, pos);
    }

    public void setRightTableView(int pos) {
        ImageView imageView = (ImageView) LayoutInflater.from(this).inflate(R.layout.war_game_image_layout, null);
        imageView.setImageResource(R.mipmap.test1);
        imageView.setLayoutParams(new LinearLayout.LayoutParams((int) Utils.dpToPixel(70), (int) Utils.dpToPixel(70)));
        imageView.setImageBitmap(ImageUtils.rotateBitmap(imageView, -90));
        rightRadomLayout.addViewAtRandomXY(imageView, pos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
        // 清除所有动画

    }
}
