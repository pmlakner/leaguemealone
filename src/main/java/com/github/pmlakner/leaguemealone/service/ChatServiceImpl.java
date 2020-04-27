package com.github.pmlakner.leaguemealone.service;

import com.github.pmlakner.leaguemealone.model.chatproxy.ChatProxy;

public class ChatServiceImpl<T, V> implements ChatService<T, V>{
    private ChatProxy<T, V> chatProxy;

    public ChatServiceImpl(ChatProxy<T, V> chatProxy) {
        this.chatProxy = chatProxy;
    }

    @Override
    public void configChatConnection(T t) throws Exception {
        chatProxy.configChatConnection(t);
    }

    @Override
    public void updateStatus(V v) {

    }
}
