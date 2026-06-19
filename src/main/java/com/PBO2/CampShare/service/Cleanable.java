package com.PBO2.CampShare.service;

import com.PBO2.CampShare.entity.Message;
import java.util.List;

public interface Cleanable {
    List<Message> getMessages();
    void hardDelete();
}
