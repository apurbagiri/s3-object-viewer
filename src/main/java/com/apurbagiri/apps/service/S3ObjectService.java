package com.apurbagiri.apps.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.apurbagiri.apps.model.AwsS3Object;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

/**
 * Service class to view/retrieve S3 bucket objects
 * 
 * @author apurba.giri
 *
 */
@Service
public class S3ObjectService {

	private static S3Client client;

	private List<AwsS3Object> filteredObjectList;

	private void initClient(String accessKey, String secretKey, String bucketRegion) {
		AwsBasicCredentials awsCred = AwsBasicCredentials.create(accessKey, secretKey);
		client = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsCred))
				.region(Region.of(bucketRegion)).build();
	}

	private void populateSearchResult(String bucket, String prefix, Date dateFrom, Date dateUntil) {
		filteredObjectList = new ArrayList<>();
		ListObjectsV2Request s3Request = ListObjectsV2Request.builder().bucket(bucket).prefix(prefix).build();
		ListObjectsV2Iterable responseIterable = client.listObjectsV2Paginator(s3Request);
		for (ListObjectsV2Response objectPage : responseIterable) {
			objectPage.contents().forEach((S3Object object) -> {
				if (dateFrom != null && dateUntil != null) {
					if (object.lastModified().isAfter(dateFrom.toInstant())
							&& object.lastModified().isBefore(dateUntil.toInstant())) {
						AwsS3Object filteredObject = new AwsS3Object();
						filteredObject.setObjectKey(object.key());
						filteredObject.setLastModified(Date.from(object.lastModified()));
						filteredObject.setSize(object.size());
						filteredObjectList.add(filteredObject);
					}
				}
			});
		}
	}

	public List<AwsS3Object> getSearchResult(String accessKey, String secretKey, String bucketRegion, String bucket,
			String prefix, Date dateFrom, Date dateUntil) {

		initClient(accessKey, secretKey, bucketRegion);
		populateSearchResult(bucket, prefix, dateFrom, dateUntil);
//		populateDummyData(30);
		return filteredObjectList;
	}

	
	
	private void populateDummyData(int recordCount) {
		filteredObjectList = new ArrayList<>();
		for (int i = 0; i < recordCount; i++) {
			AwsS3Object filteredObject = new AwsS3Object();
			filteredObject.setObjectKey(UUID.randomUUID().toString());
			filteredObject.setLastModified(new Date(Math.abs(System.currentTimeMillis() - new Random().nextLong())));
			filteredObject.setSize(ThreadLocalRandom.current().nextLong(4));
			filteredObjectList.add(filteredObject);
		}
	}

}
