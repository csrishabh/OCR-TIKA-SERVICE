package com.mrll.javelin.tikaparser.model;

public class MessageBody {
	
	 private String docId;
	 private String fName;
	 private String mdId;
	 
	/**
	 * @return the docId
	 */
	public String getDocId() {
		return docId;
	}
	/**
	 * @param docId the docId to set
	 */
	public void setDocId(String docId) {
		this.docId = docId;
	}
	/**
	 * @return the fName
	 */
	public String getfName() {
		return fName;
	}
	/**
	 * @param fName the fName to set
	 */
	public void setfName(String fName) {
		this.fName = fName;
	}
	/**
	 * @return the srcDId
	 */
	public String getSrcDId() {
		return mdId;
	}
	/**
	 * @param srcDId the srcDId to set
	 */
	public void setSrcDId(String srcDId) {
		this.mdId = srcDId;
	}

}
