package com.github.pmlakner.leaguemealone.model.chatproxy;


public interface ChatProxy<T, V> {
    public T configChatConnection(T t) throws Exception;
    public void updateStatus(V v);
}
