package org.kajevand.messageboard;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kajevand.messageboard.entity.Message;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.kajevand.messageboard.MockData.userOneAllMessages;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MessageControllerTest {


    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void initTests() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void shouldGet() throws Exception {

        mvc.perform(get("/messages/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.subject", is(userOneAllMessages().get(0).getSubject())))
                .andExpect(jsonPath("$.content", is(userOneAllMessages().get(0).getContent())))
                .andExpect(jsonPath("$.username", is(MockData.USER_ONE)));
    }

    @Test
    public void shouldGetAll() throws Exception {

        mvc.perform(get("/messages")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.messageList", hasSize(6)));
    }

    @Test
    public void shouldAddUpdateAndDelete() throws Exception {
        Message message = MockData.userOneAllMessages().get(2);
        byte[] json = toJson(message);
        MvcResult result =
        mvc.perform(post("/messages")
                .principal(mockPrincipal())
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        int id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        message.setSubject("subjectUpdated");
        json = toJson(message);
        mvc.perform(put("/messages/" + id)
                .principal(mockPrincipal())
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subject", is("subjectUpdated")))
                .andExpect(status().isCreated());

        mvc.perform(delete("/messages/" + id)
                .principal(mockPrincipal())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mvc.perform(get("/messages/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private Principal mockPrincipal() {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(MockData.USER_ONE);
        return mockPrincipal;
    }

    private byte[] toJson(Object r) throws Exception {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(r).getBytes();
    }
}