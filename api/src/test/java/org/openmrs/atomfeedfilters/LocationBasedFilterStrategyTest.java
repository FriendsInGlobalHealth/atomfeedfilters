package org.openmrs.atomfeedfilters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.atomfeedfilters.Constants;
import org.openmrs.module.atomfeedfilters.LocationBasedFilterStrategy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 10/23/19.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocationBasedFilterStrategyTest {
    private static final String TEST_LOCATION_NAME = "Quelimane";

    @Mock
    private AdministrationService adminService;

    @InjectMocks
    private LocationBasedFilterStrategy locationBasedFilterStrategy = new LocationBasedFilterStrategy();

    @Before
    public void setup() {
        Mockito.when(adminService.getGlobalProperty(Constants.GP_LOCATION_NAME, Constants.ANY_NAME_LOCATION)).thenReturn(TEST_LOCATION_NAME);
    }

    @Test
    public void isTagValidShouldReturnTrueWhenTagIsValid() {
        assertTrue(locationBasedFilterStrategy.isFilterTagValid(TEST_LOCATION_NAME));
        assertTrue(locationBasedFilterStrategy.isFilterTagValid(TEST_LOCATION_NAME.toUpperCase()));
        assertFalse(locationBasedFilterStrategy.isFilterTagValid(null));
        assertFalse(locationBasedFilterStrategy.isFilterTagValid("not-same-as-set"));
    }
}
