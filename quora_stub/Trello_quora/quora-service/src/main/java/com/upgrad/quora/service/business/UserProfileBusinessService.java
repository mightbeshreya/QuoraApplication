package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileBusinessService {
    @Autowired
    private UserDao userDao;

    public UserEntity getUser(final String userId, final String authorization)
            throws UserNotFoundException, AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity==null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthTokenEntity.getLogout_at()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }
        UserEntity userEntity = userDao.getUserByUserID(userId);
        if(userEntity==null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        return userEntity;
    }
}
