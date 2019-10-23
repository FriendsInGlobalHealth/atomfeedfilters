package org.openmrs.atomfeedfilters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.atomfeed.api.filter.FeedFilter;
import org.openmrs.module.atomfeed.api.service.XMLParseService;
import org.openmrs.module.atomfeedfilters.Constants;
import org.openmrs.module.atomfeedfilters.LocationBasedFilterStrategy;

import javax.xml.bind.JAXBException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 10/23/19.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocationBasedFilterStrategyTest {
    private static final String TEST_LOCATION_NAME = "Quelimane";

    @Mock
    private AdministrationService adminService;

    @Mock
    private XMLParseService xmlParseService;

    @InjectMocks
    private LocationBasedFilterStrategy locationBasedFilterStrategy = new LocationBasedFilterStrategy();

    @Before
    public void setup() throws JAXBException {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><something>" + TEST_LOCATION_NAME + "</something>";
        Mockito.when(adminService.getGlobalProperty(Constants.GP_LOCATION_NAME, Constants.ANY_NAME_LOCATION)).thenReturn(TEST_LOCATION_NAME);

        Mockito.when(xmlParseService.createXMLFromFeedFilter(Mockito.any(FeedFilter.class))).thenReturn(xml);
    }

    @Test
    public void isTagValidShouldReturnTrueWhenTagIsValid() {
        assertTrue(locationBasedFilterStrategy.isFilterTagValid(TEST_LOCATION_NAME));
        assertTrue(locationBasedFilterStrategy.isFilterTagValid(TEST_LOCATION_NAME.toUpperCase()));
        assertFalse(locationBasedFilterStrategy.isFilterTagValid(null));
        assertFalse(locationBasedFilterStrategy.isFilterTagValid("not-same-as-set"));
    }

    @Test
    public void createFilterFeedShouldReturnXmlStringWithCorrectValue() {
        Location location = new Location();
        location.setName(TEST_LOCATION_NAME);

        Encounter encounter = new Encounter();
        encounter.setLocation(location);

        assertTrue(locationBasedFilterStrategy.createFilterFeed(encounter).contains(location.getName()));

        encounter.setLocation(null);

        assertNull(locationBasedFilterStrategy.createFilterFeed(encounter));

        Obs obs = new Obs();
        obs.setLocation(location);
        assertTrue(locationBasedFilterStrategy.createFilterFeed(obs).contains(location.getName()));

        obs.setLocation(null);
        encounter.setLocation(location);
        obs.setEncounter(encounter);

        assertTrue(locationBasedFilterStrategy.createFilterFeed(obs).contains(location.getName()));

        obs.setEncounter(null);

        assertNull(locationBasedFilterStrategy.createFilterFeed(obs));

        Visit visit = new Visit();
        visit.setLocation(location);
        assertTrue(locationBasedFilterStrategy.createFilterFeed(visit).contains(location.getName()));

        visit.setLocation(null);
        assertNull(locationBasedFilterStrategy.createFilterFeed(visit));
    }
}
