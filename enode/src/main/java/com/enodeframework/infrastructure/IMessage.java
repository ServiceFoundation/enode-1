package com.enodeframework.infrastructure;

import java.util.Date;

public interface IMessage {
    String getId();

    void setId(String id);

    Date getTimestamp();

    void setTimestamp(Date timestamp);
}
