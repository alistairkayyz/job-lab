package com.dso34bt.jobportal.utilities;

import com.dso34bt.jobportal.model.CandidateAccount;
import com.dso34bt.jobportal.model.Recruiter;
import com.dso34bt.jobportal.model.User;

public final class Session {
    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        Session.user = user;
    }
}
