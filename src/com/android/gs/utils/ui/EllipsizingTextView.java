package com.android.gs.utils.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class EllipsizingTextView extends TextView {
	private static final String ELLIPSIS = "...";
	private static final int MAX_NUM_CHARACTER_IN_LINE = 35;
	public interface EllipsizeListener {
		void ellipsizeStateChanged(boolean ellipsized, int lastIndexTrunkcate);
	}

	private final List<EllipsizeListener> ellipsizeListeners = new ArrayList<EllipsizeListener>();
	private boolean isEllipsized;
	private boolean isStale;
	private boolean programmaticChange;
	private String fullText;
	private int maxLines = 1;
	private float lineSpacingMultiplier = 1.0f;
	private float lineAdditionalVerticalPadding = 0.0f;
	private int lastIndexTrunkcated;
	private String strMore = "";
	
	public EllipsizingTextView(Context context) {
		super(context);
	}

	public EllipsizingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EllipsizingTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void addEllipsizeListener(EllipsizeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		ellipsizeListeners.add(listener);
	}

	public void removeEllipsizeListener(EllipsizeListener listener) {
		ellipsizeListeners.remove(listener);
	}

	public boolean isEllipsized() {
		return isEllipsized;
	}

	@Override
	public void setMaxLines(int maxLines) {
		super.setMaxLines(maxLines);
		this.maxLines = maxLines;
		isStale = true;
	}

	public void setStringMore(String more) {
		this.strMore = more;
	}

	public int getMaxLines() {
		return maxLines;
	}

	@Override
	public void setLineSpacing(float add, float mult) {
		this.lineAdditionalVerticalPadding = add;
		this.lineSpacingMultiplier = mult;
		super.setLineSpacing(add, mult);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before,
			int after) {
		super.onTextChanged(text, start, before, after);
		if (!programmaticChange) {
			fullText = text.toString();
			isStale = true;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isStale) {
			super.setEllipsize(null);
			resetText();
		}
		super.onDraw(canvas);
	}

	private void resetText() {
		int maxLines = getMaxLines();
		String workingText = fullText;
		String totalString = workingText + ELLIPSIS + strMore;
		String more = ELLIPSIS + strMore;
		boolean ellipsized = false;
		if (maxLines != -1) {
			Layout layout = null;
			if (maxLines == 1 && length() <= MAX_NUM_CHARACTER_IN_LINE )
				layout = createWorkingLayout(workingText);
			else
				layout = createWorkingLayout(workingText + strMore);
			if (layout.getLineCount() > maxLines) {
				// workingText = fullText.substring(0,
				// layout.getLineVisibleEnd(maxLines));
				totalString = totalString.substring(0, totalString.length()
						- more.length());
				// workingText = fullText.substring(0,
				// layout.getEllipsisStart(maxLines));
				layout = createWorkingLayout(totalString);
				while (layout.getLineCount() > maxLines) {

					totalString = totalString.substring(0, totalString.length()
							- more.length());
					// workingText = workingText.substring(0,
					// layout.getLineEnd(maxLines - 1));
					layout = createWorkingLayout(totalString);
				}
				lastIndexTrunkcated = totalString.length() - 1;
				totalString = totalString.substring(0, lastIndexTrunkcated
						- more.length());
				totalString = totalString + ELLIPSIS + strMore;

				ellipsized = true;
			}

		}
		if (!ellipsized) {
			programmaticChange = true;
			try {
				setText(workingText + strMore);
			} finally {
				programmaticChange = false;
			}
		} else {
			setText(totalString);
		}
		// Display display = ((WindowManager)
		// KunKunInfo.getInstance().getAppContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		// Log.e("PhucNT4", "max width kunkun " + getWidth() +"   " +
		// display.getWidth());
		// if (getWidth() == display.getWidth())
		// {
		//
		// workingText = workingText.substring(0, workingText.length()-1 -5);
		// setText(workingText + strMore);
		// }
		isStale = false;
		if (ellipsized != isEllipsized) {
			isEllipsized = ellipsized;
			for (EllipsizeListener listener : ellipsizeListeners) {
				listener.ellipsizeStateChanged(ellipsized, lastIndexTrunkcated);
			}
		}
	}

	private Layout createWorkingLayout(String workingText) {

		return new StaticLayout(workingText, getPaint(), getWidth()
				- getPaddingLeft() - getPaddingRight(), Alignment.ALIGN_NORMAL,
				lineSpacingMultiplier, lineAdditionalVerticalPadding, false);
		// return new StaticLayout(workingText, 0, workingText.length(),
		// getPaint(), getWidth() - getPaddingLeft() - getPaddingRight(),
		// Alignment.ALIGN_NORMAL, 0,
		// 0, true,TruncateAt.END, getWidth());
	}

	@Override
	public void setEllipsize(TruncateAt where) {
		// Ellipsize settings are not respected
	}

	public int getIndexTruncated() {
		return lastIndexTrunkcated;
	}
}
