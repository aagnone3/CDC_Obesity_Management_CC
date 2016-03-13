package edu.gatech.johndoe.carecoordinator;

import android.net.Uri;

public interface OnFragmentInteractionListener {
    void onSummaryFragmentInteraction(Uri uri);
    void onReferralFragmentInteraction(Uri uri);
    void onPatientFragmentInteraction(Uri uri);
    void onCommunityFragmentInteraction(CommunityListFragment fragment);
    void onCommunityDetailFragmentInteraction(Uri uri);
    void onShouldUpdateDetail(Object content);
}
