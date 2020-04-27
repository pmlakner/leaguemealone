package com.github.pmlakner.leaguemealone.service;

public interface ChatService<T, V> {
    public void configChatConnection(T t) throws Exception;
    public void updateStatus(V v);
}
