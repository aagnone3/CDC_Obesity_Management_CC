package edu.gatech.johndoe.carecoordinator;

import java.util.List;

public interface Restorable<E> {
    List<E> getDataSet();
    int getSelectedPosition();
}
