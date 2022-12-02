package com.apurbagiri.apps.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.apurbagiri.apps.model.AwsS3Object;
import com.apurbagiri.apps.service.S3ObjectService;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@Scope("view")
public class ObjectView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3577766961460877816L;

	@Autowired
	private SearchParam searchParam;

	@Autowired
	private S3ObjectService searchService;

	private List<AwsS3Object> objectList;

	private AwsS3Object selectedObject;

	public void search() {
		objectList = new ArrayList<>();
		try {
			objectList = searchService.getSearchResult(searchParam.getAccessKey(), searchParam.getSecretKey(),
					searchParam.getBucketRegion(), searchParam.getBucketName(), searchParam.getSearchPrefix(),
					searchParam.getDateFrom(), searchParam.getDateTo());
		} catch (NoSuchBucketException e) {
			addError("Bucket not found");
		} catch (S3Exception e) {
			switch (e.awsErrorDetails().errorCode()) {
			case "InvalidAccessKeyId":
				addError("Access Key not found");
				break;
			case "SignatureDoesNotMatch":
				addError("Access Key and Secret Key combination is invalid");
				break;
			case "AccessDenied":
				addError("Access denied to specified bucket");
				break;
			case "PermanentRedirect":
				addError("Bucket not found in the selected region");
				break;
			default:
				addError("Unknown error ocurred");
			}

		} catch (SdkClientException e) {
			addError("Selected region is invalid");
		} catch (Exception e) {
			addError("Unknown error ocurred");
		}

		System.out.println("Hello");

	}

	public void onPeriodSelectionListener(SelectEvent event) {
		if (!searchParam.isManualPeriod()) {
			searchParam.setDateFrom(getTimedDate(new Date(), -1 * searchParam.getRetrievePeriod()));
			searchParam.setDateTo(getTimedDate(new Date(), 0));
		}
	}
	
	
	public void download()
	{
		System.out.println("Download executed!");
	}
	
	/**
	 * Method to reset search form
	 */
	public void reset() {
		System.out.println("Reset executed!");
		searchParam.setAccessKey(null);
		searchParam.setSecretKey(null);
		searchParam.setBucketName(null);
		searchParam.setBucketRegion(null);
		searchParam.setDateFrom(null);
		searchParam.setDateTo(null);
		searchParam.setManualPeriod(false);
		searchParam.setRetrievePeriod(0);
		searchParam.setSearchPrefix(null);
		objectList = new ArrayList<>();
		selectedObject = null;
		addMessage("Form has been reset");
	}

	private Date getTimedDate(Date date, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

	public void addError(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void addMessage(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public List<AwsS3Object> getObjectList() {
		return objectList;
	}

	public void setObjectList(List<AwsS3Object> objectList) {
		this.objectList = objectList;
	}

	public AwsS3Object getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(AwsS3Object selectedObject) {
		this.selectedObject = selectedObject;
	}
}
