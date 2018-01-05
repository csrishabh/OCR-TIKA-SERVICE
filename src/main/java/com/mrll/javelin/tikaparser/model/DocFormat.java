package com.mrll.javelin.tikaparser.model;

/**
 * 
 * @author shipra.pandey
 *
 */
public class DocFormat {

	private String pName;

	private String docId;

	private DocProperties docProperties;

	/**
	 * Parameterized constructor
	 * 
	 * @param pName
	 * @param docId
	 */
	public DocFormat(String pName, String docId) {
		this.pName = pName;
		this.docId = docId;
	}

	public DocFormat() {
		// default generated constructor
	}

	/**
	 * @return the pName
	 */
	public String getpName() {
		return pName;
	}

	/**
	 * @param pName
	 *            the pName to set
	 */
	public void setpName(String pName) {
		this.pName = pName;
	}

	/**
	 * @return the docId
	 */
	public String getDocId() {
		return docId;
	}

	/**
	 * @param docId
	 *            the docId to set
	 */
	public void setDocId(String docId) {
		this.docId = docId;
	}

	/**
	 * @return the docProperties
	 */
	public DocProperties getDocProperties() {
		return docProperties;
	}

	/**
	 * @param docProperties
	 *            the docProperties to set
	 */
	public void setDocProperties(DocProperties docProperties) {
		this.docProperties = docProperties;
	}

}
