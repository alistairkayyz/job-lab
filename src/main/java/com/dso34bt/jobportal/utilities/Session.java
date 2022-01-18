package com.dso34bt.jobportal.utilities;

import com.dso34bt.jobportal.model.CandidateAccount;
import com.dso34bt.jobportal.model.StaffAccount;

public final class Session {
    private static StaffAccount staffAccount;
    private static CandidateAccount candidateAccount;

    public static StaffAccount getStaffAccount() {
        return staffAccount;
    }

    public static void setStaffAccount(StaffAccount staffAccount) {
        Session.staffAccount = staffAccount;
    }

    public static CandidateAccount getCandidateAccount() {
        return candidateAccount;
    }

    public static void setCandidateAccount(CandidateAccount candidateAccount) {
        Session.candidateAccount = candidateAccount;
    }
}
