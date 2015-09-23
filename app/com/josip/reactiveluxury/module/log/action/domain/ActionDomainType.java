package com.josip.reactiveluxury.module.log.action.domain;

public enum ActionDomainType
{
    USER("USER"),
    ORGANIZATION_STRUCTURE("ORGANIZATION_STRUCTURE");

    private ActionDomainType(String displayName)
    {
        this.displayName = displayName;
    }
    private final String displayName;

    public String displayName()
    {
        return this.displayName;
    }
}
