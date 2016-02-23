package edu.gatech.johndoe.carecoordinator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Patient {
    private String first_name;
    private String last_name;
    private Date birth_date;
    private List<EHR> ehr;
    private boolean sortedByImport;

    public Patient(String first_name, String last_name) {
        this(first_name, last_name, null, new ArrayList<EHR>());
    }

    public Patient(String first_name, String last_name, Date birth_date) {
        this(first_name, last_name, birth_date, new ArrayList<EHR>());
    }

    public Patient(String first_name, String last_name, Date birth_date, List<EHR> ehr) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.birth_date = birth_date;
        this.ehr = ehr;
        this.sortedByImport = true;
    }

    public String getName_first() {
        return first_name.concat(" " + last_name);
    }

    public String getName_last() {
        return last_name.concat(", " + first_name);
    }

    public int getAge() {
        Calendar dob = Calendar.getInstance();
        dob.setTime(birth_date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
            age--;
        return age;
    }

    public String getBirthDate() {
        return new SimpleDateFormat("MM-dd-yyyy").format(birth_date);
    }

    public EHR getEHR(int index) {
        return ehr.get(index);
    }

    public EHR removeEHR(int index) {
        return ehr.remove(index);
    }

    public boolean isPending() {
        for (EHR e : ehr) {
            if (e.isPending()) {
                return true;
            }
        }
        return false;
    }
    public void addEHR(EHR ehr) {
        this.ehr.add(ehr);
        if (sortedByImport) {
            Collections.sort(this.ehr, new ComparatorImport());
        } else {
            Collections.sort(this.ehr, new ComparatorIssue());
        }
    }

    public List<EHR> getEHR_by_import() {
        Collections.sort(ehr, new ComparatorImport());
        sortedByImport = true;
        return ehr;
    }

    public List<EHR> getEHR_by_issue() {
        Collections.sort(ehr, new ComparatorIssue());
        sortedByImport = false;
        return ehr;
    }

    public class ComparatorImport implements Comparator<EHR> {
        @Override
        public int compare(EHR e1, EHR e2) {
            return e1.getDateOfimport().compareTo(e2.getDateOfimport());
        }
    }

    public class ComparatorIssue implements Comparator<EHR> {
        @Override
        public int compare(EHR e1, EHR e2) {
            return e1.getIssueDate().compareTo(e2.getIssueDate());
        }
    }

    public String toString() {
        return getName_first();
    }
}
