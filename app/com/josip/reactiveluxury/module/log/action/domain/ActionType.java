package com.josip.reactiveluxury.module.log.action.domain;

public enum ActionType
{
    CREATED("CREATED"),
    UPDATED("UPDATED");

    private ActionType(String displayName)
    {
        this.displayName = displayName;
    }
    private final String displayName;

    public String displayName()
    {
        return this.displayName;
    }
}
