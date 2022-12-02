package com.apurbagiri.apps.view;

import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class SearchParam{

	private String accessKey;
	private String secretKey;
	private String bucketName;
	private String bucketRegion;
	private String searchPrefix;
	private int retrievePeriod = 1;
	private boolean isManualPeriod;
	private Date dateFrom;
	private Date dateTo;

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getBucketRegion() {
		return bucketRegion;
	}

	public void setBucketRegion(String bucketRegion) {
		this.bucketRegion = bucketRegion;
	}

	public String getSearchPrefix() {
		return searchPrefix;
	}

	public void setSearchPrefix(String searchPrefix) {
		this.searchPrefix = searchPrefix;
	}

	public int getRetrievePeriod() {
		return retrievePeriod;
	}

	public void setRetrievePeriod(int retrievePeriod) {
		this.retrievePeriod = retrievePeriod;
	}

	public boolean isManualPeriod() {
		return isManualPeriod;
	}

	public void setManualPeriod(boolean isManualPeriod) {
		this.isManualPeriod = isManualPeriod;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

}
