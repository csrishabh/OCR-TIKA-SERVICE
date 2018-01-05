package com.mrll.javelin.tikaparser.model;

/**
 * 
 * @author shipra.pandey
 *
 */
public class DocProperties {

	private String fileSize;
	private String pageCount;
	
	private String fileType;

	public DocProperties() {
		// default constructor
	}

	public DocProperties(String fileSize, String pageCount, String fileType) {
		super();
		if (fileSize != null) {
			this.fileSize = fileSize;
		} else {
			this.fileSize = "0";
		}
		if (pageCount != null) {
			this.pageCount = pageCount;
		} else {
			this.pageCount = "0";
		}
		if (fileType != null) {
			this.fileType = fileType;
		} else {
			this.fileType = "";
		}
	}

	/**
	 * @return the fileSize
	 */
	public String getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return the pageCount
	 */
	public String getPageCount() {
		return pageCount;
	}

	/**
	 * @param pageCount
	 *            the pageCount to set
	 */
	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * @param fileType
	 *            the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
