package org.fenixedu.bennu.user.management;

import java.util.Objects;

import jvstm.cps.ConsistencyPredicate;

import org.fenixedu.bennu.core.domain.User;
import org.joda.time.LocalDate;

/**
 * Represents a period in time in which the associated {@link User} is allowed to log in to the application.
 * 
 * Note that a user may have several active Periods at the same time, but can only have one open period (i.e. a period without a
 * end date).
 * 
 * @author João Carvalho (joao.pedro.carvalho@ist.utl.pt)
 * 
 */
public class UserLoginPeriod extends UserLoginPeriod_Base {

    /**
     * Creates a {@link UserLoginPeriod} for the given {@link User} with the exact dates.
     * 
     * All arguments are required.
     */
    public UserLoginPeriod(User user, LocalDate beginDate, LocalDate endDate) {
        setUser(Objects.requireNonNull(user));
        super.setBeginDate(Objects.requireNonNull(beginDate, "beginDate cannot be null"));
        super.setEndDate(Objects.requireNonNull(endDate, "endDate cannot be null"));
        computeUserExpiration(user);
    }

    /**
     * Edits this period. Changing the begin date is only allowed if the period is not already started, and changing the end date
     * is only allowed if such date is in the future.
     */
    public void edit(LocalDate beginDate, LocalDate endDate) {
        if (isClosed()) {
            throw UserManagementDomainException.cannotEditClosedLogin();
        }
        if (!getBeginDate().equals(beginDate) && isStarted()) {
            throw UserManagementDomainException.cannotEditOpenPeriodStartDate();
        }
        super.setBeginDate(beginDate);
        super.setEndDate(endDate);
        computeUserExpiration(getUser());
    }

    /**
     * Returns whether this period is already closed, i.e. its end date is in the past.
     */
    public boolean isClosed() {
        return getEndDate() != null && getEndDate().isBefore(new LocalDate());
    }

    /**
     * Returns whether this period has already started, i.e. its begin date is not in the future.
     */
    public boolean isStarted() {
        return !getBeginDate().isAfter(new LocalDate());
    }

    /**
     * Returns whether this period matches exactly the given dates.
     */
    public boolean matches(LocalDate beginDate, LocalDate endDate) {
        return getBeginDate().equals(beginDate) && Objects.equals(getEndDate(), endDate);
    }

    /**
     * Deletes this period. If the period is already started, it throws an exception.
     * 
     * @see org.fenixedu.bennu.user.management.UserLoginPeriod#isStarted()
     */
    public void delete() {
        if (isStarted()) {
            throw new UserManagementDomainException("cannot.delete.started.login.period");
        } else {
            setUser(null);
            deleteDomainObject();
        }
    }

    /**
     * Returns an open (i.e. without end date) period for the given {@link User}, or creates one with today's start date if
     * necessary.
     */
    public static UserLoginPeriod createOpenPeriod(User user) {
        UserLoginPeriod period = getOpenPeriod(user);
        return period == null ? new UserLoginPeriod(user) : period;
    }

    /**
     * Closes the open (i.e. without end date) period for the given {@link User} if it exists.
     */
    public static void closeOpenPeriod(User user) {
        UserLoginPeriod period = getOpenPeriod(user);
        if (period != null) {
            period.edit(period.getBeginDate(), new LocalDate());
        }
    }

    // Private API

    /**
     * @see org.fenixedu.bennu.user.management.UserLoginPeriod#edit(LocalDate, LocalDate)
     */
    @Override
    public void setBeginDate(LocalDate beginDate) {
        throw UserManagementDomainException.cannotOverwritePeriodDates();
    }

    /**
     * @see org.fenixedu.bennu.user.management.UserLoginPeriod#edit(LocalDate, LocalDate)
     */
    @Override
    public void setEndDate(LocalDate endDate) {
        throw UserManagementDomainException.cannotOverwritePeriodDates();
    }

    private UserLoginPeriod(User user) {
        setUser(user);
        super.setBeginDate(new LocalDate());

        // Open periods means the user has no expiration
        user.setExpiration(null);
    }

    @ConsistencyPredicate
    protected boolean checkDateInterval() {
        return getEndDate() == null || !getBeginDate().isAfter(getEndDate());
    }

    /*
     * Note that each user can only have at most one open period.
     */
    private static UserLoginPeriod getOpenPeriod(User user) {
        for (UserLoginPeriod loginPeriod : user.getLoginPeriodSet()) {
            if (loginPeriod.getEndDate() == null) {
                return loginPeriod;
            }
        }
        return null;
    }

    public static void computeUserExpiration(User user) {
        LocalDate latest = user.getExpiration();
        for (UserLoginPeriod period : user.getLoginPeriodSet()) {
            // If there is an open period, set the user's expiration to null (i.e. open)
            if (period.getEndDate() == null) {
                latest = null;
                break;
            }

            // If no expiration is defined, or the current expiration is before the period's end date,
            // set it as the expiration.
            if (latest == null || latest.isBefore(period.getEndDate())) {
                latest = period.getEndDate();
            }
        }
        user.setExpiration(latest);
    }
}
