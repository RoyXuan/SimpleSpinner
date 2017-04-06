package com.vest.spinnerlibrary;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @author angelo.marchesin
 */
@SuppressWarnings("unused")
public class SimpleSpinner extends TextView {

    private static final int MAX_LEVEL = 10000;
    private static final int DEFAULT_ELEVATION = 16;
    private static final String INSTANCE_STATE = "instance_state";
    private static final String SELECTED_INDEX = "selected_index";
    private static final String IS_POPUP_SHOWING = "is_popup_showing";
    public boolean isShow = false;
    private boolean isOnlyText = false;
    private int mSelectedIndex = -1;
    private Drawable mDrawable;
    private PopupWindow mPopup;
    private RecyclerView mListView;
    private SimpleSpinnerBaseAdapter mAdapter;
    private boolean mHideArrow;

    public PopupWindow getPopup() {
        return mPopup;
    }

    @SuppressWarnings("ConstantConditions")
    public SimpleSpinner(Context context) {
        super(context);
        init(context, null);
    }

    public SimpleSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SimpleSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(SELECTED_INDEX, mSelectedIndex);

        if (mPopup != null) {
            bundle.putBoolean(IS_POPUP_SHOWING, mPopup.isShowing());
            dismissDropDown();
        }

        return bundle;
    }


    @Override
    public void onRestoreInstanceState(Parcelable savedState) {
        if (savedState instanceof Bundle) {
            Bundle bundle = (Bundle) savedState;

            mSelectedIndex = bundle.getInt(SELECTED_INDEX);

            if (mAdapter != null) {
                setText(mAdapter.getItemInDataSet(mSelectedIndex));
//                mAdapter.notifyItemSelected(mSelectedIndex);
            }

            if (bundle.getBoolean(IS_POPUP_SHOWING)) {
                if (mPopup != null) {
                    // Post the show request into the looper to avoid bad token exception
                    post(new Runnable() {
                        @Override
                        public void run() {
                            showDropDown();
                        }
                    });
                }
            }

            savedState = bundle.getParcelable(INSTANCE_STATE);
        }

        super.onRestoreInstanceState(savedState);
    }

    public boolean isOnlyText() {
        return isOnlyText;
    }

    public void setOnlyText(boolean onlyText) {
        if (onlyText) setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        else setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawable, null);
        isOnlyText = onlyText;
    }

    private void init(Context context, AttributeSet attrs) {
        Resources resources = getResources();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleSpinner);
        int defaultPadding = resources.getDimensionPixelSize(R.dimen.one_and_a_half_grid_unit);
//        setBackgroundResource(R.drawable.);
        setTextColor(resources.getColor(R.color.text_dark_color));
        setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        setPadding(0, defaultPadding, defaultPadding, defaultPadding);
        setClickable(true);
        setBackgroundResource(R.drawable.selector);
        if (isOnlyText) {
            typedArray.recycle();
            return;
        }
        mListView = new RecyclerView(context);
        mListView.setLayoutManager(new LinearLayoutManager(context));
        mListView.setId(getId());
        mPopup = new PopupWindow(context);
        mPopup.setContentView(mListView);
        mPopup.setOutsideTouchable(false);
        mPopup.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPopup.setElevation(DEFAULT_ELEVATION);
            mPopup.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.sp_spinner_drawable));
        } else {
            mPopup.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.drop_down_shadow));
        }
        mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!mHideArrow) {
                    animateArrow(false);
                }
            }
        });

        mHideArrow = typedArray.getBoolean(R.styleable.SimpleSpinner_hideArrow, false);
        if (!mHideArrow) {
            Drawable basicDrawable = ContextCompat.getDrawable(context, R.drawable.sp_arrow);
            int resId = typedArray.getColor(R.styleable.SimpleSpinner_arrowTint, -1);

            if (basicDrawable != null) {
                mDrawable = DrawableCompat.wrap(basicDrawable);

                if (resId != -1) {
                    DrawableCompat.setTint(mDrawable, resId);
                }
            }
            setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawable, null);

        }
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPopup.isShowing()) {
                    showDropDown();
                } else {
                    dismissDropDown();
                }
            }
        });
        typedArray.recycle();
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    /**
     * Set the default spinner item using its index
     *
     * @param position the item's position
     */
    public void setSelectedIndex(int position) {
        if (mAdapter != null) {
            if (position >= 0 && position <= mAdapter.getItemCount()) {
                mSelectedIndex = position;
                setText(mAdapter.getItemInDataSet(position));
            } else {
                throw new IllegalArgumentException("Position must be lower than adapter count!");
            }
        }
    }

    public OnItemClickerListener listener;

    public void setListener(OnItemClickerListener listener) {
        if (listener == null)
            throw new IllegalArgumentException("this afferent parameter can not be null");
        this.listener = listener;
        mAdapter.setListener(listener);
    }

    public void attachCustomDataSource(@NonNull Channel channel) {
        isShow = true;
        mSelectedIndex = 0;
        setText(channel.onShow(mSelectedIndex));
        mAdapter = new SpinnerAdapter(channel, new OnItemClickerListener() {
            @Override
            public void onResult(String result, int position) {
                mSelectedIndex = position;
                setText(result);
                dismissDropDown();
            }
        });
        setAdapterInternal(mAdapter);
    }

    private void setAdapterInternal(@NonNull SimpleSpinnerBaseAdapter adapter) {
        mListView.setAdapter(adapter);
        setText(adapter.getItemInDataSet(mSelectedIndex));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mPopup.setWidth(MeasureSpec.getSize(widthMeasureSpec));
        mPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private void animateArrow(boolean shouldRotateUp) {
        int start = shouldRotateUp ? 0 : MAX_LEVEL;
        int end = shouldRotateUp ? MAX_LEVEL : 0;
        ObjectAnimator animator = ObjectAnimator.ofInt(mDrawable, "level", start, end);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void dismissDropDown() {
        if (!mHideArrow) {
            animateArrow(false);
        }
        mPopup.dismiss();
    }

    public void showDropDown() {
        if (!mHideArrow) {
            animateArrow(true);
        }
        mPopup.showAsDropDown(this);
    }

    public void setTintColor(@ColorRes int resId) {
        if (mDrawable != null && !mHideArrow) {
            DrawableCompat.setTint(mDrawable, getResources().getColor(resId));
        }
    }
}

