package com.apurbagiri.apps.view;

import java.io.BufferedInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.apurbagiri.apps.model.AwsS3Object;
import com.apurbagiri.apps.service.S3ObjectService;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
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

	private List<String> regionList;
	
	public ObjectView() {
		regionList = new ArrayList<>();
		for (Region region : Region.regions()) {
			regionList.add(region.toString());
		}
		Collections.sort(regionList);
	}
	
	/**
	 * Method to perform S3 Object search
	 */
	public void search() {
		objectList = new ArrayList<>();
		updateSearchPeriod();
		try {
			objectList = searchService.getSearchResult(searchParam.getAccessKey(), searchParam.getSecretKey(),
					searchParam.getBucketRegion(), searchParam.getBucketName(), searchParam.getSearchPrefix(),
					searchParam.getDateFrom(), searchParam.getDateTo());
		} catch (NoSuchBucketException e) {
			addError("Bucket not found");
			e.printStackTrace();
		} catch (S3Exception e) {
			switch (e.awsErrorDetails().errorCode()) {
			case "InvalidAccessKeyId":
				addError("Access Key not found");
				e.printStackTrace();
				break;
			case "SignatureDoesNotMatch":
				addError("Access Key and Secret Key combination is invalid");
				e.printStackTrace();
				break;
			case "AccessDenied":
				addError("Access denied to specified bucket");
				e.printStackTrace();
				break;
			case "PermanentRedirect":
				addError("Bucket not found in the selected region");
				e.printStackTrace();
				break;
			default:
				addError("Unknown error ocurred");
				e.printStackTrace();
			}

		} catch (SdkClientException e) {
			addError("Selected region is invalid");
			e.printStackTrace();
		} catch (Exception e) {
			addError("Unknown error ocurred");
			e.printStackTrace();
		}
	}

	/**
	 * Listener method to modify date range based on period selection
	 * 
	 * @param event
	 */
	public void onPeriodSelectionListener(SelectEvent event) {
		updateSearchPeriod();
	}

	private void updateSearchPeriod() {
		if (!searchParam.isManualPeriod()) {
			searchParam.setDateFrom(getTimedDate(new Date(), -1 * searchParam.getRetrievePeriod()));
			searchParam.setDateTo(getTimedDate(new Date(), 0));
		}
	}

	/**
	 * Method to return download stream for selected S3 Object
	 * 
	 * @return StreamedContent
	 */
	public StreamedContent downloadFile() {
		String key = selectedObject.getObjectKey();
		try {
			return DefaultStreamedContent.builder().name(key).stream(
					() -> new BufferedInputStream(searchService.getBiggerObject(searchParam.getBucketName(), key)))
					.build();
		} catch (Exception e) {
			addError("Something went wrong!");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Method to reset search form
	 */
	public void reset() {
		searchParam.setAccessKey(null);
		searchParam.setSecretKey(null);
		searchParam.setBucketName(null);
		searchParam.setBucketRegion("us-east-1");
		searchParam.setDateFrom(null);
		searchParam.setDateTo(null);
		searchParam.setManualPeriod(false);
		searchParam.setRetrievePeriod(5);
		searchParam.setSearchPrefix(null);
		objectList = new ArrayList<>();
		selectedObject = null;
		addMessage("Form has been reset");
	}

	/**
	 * Method to retrieve the date varied by provided minutes
	 * 
	 * @param date
	 * @param minutes
	 * @return Date
	 */
	private Date getTimedDate(Date date, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

	/**
	 * Method to show error message on the UI
	 * 
	 * @param summary
	 */
	public void addError(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	/**
	 * Method to show info message on the UI
	 * 
	 * @param summary
	 */
	public void addMessage(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	/**
	 * Get S3 Object list for populating dataTable
	 * 
	 * @return List<AwsS3Object>
	 */
	public List<AwsS3Object> getObjectList() {
		return objectList;
	}

	/**
	 * Set S3 Object list
	 * 
	 * @param objectList
	 */
	public void setObjectList(List<AwsS3Object> objectList) {
		this.objectList = objectList;
	}

	/**
	 * Get selected object from object dataTable
	 * 
	 * @return AwsS3Object
	 */
	public AwsS3Object getSelectedObject() {
		return selectedObject;
	}

	/**
	 * Set object selection from object dataTable
	 * 
	 * @param selectedObject
	 */
	public void setSelectedObject(AwsS3Object selectedObject) {
		this.selectedObject = selectedObject;
	}


	/**
	 * @return the regionList
	 */
	public List<String> getRegionList() {
		return regionList;
	}

}
