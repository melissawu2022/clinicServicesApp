package com.example.clinicservicesapp;

import android.widget.EditText;

import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;

import com.example.clinicservicesapp.EmployeeFeatures.CompleteProfile;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CompleteProfileTest {

    @Rule
    public ActivityTestRule<CompleteProfile> cProfileTestRule = new ActivityTestRule<CompleteProfile>(CompleteProfile.class);
    private CompleteProfile cProfile = null;
    private EditText text;

    @Before
    public void setUp() throws Exception{
        cProfile = cProfileTestRule.getActivity();
    }

    @Test
    @UiThreadTest
    public void checkAddress() throws Exception{
        text=cProfile.findViewById(R.id.editAddress);
        text.setText("21 Jump Street");
        String address = text.getText().toString();
        assertNotEquals("21 Jump",address);
    }

    @Test
    @UiThreadTest
    public void checkPostalCode() throws Exception{
        text=cProfile.findViewById(R.id.editPostal);
        text.setText("L6Y 4E6");
        String postal = text.getText().toString();
        assertEquals("Postal code is not equal", text.getText().toString(),postal);
    }

    @Test
    @UiThreadTest
    public void checkClincName() throws Exception{
        text = cProfile.findViewById(R.id.editClinic);
        text.setText("Bad bitches");
        String clincName = text.getText().toString();
        assertNotEquals("Bad itches", clincName);
    }
}
