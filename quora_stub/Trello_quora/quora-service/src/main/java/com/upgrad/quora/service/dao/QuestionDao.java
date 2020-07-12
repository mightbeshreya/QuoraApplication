package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }
    public List<QuestionEntity> getAllQuestions() {
        try {
            return entityManager.createNamedQuery("getAllQuestions" , QuestionEntity.class).getResultList();
        }catch(NoResultException nre) {
            return null;
        }
    }
    public QuestionEntity getQuestionById(final String questionId) {
        try {
            return entityManager.createNamedQuery("getQuestionById", QuestionEntity.class)
                    .setParameter("questionId", questionId).getSingleResult();
        }catch(NoResultException nre ) {
            return null;
        }
    }

    public QuestionEntity editQuestionContent(QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
        return questionEntity;
    }
}
