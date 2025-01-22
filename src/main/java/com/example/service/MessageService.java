package com.example.service;

import com.example.entity.Message;
import com.example.entity.Account;
import com.example.repository.MessageRepository;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Message createMessage(Message message, Integer userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        message.setPostedBy(account.getAccountId());
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message getMessageById(Integer messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    public boolean deleteMessage(Integer messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return true;
        }
        return false;
    }

    public int updateMessage(Integer messageId, String newMessageText) {
        if (newMessageText == null || newMessageText.trim().isEmpty() || newMessageText.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message text");
        }   // Empty string is somehow passing through even though it is getting checked...
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message not found"));

        message.setMessageText(newMessageText);
        messageRepository.save(message);

        return 1;
    }

    public List<Message> getMessagesByUser(Integer userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        return messageRepository.findByPostedBy(userId);
    }

}
