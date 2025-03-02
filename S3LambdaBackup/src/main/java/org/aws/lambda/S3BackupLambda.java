package org.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class S3BackupLambda {

    private static final String SOURCE_BUCKET = "naga-source-s3-bucket";  // Change to your source bucket
    private static final String BACKUP_BUCKET = "naga-backup-s3-bucket";  // Change to your backup bucket

    private final S3Client s3Client;

    public S3BackupLambda() {
        this.s3Client = S3Client.create();
    }

    public void handleRequest(S3Event s3Event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Received event: " + s3Event.toString());

        for (S3EventNotification.S3EventNotificationRecord record : s3Event.getRecords()) {
            String bucketName = record.getS3().getBucket().getName();
            String fileKey = record.getS3().getObject().getKey();

            if (bucketName.equals(SOURCE_BUCKET)) {
                copyFile(fileKey, logger);
            }
        }
    }

    private void copyFile(String fileKey, LambdaLogger logger) {
        try {
            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .sourceBucket(SOURCE_BUCKET)
                    .destinationBucket(BACKUP_BUCKET)
                    .sourceKey(fileKey)
                    .destinationKey(fileKey)
                    .build();

            s3Client.copyObject(copyRequest);
            logger.log("Successfully copied file: " + fileKey + " to " + BACKUP_BUCKET);

        } catch (S3Exception e) {
            logger.log("Error copying file: " + e.awsErrorDetails().errorMessage());
        }
    }
}
