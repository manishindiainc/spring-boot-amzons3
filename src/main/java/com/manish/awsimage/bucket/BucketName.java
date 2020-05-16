package com.manish.awsimage.bucket;

public enum BucketName {
    PROFILE_IMAGE("manish-aws-image-upload");


    private final String bucketName;

    BucketName(String name) {
        bucketName = name;
    }


    public String getBucketName() {
        return bucketName;
    }
}
