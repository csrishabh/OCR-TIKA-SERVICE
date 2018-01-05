
package com.mrll.javelin.tikaparser.model;

import java.io.InputStream;
import java.io.Serializable;

public class Document implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6387184918039881320L;
	private String id;
	private String docId;
	private String name;
	private InputStream content;
	private String date;
	private float score;
	private String rawText;
	private long size;

	/**
	 * 
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return docId
	 */
	public String getDocId() {
		return docId;
	}

	/**
	 * 
	 * @param docId
	 */
	public void setDocId(String docId) {
		this.docId = docId;
	}

	/**
	 * 
	 * @return name
	 */
	public String getLanguageName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setLanguageName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public InputStream getContent() {
		return content;
	}

	/**
	 * 
	 * @param content
	 */
	public void setContent(InputStream content) {
		this.content = content;
	}

	/**
	 * 
	 * @return
	 */
	public String getDate() {
		return date;
	}

	/**
	 * 
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the pageCount
	 */
	public float getScore() {
		return score;
	}

	/**
	 * @param pageCount
	 *            the pageCount to set
	 */
	public void setScore(float score) {
		this.score = score;
	}

	/**
	 * @return the rawText
	 */
	public String getRawText() {
		return rawText;
	}

	/**
	 * @param rawText
	 *            the rawText to set
	 */
	public void setRawText(String rawText) {
		this.rawText = rawText;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
