package org.kajevand.messageboard;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kajevand.messageboard.entity.Message;
import org.kajevand.messageboard.exception.MessageNotFoundException;
import org.kajevand.messageboard.exception.NotAuthorizedException;
import org.kajevand.messageboard.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.kajevand.messageboard.MockData.USER_ONE;
import static org.kajevand.messageboard.MockData.USER_TWO;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Test
    public void saveMessage() {
        Message message = messageService.saveMessage(MockData.userTwoAllMessages().get(0), USER_ONE);
        assertNotNull(message.getId());
        message = messageService.findMessageById(message.getId());
        assertEquals(message.getSubject(), MockData.userTwoAllMessages().get(0).getSubject());
        assertThrows(NotAuthorizedException.class, () -> messageService.saveMessage(MockData.userOneAllMessages().get(0), "invalidUserName"));
        assertThrows(ConstraintViolationException.class, () -> messageService.saveMessage(MockData.messageWithInvalidSubjectToLong(), USER_ONE));
        assertThrows(ConstraintViolationException.class, () -> messageService.saveMessage(MockData.messageWithInvalidSubjectEmpty(), USER_ONE));
        assertThrows(ConstraintViolationException.class, () -> messageService.saveMessage(MockData.messageWithInvalidContentEmpty(), USER_ONE));
        assertThrows(ConstraintViolationException.class, () -> messageService.saveMessage(MockData.messageWithInvalidContentToLong(), USER_ONE));
    }

    @Test
    public void findAll() {
        List<Message> allMessages = messageService.findAll();
        assertNotNull(allMessages);
        assertEquals(6, allMessages.size());
    }

    @Test
    public void findMessageById() {
        Message message = messageService.findMessageById(1L);
        assertNotNull(message);
        assertThrows(InvalidDataAccessApiUsageException.class, () -> messageService.findMessageById(null));
        assertThrows(MessageNotFoundException.class, () -> messageService.findMessageById(2000L));
    }

    @Test
    public void deleteMessage() {
        assertThrows(NotAuthorizedException.class, () -> messageService.deleteMessageById(1L, "notAuthorizedUsername@email.com"));
        messageService.deleteMessageById(1L, USER_ONE);
        assertThrows(MessageNotFoundException.class, () -> messageService.findMessageById(1L));
        assertThrows(MessageNotFoundException.class, () -> messageService.deleteMessageById(1L, USER_ONE));
    }

    @Test
    public void updateMessageById() {
        Message message = messageService.findMessageById(1L);
        message.setSubject("subjectUpdated");
        messageService.updateMessageById(message, 1L, USER_ONE);
        Message updatedMessages = messageService.findMessageById(1L);
        assertEquals("subjectUpdated", updatedMessages.getSubject());
    }

    @Test
    public void updateMessageNotAuthorized() {
        Message updatedMessage = MockData.userOneAllMessages().get(0);
        assertThrows(NotAuthorizedException.class, () -> messageService.updateMessageById(updatedMessage, 1L, USER_TWO));
    }

    @Test
    public void updateMessageNotFound() {
        Message updatedMessage = MockData.userOneAllMessages().get(0);
        assertThrows(MessageNotFoundException.class, () -> messageService.updateMessageById(updatedMessage, 1000L, USER_ONE));
    }
}