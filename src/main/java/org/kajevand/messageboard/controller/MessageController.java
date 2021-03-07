package org.kajevand.messageboard.controller;

import org.kajevand.messageboard.entity.Message;
import org.kajevand.messageboard.service.MessageService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class MessageController {

    private MessageService messageService;

    private MessageModelAssembler assembler;

    public MessageController(MessageService messageService, MessageModelAssembler assembler) {
        this.messageService = messageService;
        this.assembler = assembler;
    }

    @GetMapping("/messages")
    public CollectionModel<EntityModel<Message>> findAll(){
        List<EntityModel<Message>> messages = messageService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(messages, linkTo(methodOn(MessageController.class).findAll()).withSelfRel());
    }

    @GetMapping("messages/{id}")
    public EntityModel<Message> findMessage(@Valid @PathVariable Long id){
        Message message = messageService.findMessageById(id);
        return assembler.toModel(message);
    }

    @DeleteMapping("messages/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id){
        messageService.deleteMessageById(id, getUser());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("messages")
    public ResponseEntity<?> addMessage(@RequestBody Message message){
        EntityModel<Message> model = assembler.toModel(messageService.saveMessage(message, getUser()));
        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @PutMapping("messages/{id}")
    public ResponseEntity<?> updateMessage(@RequestBody Message message, @PathVariable Long id){
        EntityModel<Message> model = assembler.toModel(messageService.updateMessageById(message, id, getUser()));
        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    private String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
