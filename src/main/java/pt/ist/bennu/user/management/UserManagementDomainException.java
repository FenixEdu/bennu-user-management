package pt.ist.bennu.user.management;

import javax.ws.rs.core.Response.Status;

import pt.ist.bennu.core.domain.exceptions.DomainException;

public class UserManagementDomainException extends DomainException {

    private static final long serialVersionUID = -8172373378345997535L;

    private static final String BUNDLE = "resources.UserManagementResources";

    protected UserManagementDomainException(String key, String... args) {
        super(BUNDLE, key, args);
    }

    protected UserManagementDomainException(Status status, String key, String... args) {
        super(status, BUNDLE, key, args);
    }

    protected UserManagementDomainException(Throwable cause, String key, String... args) {
        super(cause, BUNDLE, key, args);
    }

    protected UserManagementDomainException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, BUNDLE, key, args);
    }

    public static UserManagementDomainException cannotEditClosedLogin() {
        return new UserManagementDomainException("cannot.edit.closed.login");
    }

    public static UserManagementDomainException cannotEditOpenPeriodStartDate() {
        return new UserManagementDomainException("cannot.edit.open.period.start.date");
    }

    public static UserManagementDomainException cannotOverwritePeriodDates() {
        return new UserManagementDomainException("cannot.overwrite.period.dates");
    }

}
