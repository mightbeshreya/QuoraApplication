package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {
    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
          @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());

        final QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity,
                authorization);

        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid())
                .status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions
            (@RequestHeader("authorization") final String authorization ) throws AuthorizationFailedException {
        List<QuestionEntity> listOfQuestions = questionBusinessService.getAllQuestions(authorization);
        final List<QuestionDetailsResponse> questionDetailsResponses = new LinkedList<>() ;

        for(QuestionEntity q: listOfQuestions) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.setId(q.getUuid());
            questionDetailsResponse.setContent(q.getContent());
            questionDetailsResponses.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses, HttpStatus.OK);
    }
    /*@RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent()*/
}
