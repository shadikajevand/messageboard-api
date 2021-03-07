package org.kajevand.messageboard.service;

import org.kajevand.messageboard.entity.Message;
import org.kajevand.messageboard.entity.User;
import org.kajevand.messageboard.exception.MessageNotFoundException;
import org.kajevand.messageboard.exception.NotAuthorizedException;
import org.kajevand.messageboard.repository.MessageRepository;
import org.kajevand.messageboard.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private MessageRepository messageRepository;
    private UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository){
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public List<Message> findAll() {
        return (List<Message>) messageRepository.findAll();
    }

    public Message findMessageById(Long id) {
        return messageRepository.findById(id).
                orElseThrow(() -> new MessageNotFoundException(id));
    }

    public Message saveMessage(Message message, String username) {
        User user = userRepository.findByUsernameIgnoreCase(username).stream().findFirst().orElseThrow(NotAuthorizedException::new);
        message.setUsername(user.getUsername());
        return messageRepository.save(message);
    }

    public Message updateMessageById(Message message, Long id, String username) {

        Message savedMessage = messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException(id));

        if(!savedMessage.getUsername().equalsIgnoreCase(username)) {
            throw new NotAuthorizedException(id);
        }
        savedMessage.setSubject(message.getSubject());
        savedMessage.setContent(message.getContent());
        return messageRepository.save(savedMessage);
    }

    public void deleteMessageById(Long id, String username) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException(id));

        if(message.getUsername().equalsIgnoreCase(username)) {
            messageRepository.deleteById(id);
        } else {
            throw new NotAuthorizedException(id);
        }
    }
}
