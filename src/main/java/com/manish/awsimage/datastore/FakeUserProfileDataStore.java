package com.manish.awsimage.datastore;

import com.manish.awsimage.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {
    private static final List<UserProfile> USER_PROFILES= new ArrayList<>();

    static {
        //use your own uuid, UUID.randomUUID()
        USER_PROFILES.add(new UserProfile(UUID.fromString(
                "0e39fbdd-41ab-4ebb-9e56-0faaeb632d86"), "manish",null));
        USER_PROFILES.add(new UserProfile(UUID.fromString(
                "3cc542ac-3a12-4608-bc55-6806070181fd"), "Ravi",null));
        USER_PROFILES.add(new UserProfile(UUID.fromString(
                "db5c0948-afe7-4c53-ab7e-daca0619077e"), "Deepak",null));
    }

    public List<UserProfile> getUserProfiles(){
        return USER_PROFILES;
    }
}
