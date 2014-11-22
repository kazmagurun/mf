package com.kazma.mathfactor;

/**
 * An episode item.
 */
public class Item {
	/** Title of the episode. */
	private String mTitle;
	/** Link to the episode. */
	private String mLink;
	/** Download link to the episode. */
	private String mDownload;
	/** Some extra info to display. */
	private String mExtraInfo;
	/** Last known listened position. */
	private int mPosition;
	/** Length (0 if unknown). */
	private int mLength;
	
	public Item(String title, String link, String download) {
		this(title, link, download, 0, 0);
	}
	/**
	 * Constructor.
	 * 
	 * @param title title of the episode
	 * @param link link to the episode
	 * @param download link to the episode
	 */
	public Item(String title, String link, String download, int position, int length) {
		mTitle = title;
		mLink = link;
		mDownload = download;
		mPosition = position;
		mLength = length;
		mExtraInfo = "";
	}

	public void setPosition(int position) {
		mPosition = position;
	}
	
	public void setLength(int length) {
		mLength = length;
	}
	
	public String getExtraInfo() {
		int mRatio = 0;
		if (mLength > 0) {
			mRatio = (mPosition*100)/mLength;
		}
		return Integer.toString(mRatio) + "%";
	}
	
	public String getTitle() {
		return mTitle;
	}

	public String getDownload() {
		return mDownload;
	}
	
	public String getLink() {
		return mLink;
	}
}
