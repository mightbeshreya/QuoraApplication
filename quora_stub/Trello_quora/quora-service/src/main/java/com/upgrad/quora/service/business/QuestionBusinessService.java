package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String authorization) throws AuthorizationFailedException{
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity==null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthTokenEntity.getLogout_at()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        questionEntity.setUser(userAuthTokenEntity.getUser());
        return questionDao.createQuestion(questionEntity);
    }

    public List<QuestionEntity> getAllQuestions(final String authorization) throws AuthorizationFailedException{
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity==null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthTokenEntity.getLogout_at()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        List<QuestionEntity> listOfQuestions  = questionDao.getAllQuestions();
        return listOfQuestions;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(QuestionEntity questionEntity, final String authorization,
                                              final String questionId) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);
        if(userAuthTokenEntity==null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthTokenEntity.getLogout_at()!=null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        QuestionEntity questionEntitybyId = questionDao.getQuestionById(questionId);
        if(questionEntitybyId==null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if(!questionEntitybyId.getUser().getUsername().equals(userAuthTokenEntity.getUser().getUsername())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        return questionDao.editQuestionContent(questionEntity);
    }
}
