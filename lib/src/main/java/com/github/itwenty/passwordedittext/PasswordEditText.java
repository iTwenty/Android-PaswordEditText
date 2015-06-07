package com.github.itwenty.passwordedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class PasswordEditText extends EditText implements View.OnTouchListener
{
    private static final int LEFT   = 0;
    private static final int TOP    = 1;
    private static final int RIGHT  = 2;
    private static final int BOTTOM = 3;

    private Drawable mHiddenDrawable;
    private Drawable mShownDrawable;
    private Drawable mActiveDrawable;

    /**
     * If true, password is visible only as long as the drawable is held down.
     */
    private boolean mIsPasswordPeek;

    /**
     * Whether to show or hide the password. Initialized with true, but set to false
     * in init() so that setShown() executes at least once.
     */
    private boolean mIsShown = true;

    public PasswordEditText( final Context ctx )
    {
        super( ctx );
        init( ctx, null );
    }

    public PasswordEditText( final Context ctx, final AttributeSet attrs )
    {
        super( ctx, attrs );
        init( ctx, attrs );
    }

    public PasswordEditText( final Context ctx, final AttributeSet attrs, final int defStyleAttr )
    {
        super( ctx, attrs, defStyleAttr );
        init( ctx, attrs );
    }

    private void init( final Context ctx, final AttributeSet attrs )
    {
        setOnTouchListener( this );

        TypedArray a = ctx.obtainStyledAttributes( attrs,
                                                   R.styleable.PasswordEditText,
                                                   R.attr.PasswordEditTextStyle,
                                                   R.style.PasswordEditTextStyle );

        if( a != null )
        {
            mHiddenDrawable = a.getDrawable( R.styleable.PasswordEditText_passwordHidden );
            mShownDrawable = a.getDrawable( R.styleable.PasswordEditText_passwordShown );
            mIsPasswordPeek = a.getBoolean( R.styleable.PasswordEditText_passwordPeek, true );
            a.recycle();
        }

        setShown( false );
    }

    @Override
    public boolean onTouch( final View view, final MotionEvent ev )
    {
        final Drawable[] drawables = getCompoundDrawables();
        if( drawables[RIGHT] != null )
        {
            // We only want to toggle if the tap is within the drawable region
            // If it is, and peek is set, we toggle on UP and DOWN events.
            // If peek is not set, we toggle only on UP.
            final int xEnd = getWidth() - getPaddingRight();
            final int xBegin = xEnd - mActiveDrawable.getIntrinsicWidth();
            final int yEnd = getHeight() - getPaddingBottom();
            final int yBegin = yEnd - mActiveDrawable.getIntrinsicHeight();
            final Rect r = new Rect( xBegin, yBegin, xEnd, yEnd );
            final boolean tapped = r.contains( ( int ) ev.getX(), ( int ) ev.getY() );

            if( tapped )
            {
                if( mIsPasswordPeek )
                {
                    if( ev.getAction() == MotionEvent.ACTION_DOWN
                        || ev.getAction() == MotionEvent.ACTION_UP )
                    {
                        toggle();
                    }
                }
                else if( ev.getAction() == MotionEvent.ACTION_UP )
                {
                    toggle();
                }
                // We want to consume the touch event if it's within the drawable region
                return true;
            }
            // We also need to handle the case where peek is on, user presses
            // down and moves out of the drawable region before releasing
            // the touch. In this case, we intercept any MOVE event outside the
            // drawable region and hide the password.
            else
            {
                if( mIsPasswordPeek && ev.getAction() == MotionEvent.ACTION_MOVE )
                {
                    setShown( false );
                }
            }
        }
        return false;
    }

    private void setShown( boolean shown )
    {
        if( mIsShown == shown ) return;

        int startPos = getSelectionStart();
        int endPos = getSelectionEnd();

        TransformationMethod method = null;
        if( !shown ) method = PasswordTransformationMethod.getInstance();
        setTransformationMethod( method );

        setShownDrawable( shown );

        requestFocus();
        setSelection( startPos, endPos );
        mIsShown = shown;
    }

    private void toggle()
    {
        setShown( !mIsShown );
    }

    private void setShownDrawable( boolean shown )
    {
        Drawable[] compoundDrawables = getCompoundDrawables();

        mActiveDrawable = shown ? mShownDrawable : mHiddenDrawable;

        setCompoundDrawablesWithIntrinsicBounds( compoundDrawables[LEFT], compoundDrawables[TOP],
                                                 mActiveDrawable, compoundDrawables[BOTTOM] );
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState( superState, mIsShown );
    }

    @Override
    public void onRestoreInstanceState( Parcelable state )
    {
        SavedState ss = ( SavedState ) state;
        super.onRestoreInstanceState( ss.getSuperState() );
        setShown( ss.mIsShown );
    }

    protected static class SavedState extends BaseSavedState
    {
        public boolean mIsShown;

        public SavedState( Parcel source )
        {
            super( source );
            mIsShown = source.readByte() == 1;
        }

        public SavedState( Parcelable superState, boolean isShown )
        {
            super( superState );
            mIsShown = isShown;
        }

        @Override
        public void writeToParcel( Parcel dest, int flags )
        {
            super.writeToParcel( dest, flags );
            dest.writeByte( mIsShown ? ( byte ) 1 : 0 );
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>()
        {
            @Override
            public SavedState createFromParcel( Parcel source )
            {
                return new SavedState( source );
            }

            @Override
            public SavedState[] newArray( int size )
            {
                return new SavedState[size];
            }
        };
    }
}