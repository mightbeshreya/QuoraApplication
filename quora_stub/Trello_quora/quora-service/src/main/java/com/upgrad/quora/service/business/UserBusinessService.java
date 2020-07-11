package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        if(userDao.isUsernameExists(userEntity.getUsername())) {
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }
        if(userDao.isEmailExists(userEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        return userDao.createUser(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signin(final String username, final String password) throws AuthenticationFailedException {
        UserEntity existingUserEntity = userDao.getUserByUsername(username);
        if(existingUserEntity==null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, existingUserEntity.getSalt());
        if(encryptedPassword.equals(existingUserEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthTokenEntity  = new UserAuthTokenEntity();
            userAuthTokenEntity.setUser(existingUserEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            userAuthTokenEntity.setAccess_token(jwtTokenProvider.generateToken(existingUserEntity.getUuid(),now,expiresAt));
            userAuthTokenEntity.setLogin_at(now);
            userAuthTokenEntity.setExpires_at(expiresAt);
            userAuthTokenEntity.setUuid(UUID.randomUUID().toString());

            userDao.createAuthTokenEntity(userAuthTokenEntity);

            return userAuthTokenEntity;

        }else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }
    }
}
