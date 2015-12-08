package com.josip.reactiveluxury.core.messages;

public enum MessageType
{
    INFORMATION ("Information"),
    WARNING     ("Warning"),
    ERROR       ("Error");

    private MessageType(String displayName) {
        this.displayName = displayName;
    }
    private final String displayName;

    public String displayName() {
        return this.displayName;
    }

    public boolean isInformation() {
        return this == MessageType.INFORMATION;
    }

    public boolean isWarning() {
        return this == MessageType.WARNING;
    }

    public boolean isError() {
        return this == MessageType.ERROR;
    }
}
