package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

@RestController
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/register")
    public Account registerAccount(@RequestBody Account account) {
        return accountService.registerAccount(account);
    }

    @PostMapping("/login")
    public Account loginAccount(@RequestBody Account account) {
        Optional<Account> loggedInAccount = accountService.loginAccount(account);
        if (loggedInAccount.isPresent()) {
            return loggedInAccount.get();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/messages")
    public Message createMessage(@RequestBody Message message) {
        if (message.getMessageText() == null || message.getMessageText().isBlank() || message.getMessageText().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text is either blank or exceeds 255 characters.");
        }
        if (message.getPostedBy() == null || !accountService.accountExists(message.getPostedBy())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user: postedBy must refer to an existing user.");
        }
    
        return messageService.createMessage(message, message.getPostedBy());
    }

    @GetMapping("/messages")
    public List<Message> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
    
        return messages;
    }

    @GetMapping("/messages/{messageId}")
    public Message getMessageById(@PathVariable Integer messageId) {
        return messageService.getMessageById(messageId);
    }

    @DeleteMapping("/messages/{messageId}")
    public Integer deleteMessage(@PathVariable Integer messageId) {
        boolean deleted = messageService.deleteMessage(messageId);

        return deleted ? 1 : null;
    }

    @PatchMapping("/messages/{messageId}")
    public int updateMessage(@PathVariable Integer messageId, @RequestBody String newMessageText) {
        return messageService.updateMessage(messageId, newMessageText);
    }

    @GetMapping("/accounts/{accountId}/messages")
    public List<Message> getMessagesByUser(@PathVariable Integer accountId) {
        return messageService.getMessagesByUser(accountId);
    }

}
