package com.manish.awsimage.profile;

import com.manish.awsimage.bucket.BucketName;
import com.manish.awsimage.filestore.FileStore;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserProfileService {
    private final UserProfileDataAccessService userProfileDataAccessService;
    private final FileStore fileStore;

    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    public List<UserProfile> getUserProfiles(){
        return userProfileDataAccessService.getUserProfiles();
    }

    public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        //check if file is empty
        isFileEmpty(file);

        //2. check if file is an image
        isImage(file);

        //user exist in our database
        UserProfile user = getUserProfileOrThrow(userProfileId);

        //grab some metadata if any
        Map<String, String> metaData = extractMetaData(file);

        //store the image in s3 bucket
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(),userProfileId);
        String fileName = String.format("%s-%s",file.getOriginalFilename(),UUID.randomUUID());
        try {
            fileStore.save(path, fileName, Optional.of(metaData), file.getInputStream());
            user.setUserProfileImageLink(fileName);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] downloadUserProfileImage(UUID userProfileId) {
        //user exist in our database
        UserProfile user = getUserProfileOrThrow(userProfileId);
        //store the image in s3 bucket
        String path = String.format("%s/%s",
                            BucketName.PROFILE_IMAGE.getBucketName(),
                            userProfileId);
        return user.getUserProfileImageLink()
                .map(key-> fileStore.download(path,key))
                .orElse(new byte[0]);
    }

    private Map<String, String> extractMetaData(MultipartFile file) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("Content-Type",file.getContentType());
        metaData.put("Content-Length", String.valueOf(file.getSize()));
        return metaData;
    }

    private UserProfile getUserProfileOrThrow(UUID userProfileId) {
        return userProfileDataAccessService
                .getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(()-> new  IllegalStateException(String.format("User profile %s not found", userProfileId)));
    }

    private void isImage(MultipartFile file) {
        if(!Arrays.asList(
                ContentType.IMAGE_JPEG.getMimeType(),
                ContentType.IMAGE_PNG.getMimeType(),
                ContentType.IMAGE_GIF.getMimeType()
        ).contains(file.getContentType())){
            throw new IllegalStateException("file must be an image [ "+file.getContentType()+" ]");
        }
    }

    private void isFileEmpty(MultipartFile file) {
        if(file.isEmpty()){
            throw new IllegalStateException("Cannot upload empty file [ "+file.getSize()+" ]");
        }
    }


}
