package net.event;

/**
 * Copyright (C) 2021,2021/6/30, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class MessageEvent {

    public final Object[] message;

    public MessageEvent(Object... message) {
        this.message = message;
    }

    public Object[] getMessage() {
        return message;
    }
}
